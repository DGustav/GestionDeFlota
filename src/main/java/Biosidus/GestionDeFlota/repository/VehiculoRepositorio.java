package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Flotas.Vehiculo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepositorio extends CrudRepository<Vehiculo,Long> {
}
