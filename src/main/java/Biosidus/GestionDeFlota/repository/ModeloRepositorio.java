package Biosidus.GestionDeFlota.repository;


import Biosidus.GestionDeFlota.domain.Flotas.Modelo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloRepositorio extends CrudRepository<Modelo,Long> {
}
