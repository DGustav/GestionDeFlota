package Biosidus.GestionDeFlota.domain.Flotas;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Service")

public class Service {
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
    @Column(name = "comprobante", nullable = false)
    private String comprobante;
}
