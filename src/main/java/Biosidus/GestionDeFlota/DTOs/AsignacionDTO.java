package Biosidus.GestionDeFlota.DTOs;

import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class AsignacionDTO {

    private Long id;

    private Long vehiculoId;

    private String nombreVehiculo;

    private Long conductorId;

    private String nombreConductor;

    @NotNull(message = "La fecha de asignación es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaAsignacion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaDevolucion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaDevolucionEstimada;

    @NotNull(message = "El estado de la asignación es obligatorio")
    private EstadoAsignacion estado;

    private Integer kilometrajeInicial;

    private Integer kilometrajeFinal;

    private Integer kilometrosRecorridos;

    private MultipartFile documento_asignacion;
}
