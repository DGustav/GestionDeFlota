package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.domain.empleado.Usuario;
import Biosidus.GestionDeFlota.exceptions.UsuarioNoEncontradoException;
import Biosidus.GestionDeFlota.repositorios.UsuarioRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
      private final UsuarioRepositorio usuarioRepositorio;
      private final PasswordEncoder passwordEncoder;

      public UsuarioService(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
          this.usuarioRepositorio = usuarioRepositorio;
          this.passwordEncoder = passwordEncoder;
      }

    public List<Usuario> listar(){
        return usuarioRepositorio.findAll();
    }

    public Usuario guardar(Usuario usuario){
        return usuarioRepositorio.save(usuario);
    }

    public Usuario buscarPorId(Long id){
        return usuarioRepositorio.findById(id).orElse(null);
    }
    public Optional<Usuario> buscarPorNickname(String nickname){
        return Optional.ofNullable(usuarioRepositorio.findByNickname(nickname).orElse(null));
    }
    public void eliminar(Usuario usuario){
        usuarioRepositorio.delete(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuario){
        Usuario existente = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        existente.setNickname(usuario.getNickname());
        existente.setRol(usuario.getRol());
        existente.setPassword(usuario.getPassword());

        return usuarioRepositorio.save(existente);
    }


}
