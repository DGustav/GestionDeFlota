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
@Table(name = "Asignacion")

public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "vehiculo", referencedColumnName = "id", nullable = false)
    private Vehiculo vehiculo;
    @OneToOne
    @JoinColumn(name = "conductor", referencedColumnName = "id")
    private Conductor conductor;
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

    public void actualizarKms(Integer kilometros){
        this.kilometrosRecorridos += kilometros;
    }

};