package Biosidus.GestionDeFlota.DTOs;

import Biosidus.GestionDeFlota.validations.NotEmptyFiles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class SiniestroDTO {
    
    private Long id;

    @NotNull(message = "Debe seleccionar un vehículo de la lista")
    private Long vehiculoId;

    private String nombreVehiculo; // Para el datalist

    @NotNull(message = "La fecha del siniestro es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaSiniestro;

    private String descripcion;

    @NotEmptyFiles(message = "Debe seleccionar al menos un archivo")
    private MultipartFile[] pruebasArchivos;
}
