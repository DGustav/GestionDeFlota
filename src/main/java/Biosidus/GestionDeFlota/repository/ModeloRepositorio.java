package Biosidus.GestionDeFlota.repository;


import Biosidus.GestionDeFlota.domain.Flotas.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloRepositorio extends JpaRepository<Modelo,Long> {
}
