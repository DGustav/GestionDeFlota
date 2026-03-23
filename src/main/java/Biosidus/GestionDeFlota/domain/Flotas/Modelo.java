package Biosidus.GestionDeFlota.domain.Flotas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Modelo")

public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "marca",nullable = false)
    private String marca;
    @Column(name = "año",nullable = false)
    private String ano;
}

