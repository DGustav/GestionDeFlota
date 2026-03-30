package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Empleados.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConductorRepositorio extends JpaRepository<Conductor,Long> {
}
