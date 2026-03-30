package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Flotas.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepositorio extends JpaRepository<Service,Long> {
}
