package Biosidus.GestionDeFlota.domain.Asignaciones;

import java.time.LocalDateTime;
import java.util.List;

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
    @OneToOne
    @JoinColumn(name = "contexto",referencedColumnName = "id",nullable = false)
    private Asignacion contexto;
    @Column(name = "fecha_siniestro",nullable = false)
    private LocalDateTime fechaSiniestro;
    @Column(name = "descripcion",nullable = false)
    private String descripcion;
    @ElementCollection
    @CollectionTable(name = "prueba",joinColumns = @JoinColumn(name = "siniestro_id"))
    private List<String> pruebas;
}
