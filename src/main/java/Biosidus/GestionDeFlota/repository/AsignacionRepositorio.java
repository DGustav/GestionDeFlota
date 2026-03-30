package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Asignaciones.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionRepositorio extends JpaRepository<Asignacion,Long> {
}
