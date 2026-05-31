package Biosidus.GestionDeFlota.domain.alerta;

import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "alerta")
@Setter
@Getter
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descripcion",nullable = true)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoAlerta estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoAlerta tipo;

    @Column(name = "fechaEnvio",nullable = false)
    private LocalDateTime fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", referencedColumnName = "id")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "documento_id", referencedColumnName = "id")
    private Documento documento;
}
