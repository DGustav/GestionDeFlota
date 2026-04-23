package Biosidus.GestionDeFlota.DTOs;

import Biosidus.GestionDeFlota.domain.empleado.Rango;
import Biosidus.GestionDeFlota.domain.empleado.Rol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConductorUsuarioDTO {
    // Datos del Conductor
    private String nomApe;
    private String dni;
    private String legajo;
    private String mail;
    private Rango rango;

    // Datos del Usuario
    private String nickname;
    private String contrasena;
    private String verificacionContrasena;
    private Rol rol;
}
