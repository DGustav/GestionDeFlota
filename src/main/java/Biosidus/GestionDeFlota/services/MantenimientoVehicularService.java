package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.MantenimientoVehicularDTO;
import Biosidus.GestionDeFlota.domain.alerta.Alerta;
import Biosidus.GestionDeFlota.domain.alerta.EstadoAlerta;
import Biosidus.GestionDeFlota.domain.alerta.TipoAlerta;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import Biosidus.GestionDeFlota.domain.mantenimientoVehicular.Tipo;
import Biosidus.GestionDeFlota.domain.siniestro.Siniestro;
import Biosidus.GestionDeFlota.domain.vehiculo.EstadoVehiculo;
import Biosidus.GestionDeFlota.domain.mantenimientoVehicular.MantenimientoVehicular;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.repositorios.AsignacionRepositorio;
import Biosidus.GestionDeFlota.repositorios.MantenimientoVehicularRepositorio;
import Biosidus.GestionDeFlota.repositorios.VehiculoRepositorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MantenimientoVehicularService {

    @Autowired
    private MantenimientoVehicularRepositorio mantenimientoRepositorio;

    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;

    @Autowired
    private AsignacionRepositorio asignacionRepositorio;

    @Autowired
    private AlertaService alertaService;

    @Value("${spring.servlet.multipart.location}")
    private String uploadLocation;
    @Autowired
    private AsignacionService asignacionService;

    @Transactional(readOnly = true)
    public List<MantenimientoVehicular> listarTodos() {
        return mantenimientoRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Page<MantenimientoVehicular> listarMantenimientos(int page, String buscar, LocalDate fecha, LocalDate fechaDesde, LocalDate fechaHasta, Vehiculo vehiculo) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
        
        LocalDateTime desde = (fechaDesde != null) ? fechaDesde.atStartOfDay() : null;
        LocalDateTime hasta = (fechaHasta != null) ? fechaHasta.atTime(LocalTime.MAX) : null;
        LocalDateTime fechaInicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fechaFin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;

        if (desde != null || hasta != null || fecha != null) {
            if (buscar != null && !buscar.isEmpty()) {
                return mantenimientoRepositorio.filtrarPorRangoYBusqueda(vehiculo, desde, hasta, fechaInicio, fechaFin, buscar, pageable);
            } else {
                return mantenimientoRepositorio.filtrarPorRango(vehiculo, desde, hasta, fechaInicio, fechaFin, pageable);
            }
        }

        if (buscar != null && !buscar.isEmpty()) {
            if (vehiculo != null) {
                return mantenimientoRepositorio.buscarPorVehiculoYTermino(vehiculo, buscar, pageable);
            }
            return mantenimientoRepositorio.findByVehiculoModeloNombreCompletoModeloContainingIgnoreCaseOrVehiculoPatenteContainingIgnoreCaseOrDescripcionContainingIgnoreCase(buscar, buscar, buscar, pageable);
        }

        if (vehiculo != null) {
            return mantenimientoRepositorio.findByVehiculo(vehiculo, pageable);
        }
        return mantenimientoRepositorio.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<MantenimientoVehicular> listarMantenimientos(String buscar, LocalDate fecha, LocalDate fechaDesde, LocalDate fechaHasta, Vehiculo vehiculo) {
        LocalDateTime desde = (fechaDesde != null) ? fechaDesde.atStartOfDay() : null;
        LocalDateTime hasta = (fechaHasta != null) ? fechaHasta.atTime(LocalTime.MAX) : null;
        LocalDateTime fechaInicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fechaFin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;

        if (desde != null || hasta != null || fecha != null) {
            if (buscar != null && !buscar.isEmpty()) {
                return mantenimientoRepositorio.filtrarPorRangoYBusquedaSinPag(vehiculo, desde, hasta, fechaInicio, fechaFin, buscar);
            } else {
                return mantenimientoRepositorio.filtrarPorRangoSinPag(vehiculo, desde, hasta, fechaInicio, fechaFin);
            }
        }

        if (buscar != null && !buscar.isEmpty()) {
            // Aquí faltaría un findByVehiculoAnd... que devuelva List, pero por ahora simplificamos
            return mantenimientoRepositorio.findByVehiculoModeloNombreCompletoModeloContainingIgnoreCaseOrVehiculoPatenteContainingIgnoreCaseOrDescripcionContainingIgnoreCase(buscar, buscar, buscar);
        }
        
        if (vehiculo != null) {
            return mantenimientoRepositorio.findByVehiculo(vehiculo);
        }
        return mantenimientoRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public MantenimientoVehicular buscarPorId(Long id) {
        return mantenimientoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
    }

    @Transactional(readOnly = true)
    public MantenimientoVehicularDTO buscarDTOporId(Long id) {
        MantenimientoVehicular mantenimiento = buscarPorId(id);
        MantenimientoVehicularDTO dto = new MantenimientoVehicularDTO();
        dto.setId(mantenimiento.getId());
        dto.setVehiculoId(mantenimiento.getVehiculo().getId());
        dto.setNombreVehiculo(mantenimiento.getVehiculo().getPatente() + " - " + mantenimiento.getVehiculo().getModelo().getNombreCompletoModelo());
        dto.setKilometrajeActual(mantenimiento.getKilometrajeActual());
        dto.setTipo(mantenimiento.getTipo());
        dto.setDescripcion(mantenimiento.getDescripcion());
        dto.setFechaAsignacion(mantenimiento.getFechaAsignacion());
        dto.setFechaDevolucionEstimada(mantenimiento.getFechaDevolucionEstimada());
        dto.setFechaDevolucion(mantenimiento.getFechaDevolucion());
        return dto;
    }

    @Transactional
    public void guardarMantenimiento(MantenimientoVehicularDTO dto) throws IOException {
        MantenimientoVehicular mantenimiento;
        Vehiculo vehiculo = vehiculoRepositorio.findById(dto.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        if (dto.getId() != null) {

            mantenimiento = mantenimientoRepositorio.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        } else {
            mantenimiento = new MantenimientoVehicular();
        }


        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setKilometrajeActual(dto.getKilometrajeActual());
        mantenimiento.setTipo(dto.getTipo());
        mantenimiento.setDescripcion(dto.getDescripcion());
        mantenimiento.setFechaAsignacion(dto.getFechaAsignacion());
        mantenimiento.setFechaDevolucionEstimada(dto.getFechaDevolucionEstimada());
        mantenimiento.setFechaDevolucion(dto.getFechaDevolucion());

        MultipartFile comprobanteArchivo = dto.getComprobanteArchivo();
        if (comprobanteArchivo != null && !comprobanteArchivo.isEmpty()) {
            String originalFilename = comprobanteArchivo.getOriginalFilename();
            String nuevoNombre = UUID.randomUUID().toString() + "_" + originalFilename;
            String rutaRelativa = "/services/" + nuevoNombre;
            Path rutaCompleta = Paths.get(uploadLocation, "services", nuevoNombre);
            
            File directorio = rutaCompleta.getParent().toFile();
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            Files.copy(comprobanteArchivo.getInputStream(), rutaCompleta);
            mantenimiento.setRuta_comprobante(rutaRelativa);
        } else if (dto.getId() == null) {
            mantenimiento.setRuta_comprobante("");
        }

        if (dto.getTipo().equals(Tipo.SERVICE)){
            // El service inició (no se cargó la fecha de devolución)
            if (dto.getFechaDevolucion() == null) {
                vehiculo.setEstado(EstadoVehiculo.EN_SERVICE);
                vehiculo.setDisponible(false);

            }else {
                // El service ya finalizó (se cargó la fecha de devolución)
                vehiculo.setEstado(EstadoVehiculo.ACTIVO);
                vehiculo.setDisponible(true);

                List<TipoAlerta> tipos = List.of(
                        TipoAlerta.SERVICE_3_MESES,
                        TipoAlerta.SERVICE_6_MESES,
                        TipoAlerta.SERVICE_9_MESES,
                        TipoAlerta.SERVICE_VENCIDO
                );

                for(TipoAlerta tipo : tipos){

                    alertaService
                            .obtenerActivasPorVehiculoYTipo(
                                    vehiculo.getId(),
                                    tipo
                            )
                            .ifPresent(alerta -> {
                                alerta.setEstado(EstadoAlerta.RESUELTA);
                                alertaService.guardarAlerta(alerta);
                            });
                }
            }
        }


        mantenimientoRepositorio.save(mantenimiento);
        
        // Actualizar el kilometraje del vehículo si es mayor al actual
        Integer kilometrajeActual = dto.getKilometrajeActual();
        if (kilometrajeActual != null && (vehiculo.getKilometrajeActual() == null || kilometrajeActual > vehiculo.getKilometrajeActual())) {
           Asignacion asignacion = asignacionRepositorio.findFirstByVehiculoIdAndFechaDevolucionIsNullAndEstadoOrderByFechaAsignacionDesc(
                    vehiculo.getId(), EstadoAsignacion.ACTIVA
            ).get();
            asignacion.actualizarKms(kilometrajeActual);
            asignacion.getVehiculo().setKilometrajeActual(kilometrajeActual);
            asignacionRepositorio.save(asignacion);
        }

        vehiculoRepositorio.save(vehiculo);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> descargarComprobante(Long id) throws MalformedURLException {
        MantenimientoVehicular mantenimiento = buscarPorId(id);
        String rutaRelativa = mantenimiento.getRuta_comprobante();

        if (rutaRelativa == null || rutaRelativa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(uploadLocation, rutaRelativa);
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

    @Transactional
    public Optional<MantenimientoVehicular> ultimoServicePorVehiculoId(Long vehiculoId) {
        return mantenimientoRepositorio.findTopByVehiculoIdAndTipoAndFechaAsignacionIsNotNullOrderByFechaAsignacionDesc(vehiculoId, Tipo.SERVICE);
    }

}
