package Biosidus.GestionDeFlota.domain.Flotas;

import Biosidus.GestionDeFlota.domain.Documentos.Documento;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
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
    private List<Documento> documentos;
    @Column(name = "disponible",nullable = false)
    private Boolean disponible;
    @Column(name = "en_Service",nullable = false)
    private Boolean enService;
    @Column(name = "ultimo_service")
    private LocalDate ultimoService;
}
