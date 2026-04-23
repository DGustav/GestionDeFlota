package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConductorRepositorio extends JpaRepository<Conductor, Long> {
    Optional<Conductor> findByDni(String dni);
    Optional<Conductor> findByLegajo(String legajo);
}
