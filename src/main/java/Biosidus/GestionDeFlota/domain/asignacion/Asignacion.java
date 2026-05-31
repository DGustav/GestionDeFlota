package Biosidus.GestionDeFlota.domain.asignacion;

import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "asignacion")

public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "vehiculo_id", referencedColumnName = "id")
    private Vehiculo vehiculo;
    @ManyToOne
    @JoinColumn(name = "conductor_id", referencedColumnName = "id")
    private Conductor conductor;
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;
    @Column(name = "fecha_devolucion_estimada")
    private LocalDateTime fechaDevolucionEstimada;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado",nullable = false)
    private EstadoAsignacion estado;
    @Column(name = "kilometraje_inicial")
    private Integer kilometrajeInicial;
    @Column(name = "kilometraje_final")
    private Integer kilometrajeFinal;
    @Column(name = "kilometros_recorridos")
    private Integer kilometrosRecorridos;

    public void actualizarKms(Integer kilometrajeActual){

        this.kilometrosRecorridos = kilometrajeActual - this.kilometrajeInicial;
        this.kilometrajeFinal = kilometrajeActual;
    }

    public void finalizarAsignacion(){
        this.fechaDevolucion = LocalDateTime.now();
        this.estado = EstadoAsignacion.FINALIZADA;
    }

};