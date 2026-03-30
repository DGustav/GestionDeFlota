package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Asignaciones.Siniestro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiniestroRepositorio extends JpaRepository<Siniestro,Long> {
}
