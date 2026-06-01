package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.siniestro.Siniestro;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiniestroRepositorio extends JpaRepository<Siniestro, Long>, JpaSpecificationExecutor<Siniestro> {

    List<Siniestro> findByVehiculo(Vehiculo vehiculo);
}
