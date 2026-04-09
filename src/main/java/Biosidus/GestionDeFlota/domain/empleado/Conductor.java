package Biosidus.GestionDeFlota.domain.empleado;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="Conductor")

public class Conductor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nomApe")
    private String nomApe;
    @Column(name = "legajo")
    private String legajo;
    @Column(name = "dni")
    private String dni;
    @OneToOne
    @JoinColumn(name="usuario",referencedColumnName = "id",nullable = false)
    private Usuario usuario;
    @Column(name = "mail")
    private String mail;
    @Enumerated(EnumType.STRING)
    @Column(name = "area",nullable = false)
    private Area area;
}
