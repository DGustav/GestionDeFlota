package Biosidus.GestionDeFlota.domain.mantenimientoVehicular;
import java.time.LocalDateTime;

import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "MantenimientoVehicular")

public class MantenimientoVehicular {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="vehiculo_id",referencedColumnName = "id",nullable = false)
    private Vehiculo vehiculo;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDateTime fechaDevolucion;
    @Column(name = "fecha_devolucion_estimada")
    private LocalDateTime fechaDevolucionEstimada;
    @Column(name = "ruta_comprobante")
    private String ruta_comprobante;
    @Column(name = "kilometraje_actual", nullable = false)
    private Integer kilometrajeActual;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private Tipo tipo;

    public void actualizarKms(Integer kilometros){
        this.kilometrajeActual += kilometros;
    }
}
