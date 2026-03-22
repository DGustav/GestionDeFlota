package Biosidus.GestionDeFlota.domain.Empleados;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.MediaSize;

@Setter
@Getter
@Entity
@Table(name = "Usuario")

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nickname",nullable = false)
    private String nickname;
    @Column(name = "contrasena",nullable = false)
    private String contrasena;
    @Enumerated(EnumType.STRING)
    @Column(name = "rango",nullable = false)
    private Rango rango;
}
