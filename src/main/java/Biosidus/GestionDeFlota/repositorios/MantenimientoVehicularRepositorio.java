package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.vehiculo.MantenimientoVehicular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MantenimientoVehicularRepositorio extends JpaRepository<MantenimientoVehicular, Long> {
    Page<MantenimientoVehicular> findByVehiculoModeloNombreCompletoModeloContainingIgnoreCaseOrVehiculoPatenteContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String modelo, String patente, String descripcion, Pageable pageable);

    @Query("SELECT m FROM MantenimientoVehicular m WHERE " +
           "(:fechaDesde IS NULL OR m.fechaAsignacion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR m.fechaAsignacion <= :fechaHasta) AND " +
           "(:fechaInicio IS NULL OR m.fechaAsignacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR m.fechaAsignacion <= :fechaFin) AND " +
           "(LOWER(m.vehiculo.modelo.nombreCompletoModelo) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.vehiculo.patente) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    Page<MantenimientoVehicular> filtrarPorRangoYBusqueda(@Param("fechaDesde") LocalDateTime fechaDesde, 
                                                         @Param("fechaHasta") LocalDateTime fechaHasta, 
                                                         @Param("fechaInicio") LocalDateTime fechaInicio,
                                                         @Param("fechaFin") LocalDateTime fechaFin,
                                                         @Param("buscar") String buscar, 
                                                         Pageable pageable);

    @Query("SELECT m FROM MantenimientoVehicular m WHERE " +
           "(:fechaDesde IS NULL OR m.fechaAsignacion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR m.fechaAsignacion <= :fechaHasta) AND " +
           "(:fechaInicio IS NULL OR m.fechaAsignacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR m.fechaAsignacion <= :fechaFin)")
    Page<MantenimientoVehicular> filtrarPorRango(@Param("fechaDesde") LocalDateTime fechaDesde, 
                                                 @Param("fechaHasta") LocalDateTime fechaHasta,
                                                 @Param("fechaInicio") LocalDateTime fechaInicio,
                                                 @Param("fechaFin") LocalDateTime fechaFin,
                                                 Pageable pageable);
}
