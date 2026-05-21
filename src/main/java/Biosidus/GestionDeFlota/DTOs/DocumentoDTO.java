package Biosidus.GestionDeFlota.DTOs;

import Biosidus.GestionDeFlota.domain.documento.EstadoDocumento;
import Biosidus.GestionDeFlota.domain.documento.TipoDocumento;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class DocumentoDTO {

    private Long id;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaVencimiento;

    private Long vehiculoId;
    private String nombreVehiculo;

    private Long conductorId;
    private String nombreConductor;

    @NotNull(message = "El estado del documento es obligatorio")
    private EstadoDocumento estadoDocumento;

    private MultipartFile archivo;
    private String ruta_archivo;
}
