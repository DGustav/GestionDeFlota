package Biosidus.GestionDeFlota.domain.documento;

import java.time.LocalDate;

import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="documento")

public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated (EnumType.STRING)
    @Column(name="tipo_documento",nullable = false)
    private TipoDocumento tipoDocumento;
    @Column(name="fecha_vencimiento",nullable = false)
    private LocalDate fechaVencimiento;
    @Column(name="archivo",nullable = false)
    private String ruta_archivo;
    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;
    @ManyToOne
    @JoinColumn(name = "conductor_id")
    private Conductor conductor;
    @Enumerated (EnumType.STRING)
    @Column(name="estado_documento",nullable = false)
    private EstadoDocumento estadoDocumento;

    public Boolean esVehiculo(){
        return vehiculo != null;
    }

    public Boolean esConductor(){
        return conductor != null;
    }

    public EstadoDocumento calcularEstado(int diasAnticipacion) {

        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(diasAnticipacion);

        if (fechaVencimiento.isBefore(hoy)) {
            if (this.esVehiculo()){
                this.vehiculo.setDisponible(false);
            }
            return EstadoDocumento.VENCIDO;
        }

        if (
                fechaVencimiento.isEqual(hoy)
                        || fechaVencimiento.isBefore(fechaLimite)
        ) {
            if (this.esVehiculo()){
                this.vehiculo.setDisponible(true);
            }
            return EstadoDocumento.POR_VENCER;
        }

        if (this.esVehiculo()){
            this.vehiculo.setDisponible(true);
        }
        return EstadoDocumento.VIGENTE;
    }
}
