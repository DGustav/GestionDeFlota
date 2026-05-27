package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.DocumentoDTO;
import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.repositorios.ConductorRepositorio;
import Biosidus.GestionDeFlota.repositorios.DocumentoRepositorio;
import Biosidus.GestionDeFlota.repositorios.VehiculoRepositorio;
import Biosidus.GestionDeFlota.domain.documento.EstadoDocumento;
import Biosidus.GestionDeFlota.domain.documento.TipoDocumento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;

    @Autowired
    private ConductorRepositorio conductorRepositorio;

    @Value("${spring.servlet.multipart.location}")
    private String uploadLocation;

    @Transactional(readOnly = true)
    public Page<Documento> listarTodosLosDocumentos(int page, EstadoDocumento estado, TipoDocumento tipo, String buscar) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());
        if (estado != null || tipo != null || (buscar != null && !buscar.isEmpty())) {
            return documentoRepositorio.filtrarDocumentos(estado, tipo, buscar, pageable);
        }
        return documentoRepositorio.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Documento buscarPorId(Long id) {
        return documentoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
    }

    @Transactional(readOnly = true)
    public DocumentoDTO buscarDTOporId(Long id) {
        Documento documento = buscarPorId(id);
        DocumentoDTO dto = new DocumentoDTO();
        dto.setId(documento.getId());
        dto.setTipoDocumento(documento.getTipoDocumento());
        dto.setFechaVencimiento(documento.getFechaVencimiento());
        dto.setEstadoDocumento(documento.getEstadoDocumento());
        dto.setRuta_archivo(documento.getRuta_archivo());
        
        if (documento.getVehiculo() != null) {
            dto.setVehiculoId(documento.getVehiculo().getId());
            dto.setNombreVehiculo(documento.getVehiculo().getPatente() + " - " + documento.getVehiculo().getModelo().getNombreCompletoModelo());
        }
        
        if (documento.getConductor() != null) {
            dto.setConductorId(documento.getConductor().getId());
            dto.setNombreConductor(documento.getConductor().getNomApe());
        }
        
        return dto;
    }

    @Transactional
    public void guardarDocumento(DocumentoDTO dto) throws IOException {
        Documento documento;
        if (dto.getId() != null) {
            documento = documentoRepositorio.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        } else {
            documento = new Documento();
        }

        documento.setTipoDocumento(dto.getTipoDocumento());
        documento.setFechaVencimiento(dto.getFechaVencimiento());
        documento.setEstadoDocumento(dto.getEstadoDocumento());

        if (dto.getVehiculoId() != null) {
            Vehiculo vehiculo = vehiculoRepositorio.findById(dto.getVehiculoId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            documento.setVehiculo(vehiculo);
            documento.setConductor(null);
        } else if (dto.getConductorId() != null) {
            Conductor conductor = conductorRepositorio.findById(dto.getConductorId())
                    .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));
            documento.setConductor(conductor);
            documento.setVehiculo(null);
        } else {
            throw new RuntimeException("Debe seleccionar un vehículo o un conductor");
        }

        MultipartFile archivo = dto.getArchivo();
        if (archivo != null && !archivo.isEmpty()) {
            String originalFilename = archivo.getOriginalFilename();
            String nuevoNombre = UUID.randomUUID().toString() + "_" + originalFilename;
            String subCarpeta = "vtv_cedulas_registros";
            String rutaRelativa = "/" + subCarpeta + "/" + nuevoNombre;
            Path rutaCompleta = Paths.get(uploadLocation, subCarpeta, nuevoNombre);
            
            File directorio = rutaCompleta.getParent().toFile();
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            Files.copy(archivo.getInputStream(), rutaCompleta);
            documento.setRuta_archivo(rutaRelativa);
        } else if (dto.getId() == null) {
            throw new RuntimeException("El archivo del documento es obligatorio");
        }

        documentoRepositorio.save(documento);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> descargarArchivo(Long id) throws MalformedURLException {
        Documento documento = buscarPorId(id);
        String rutaRelativa = documento.getRuta_archivo();

        if (rutaRelativa == null || rutaRelativa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(uploadLocation, rutaRelativa);
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            String filename = path.getFileName().toString();
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
    public void eliminarDocumento(Long id) {
        documentoRepositorio.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Documento> listarDocumentosParaReporte(EstadoDocumento estado, TipoDocumento tipo, String buscar) {
        if (estado != null || tipo != null || (buscar != null && !buscar.isEmpty())) {
            // El repositorio devuelve Page, pero podemos pedir la primera página con un tamaño muy grande
            // o implementar un método que devuelva List en el repositorio.
            // Dado que filtrarDocumentos devuelve Page<Documento>, podemos usar un Pageable sin límite o muy grande.
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").descending());
            return documentoRepositorio.filtrarDocumentos(estado, tipo, buscar, pageable).getContent();
        }
        return documentoRepositorio.findAll(Sort.by("id").descending());
    }
}
