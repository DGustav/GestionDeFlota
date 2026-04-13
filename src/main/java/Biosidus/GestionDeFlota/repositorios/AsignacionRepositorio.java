package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.empleado.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepositorio extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByConductor_Usuario_Nickname(String nickname);
}
