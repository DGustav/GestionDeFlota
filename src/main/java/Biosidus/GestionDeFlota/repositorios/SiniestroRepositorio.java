package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.siniestro.Siniestro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SiniestroRepositorio extends JpaRepository<Siniestro, Long>, JpaSpecificationExecutor<Siniestro> {
}
