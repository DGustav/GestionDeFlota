package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.SiniestroDTO;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import Biosidus.GestionDeFlota.domain.siniestro.Siniestro;
import Biosidus.GestionDeFlota.repositorios.AsignacionRepositorio;
import Biosidus.GestionDeFlota.repositorios.SiniestroRepositorio;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SiniestroService {

    @Autowired
    private SiniestroRepositorio siniestroRepositorio;

    @Autowired
    private AsignacionRepositorio asignacionRepositorio;

    @Value("${spring.servlet.multipart.location}")
    private String uploadLocation;

    @Transactional(readOnly = true)
    public Page<Siniestro> listarSiniestros(String buscar, LocalDate fecha, LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {
        Specification<Siniestro> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (buscar != null && !buscar.isEmpty()) {
                String likePattern = "%" + buscar.toLowerCase() + "%";
                
                Join<Object, Object> contextoJoin = root.join("contexto", JoinType.LEFT);
                Join<Object, Object> vehiculoJoin = contextoJoin.join("vehiculo", JoinType.LEFT);
                Join<Object, Object> modeloJoin = vehiculoJoin.join("modelo", JoinType.LEFT);
                Join<Object, Object> conductorJoin = contextoJoin.join("conductor", JoinType.LEFT);

                Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(modeloJoin.get("nombreCompletoModelo")), likePattern),
                    cb.like(cb.lower(vehiculoJoin.get("patente")), likePattern),
                    cb.like(cb.lower(conductorJoin.get("nomApe")), likePattern),
                    cb.like(cb.lower(conductorJoin.get("legajo")), likePattern),
                    cb.like(cb.lower(conductorJoin.get("dni")), likePattern)
                );
                predicates.add(searchPredicate);
            }

            if (fecha != null) {
                LocalDateTime inicioDia = fecha.atStartOfDay();
                LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("fechaSiniestro"), inicioDia, finDia));
            }

            if (fechaDesde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaSiniestro"), fechaDesde.atStartOfDay()));
            }

            if (fechaHasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaSiniestro"), fechaHasta.atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return siniestroRepositorio.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Siniestro obtenerSiniestroPorId(Long id) {
        return siniestroRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el siniestro con ID: " + id));
    }

    @Transactional(readOnly = true)
    public SiniestroDTO obtenerSiniestroDTOPorId(Long id) {
        Siniestro siniestro = obtenerSiniestroPorId(id);
        SiniestroDTO dto = new SiniestroDTO();
        dto.setId(siniestro.getId());
        dto.setVehiculoId(siniestro.getContexto().getVehiculo().getId());
        dto.setNombreVehiculo(siniestro.getContexto().getVehiculo().getPatente() + " - " + siniestro.getContexto().getVehiculo().getModelo().getNombreCompletoModelo());
        dto.setFechaSiniestro(siniestro.getFechaSiniestro());
        dto.setDescripcion(siniestro.getDescripcion());
        // No cargamos los archivos de prueba en el DTO de esta forma, ya que son MultipartFile en el DTO para subida
        return dto;
    }

    @Transactional
    public void actualizarSiniestro(SiniestroDTO dto) throws IOException {
        Siniestro siniestro = obtenerSiniestroPorId(dto.getId());
        
        // El vehículo no se suele cambiar en una modificación de siniestro según la UI estática, 
        // pero si se permitiera, deberíamos validar la asignación de nuevo.
        // Por ahora, actualizamos fecha y descripción.
        siniestro.setFechaSiniestro(dto.getFechaSiniestro());
        siniestro.setDescripcion(dto.getDescripcion());

        if (dto.getPruebasArchivos() != null) {
            List<String> rutasNuevas = new ArrayList<>();
            for (MultipartFile archivo : dto.getPruebasArchivos()) {
                if (archivo != null && !archivo.isEmpty()) {
                    String originalFilename = archivo.getOriginalFilename();
                    String nuevoNombre = UUID.randomUUID().toString() + "_" + originalFilename;
                    String rutaRelativa = "/siniestros/" + nuevoNombre;
                    Path rutaCompleta = Paths.get(uploadLocation, "siniestros", nuevoNombre);
                    
                    File directorio = rutaCompleta.getParent().toFile();
                    if (!directorio.exists()) {
                        directorio.mkdirs();
                    }

                    Files.copy(archivo.getInputStream(), rutaCompleta);
                    rutasNuevas.add(rutaRelativa);
                }
            }
            if (!rutasNuevas.isEmpty()) {
                siniestro.getPruebas().addAll(rutasNuevas);
            }
        }

        siniestroRepositorio.save(siniestro);
    }


    @Transactional
    public void guardarSiniestro(SiniestroDTO dto) throws IOException {
        Siniestro siniestro = new Siniestro();
        
        // Buscar la última asignación para el vehículo seleccionado
        // Intentamos buscar una activa, si no hay, la última finalizada.
        
        Specification<Asignacion> spec = (root, query, cb) -> {
            return cb.equal(root.get("vehiculo").get("id"), dto.getVehiculoId());
        };
        
        List<Asignacion> asignaciones = asignacionRepositorio.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
        if (asignaciones.isEmpty()) {
            throw new RuntimeException("No se encontró una asignación para el vehículo seleccionado. El siniestro debe estar vinculado a una asignación.");
        }
        
        // Priorizar asignación ACTIVA
        Asignacion contexto = asignaciones.stream()
                .filter(a -> a.getEstado() == EstadoAsignacion.ACTIVA)
                .findFirst()
                .orElse(asignaciones.get(0)); // Si no hay activa, tomar la última (que será la primera de la lista por el sort DESC)

        siniestro.setContexto(contexto);
        siniestro.setFechaSiniestro(dto.getFechaSiniestro());
        siniestro.setDescripcion(dto.getDescripcion());

        List<String> rutasPruebas = new ArrayList<>();
        if (dto.getPruebasArchivos() != null) {
            for (MultipartFile archivo : dto.getPruebasArchivos()) {
                if (archivo != null && !archivo.isEmpty()) {
                    String originalFilename = archivo.getOriginalFilename();
                    String nuevoNombre = UUID.randomUUID().toString() + "_" + originalFilename;
                    String rutaRelativa = "/siniestros/" + nuevoNombre;
                    Path rutaCompleta = Paths.get(uploadLocation, "siniestros", nuevoNombre);
                    
                    File directorio = rutaCompleta.getParent().toFile();
                    if (!directorio.exists()) {
                        directorio.mkdirs();
                    }

                    Files.copy(archivo.getInputStream(), rutaCompleta);
                    rutasPruebas.add(rutaRelativa);
                }
            }
        }
        siniestro.setPruebas(rutasPruebas);

        siniestroRepositorio.save(siniestro);
    }
    
    
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> descargarPrueba(String rutaRelativa) throws java.net.MalformedURLException {
        if (rutaRelativa == null || rutaRelativa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // La rutaRelativa suele empezar con /siniestros/, quitamos el primer slash si existe para Paths.get
        String rutaLimpia = rutaRelativa.startsWith("/") ? rutaRelativa.substring(1) : rutaRelativa;
        Path path = Paths.get(uploadLocation, rutaLimpia);
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            String filename = path.getFileName().toString();
            // Intentar remover el UUID del nombre para la descarga
            if (filename.contains("_")) {
                filename = filename.substring(filename.indexOf("_") + 1);
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
