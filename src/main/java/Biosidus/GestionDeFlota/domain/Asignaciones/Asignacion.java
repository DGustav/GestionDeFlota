package Biosidus.GestionDeFlota.domain.Asignaciones;

import Biosidus.GestionDeFlota.domain.Empleados.Conductor;
import Biosidus.GestionDeFlota.domain.Flotas.Service;
import Biosidus.GestionDeFlota.domain.Flotas.Vehiculo;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Asignacion")

public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "vehiculo", referencedColumnName = "id", nullable = false)
    private Vehiculo vehiculo;
    @OneToOne
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    private Conductor empleado;
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDateTime fechaDevolucion;
    @Column(name = "fecha_devolucion_estimada", nullable = false)
    private LocalDateTime fechaDevolucionEstimada;
    @Enumerated(EnumType.STRING)
    private EstadoAsignacion estado;
    @Column(name = "kilometraje_inicial", nullable = false)
    private Integer kilometrajeInicial;
    @Column(name = "kilometraje_final", nullable = false)
    private Integer kilometrajeFinal;
    @Column(name = "kilometros_recorridos", nullable = false)
    private Integer kilometrosRecorridos;


    public void actualizarVehiculo() {
        this.vehiculo.setKilometrajeActual(this.kilometrajeFinal);
        if(estado!=EstadoAsignacion.FINALIZADA){
            this.vehiculo.setDisponible(Boolean.FALSE);
        }
        else{
            this.vehiculo.setDisponible(Boolean.TRUE);
        }
    };

    public void verificarAsignacion() {

    };

};