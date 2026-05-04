package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculoRepositorio extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPatente(String patente);
    Page<Vehiculo> findByPatenteContainingIgnoreCaseOrModeloNombreCompletoModeloContainingIgnoreCase(String patente, String nombreModelo, Pageable pageable);
}
