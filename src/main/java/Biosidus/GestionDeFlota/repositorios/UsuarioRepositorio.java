package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.empleado.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNickname(String nickname);
}
