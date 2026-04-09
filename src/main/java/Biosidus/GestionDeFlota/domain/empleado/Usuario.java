package Biosidus.GestionDeFlota.domain.empleado;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Usuario")

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nickname",nullable = false, unique = true)
    private String nickname;
    @Column(name = "contrasena",nullable = false)
    private String contrasena;
    @Enumerated(EnumType.STRING)
    @Column(name = "rol",nullable = false)
    private Rol rol;
}

