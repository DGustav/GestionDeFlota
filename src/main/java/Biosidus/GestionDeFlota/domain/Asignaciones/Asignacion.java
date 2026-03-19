package Biosidus.GestionDeFlota.domain.Asignaciones;

import Biosidus.GestionDeFlota.domain.Empleados.Conductor;
import Biosidus.GestionDeFlota.domain.Flotas.Service;
import Biosidus.GestionDeFlota.domain.Flotas.Vehiculo;

import java.time.LocalDateTime;

public class Asignacion {
    //@ID
    //private Long id;
    private Vehiculo vehiculo;
    private Conductor empleado;
    private Service service;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDevolucion;
    private LocalDateTime fechaDevolucionEstimada;
    private EstadoAsignacion estado;
    private Integer kilometrajeInicial;
    private Integer kilometrajeFinal;
    private Integer kilometrosRecorridos;
};

/*private void actualizarVehiculo(){

};

private void verificarAsignacion(){

};*/
