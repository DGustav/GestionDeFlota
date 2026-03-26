package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Empleados.Usuario;

import java.util.List;

public interface UsuarioServiceI {
    Usuario saveUsuario(Usuario usuario);
    List<Usuario> fetchUsuarioList();
    void deleteUsuario(Long id);
    Usuario findUsuarioById(Long id);
}
