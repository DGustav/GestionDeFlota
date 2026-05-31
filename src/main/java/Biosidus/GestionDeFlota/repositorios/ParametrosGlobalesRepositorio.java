package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.configuracion.ParametroGlobal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametrosGlobalesRepositorio extends JpaRepository<ParametroGlobal, String> {
}
