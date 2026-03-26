package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Flotas.Service;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepositorio extends CrudRepository<Service,Long>{
}
