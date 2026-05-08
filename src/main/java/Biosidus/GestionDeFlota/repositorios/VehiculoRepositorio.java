package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepositorio extends JpaRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {
    Optional<Vehiculo> findByPatente(String patente);
    Page<Vehiculo> findByPatenteContainingIgnoreCaseOrModeloNombreCompletoModeloContainingIgnoreCase(String patente, String nombreModelo, Pageable pageable);
    List<Vehiculo> findByDisponibleTrue();
}
