package Biosidus.GestionDeFlota.domain.vehiculo;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Vehiculo")

public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Patente",nullable = false)
    private String patente;
    @OneToOne
    @JoinColumn(name = "modelo",referencedColumnName = "id",nullable = false)
    private Modelo modelo;
    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;
    @Column(name = "kilometrajeActual")
    private Integer kilometrajeActual;
    @OneToMany
    @JoinColumn(name = "vehiculo_id",referencedColumnName = "id")
    private List<MantenimientoVehicular> services;
    @Column(name = "disponible",nullable = false)
    private Boolean disponible;
    @Column(name="esLeasing")
    private Boolean esLeasing;

}
