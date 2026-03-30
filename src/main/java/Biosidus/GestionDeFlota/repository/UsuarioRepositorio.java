package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Empleados.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario,Long> {
}
