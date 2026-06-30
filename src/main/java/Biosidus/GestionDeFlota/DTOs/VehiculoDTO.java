package Biosidus.GestionDeFlota.DTOs;

import Biosidus.GestionDeFlota.domain.vehiculo.EstadoVehiculo;
import Biosidus.GestionDeFlota.domain.vehiculo.OrigenVehiculo;
import Biosidus.GestionDeFlota.domain.vehiculo.Propietario;
import Biosidus.GestionDeFlota.domain.vehiculo.TipoVehiculo;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "El estado del vehículo es obligatorio")
    private EstadoVehiculo estado;

    private String chasis;

    private String motor;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 0, message = "El año no puede ser negativo")
    @Max(value = 9999, message = "El año no puede tener más de 4 dígitos")
    private Integer anio;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "El origen del vehículo es obligatorio")
    private OrigenVehiculo origen;

    @NotNull(message = "El propietario del vehículo es obligatorio")
    private Propietario propietario;

    private String nota;
}
