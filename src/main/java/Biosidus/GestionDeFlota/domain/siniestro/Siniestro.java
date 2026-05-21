package Biosidus.GestionDeFlota.domain.siniestro;

import java.time.LocalDateTime;
import java.util.List;

import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Siniestro")

public class Siniestro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "asignacion_id",referencedColumnName = "id")
    private Asignacion contexto;
    @Column(name = "fecha_siniestro",nullable = false)
    private LocalDateTime fechaSiniestro;
    @Column(name = "descripcion")
    private String descripcion;
    @ElementCollection
    @CollectionTable(name = "prueba",joinColumns = @JoinColumn(name = "siniestro_id"))
    private List<String> pruebas;
}
