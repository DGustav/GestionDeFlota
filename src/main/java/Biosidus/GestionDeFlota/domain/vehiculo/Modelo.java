package Biosidus.GestionDeFlota.domain.vehiculo;

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
    @Column(name = "nombreCompletoModelo",nullable = false)
    private String nombreCompletoModelo;

}



