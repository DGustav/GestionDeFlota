package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionRepositorio extends JpaRepository<Asignacion, Long>, JpaSpecificationExecutor<Asignacion> {
}
