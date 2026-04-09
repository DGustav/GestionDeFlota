package Biosidus.GestionDeFlota.domain.vehiculo;
import java.time.LocalDateTime;
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
    @OneToOne
    @JoinColumn(name="vehiculo",referencedColumnName = "id",nullable = false)
    private Vehiculo vehiculo;
    @Column(name = "descripcion",nullable = false)
    private String descripcion;
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDateTime fechaDevolucion;
    @Column(name = "fecha_devolucion_estimada", nullable = false)
    private LocalDateTime fechaDevolucionEstimada;
    @Column(name = "ruta_comprobante", nullable = false)
    private String ruta_comprobante;
    @Column(name = "kilometraje_actual", nullable = false)
    private Integer kilometrajeActual;

    public void actualizarKms(Integer kilometros){
        this.kilometrajeActual += kilometros;
    }
}
