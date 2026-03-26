package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.domain.Empleados.Rango;
import Biosidus.GestionDeFlota.domain.Empleados.Usuario;
import Biosidus.GestionDeFlota.service.implementacion.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController

public class UsuarioController {
    @Autowired
    private UsuarioService service;

    public void crearUsuario(String nick, String cont, Rango rango){
        Usuario usuario = new Usuario();
        usuario.setNickname(nick);
        usuario.setContrasena(cont);
        usuario.setRango(rango);
        service.saveUsuario(usuario);
    }

    public void eliminarUsuario(Long id){
        service.deleteUsuario(id);
    }

    public Usuario buscarPorId(Long id){
        return service.findUsuarioById(id);
    }

    public List<Usuario> buscarUsuarios(){
        return service.fetchUsuarioList();
    }
}
