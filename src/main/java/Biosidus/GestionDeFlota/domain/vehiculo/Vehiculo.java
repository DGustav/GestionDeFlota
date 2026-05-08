package Biosidus.GestionDeFlota.domain.vehiculo;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@Entity
@Table(name = "Vehiculo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Patente",nullable = false)
    private String patente;

    @ManyToOne
    @JoinColumn(name = "modelo_id",referencedColumnName = "id" )
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

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoVehiculo estado;

    @Column(name = "chasis")
    private String chasis;

    @Column(name = "motor")
    private String motor;

    @Column(name = "origen")
    @Enumerated(EnumType.STRING)
    private OrigenVehiculo origen;

    @Column(name = "propietario")
    @Enumerated(EnumType.STRING)
    private Propietario propietario;


}
