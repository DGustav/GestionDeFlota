package Biosidus.GestionDeFlota.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class VehiculoDTO {

    @NotBlank(message = "La patente es obligatoria")
    @Pattern(
            regexp = "^([a-zA-Z]{2}-?[0-9]{3}-?[a-zA-Z]{2}|[a-zA-Z]{3}-?[0-9]{3})$",
            message = "Formato de patente inválido. Ej: ABC123 O AB123CD"
    )
    private String patente;

    private Long modeloId;

    @NotBlank(message = "El nombre del modelo es obligatorio")
    private String nombreModelo;

    @NotNull(message = "Marque la fecha de adquisición del vehículo")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAdquisicion;

    @Min(value = 0, message = "El kilometraje no puede ser negativo")
    @NotNull(message = "El kilometraje es obligatorio")
    private Integer kilometrajeActual;

    @NotNull(message = "Debe marcar la disponibilidad del vehículo")
    private Boolean disponible;

    @NotNull(message = "Debe especificar si el vehículo es leasing o propio")
    private Boolean esLeasing;
}
