package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.vehiculo.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepositorio extends JpaRepository<Modelo, Long> {
    Optional<Modelo> findByNombreCompletoModelo(String nombreCompletoModelo);
}
