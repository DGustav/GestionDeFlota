package Biosidus.GestionDeFlota.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class MantenimientoVehicularDTO {
    
    private Long id;

    @NotNull(message = "El vehículo es obligatorio")
    private Long vehiculoId;

    private String nombreVehiculo;

    @NotNull(message = "El kilometraje es obligatorio")
    @Min(value = 0, message = "El kilometraje no puede ser negativo")
    private Integer kilometrajeActual;

    private String descripcion;

    @NotNull(message = "La fecha de asignación es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaAsignacion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaDevolucionEstimada;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaDevolucion;

    private MultipartFile comprobanteArchivo;

}
