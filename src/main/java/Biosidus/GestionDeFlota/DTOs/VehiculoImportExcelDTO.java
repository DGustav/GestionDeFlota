package Biosidus.GestionDeFlota.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculoImportExcelDTO {
    private String propietario;
    private String origen;
    private String tipo;
    private String modeloNombre; // Columna "VEHICULO"
    private String anio;
    private String motor;
    private String patente;
    private String chasis;
    private String estadoHoja; // Para saber si viene de la hoja "Activos" o "Bajas"
}
