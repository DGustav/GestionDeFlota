package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Empleados.Usuario;
import Biosidus.GestionDeFlota.repository.UsuarioRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.UsuarioServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UsuarioServiceI {
    //@Autowired
    private UsuarioRepositorio repositorio;

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        return repositorio.save(usuario);
    }

    @Override
    public List<Usuario> fetchUsuarioList() {
        return (List<Usuario>) repositorio.findAll();
    }

    @Override
    public Usuario findUsuarioById(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Override
    public void deleteUsuario(Long usuarioId) {
        repositorio.deleteById(usuarioId);
    }
    }
