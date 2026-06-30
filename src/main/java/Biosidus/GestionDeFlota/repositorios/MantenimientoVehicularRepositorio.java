package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.mantenimientoVehicular.MantenimientoVehicular;
import Biosidus.GestionDeFlota.domain.mantenimientoVehicular.Tipo;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MantenimientoVehicularRepositorio extends JpaRepository<MantenimientoVehicular, Long> {
    Page<MantenimientoVehicular> findByVehiculoModeloNombreCompletoModeloContainingIgnoreCaseOrVehiculoPatenteContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String modelo, String patente, String descripcion, Pageable pageable);



    @Query("SELECT m FROM MantenimientoVehicular m WHERE " +
           "(:vehiculo IS NULL OR m.vehiculo = :vehiculo) AND " +
           "(:fechaDesde IS NULL OR m.fechaAsignacion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR m.fechaAsignacion <= :fechaHasta) AND " +
           "(:fechaInicio IS NULL OR m.fechaAsignacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR m.fechaAsignacion <= :fechaFin) AND " +
           "(LOWER(m.vehiculo.modelo.nombreCompletoModelo) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.vehiculo.patente) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    Page<MantenimientoVehicular> filtrarPorRangoYBusqueda(@Param("vehiculo") Vehiculo vehiculo,
                                                         @Param("fechaDesde") LocalDateTime fechaDesde, 
                                                         @Param("fechaHasta") LocalDateTime fechaHasta, 
                                                         @Param("fechaInicio") LocalDateTime fechaInicio,
                                                         @Param("fechaFin") LocalDateTime fechaFin,
                                                         @Param("buscar") String buscar, 
                                                         Pageable pageable);

    @Query("SELECT m FROM MantenimientoVehicular m WHERE " +
           "(:vehiculo IS NULL OR m.vehiculo = :vehiculo) AND " +
           "(:fechaDesde IS NULL OR m.fechaAsignacion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR m.fechaAsignacion <= :fechaHasta) AND " +
           "(:fechaInicio IS NULL OR m.fechaAsignacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR m.fechaAsignacion <= :fechaFin)")
    Page<MantenimientoVehicular> filtrarPorRango(@Param("vehiculo") Vehiculo vehiculo,
                                                 @Param("fechaDesde") LocalDateTime fechaDesde, 
                                                 @Param("fechaHasta") LocalDateTime fechaHasta,
                                                 @Param("fechaInicio") LocalDateTime fechaInicio,
                                                 @Param("fechaFin") LocalDateTime fechaFin,
                                                 Pageable pageable);

    @Query("SELECT m FROM MantenimientoVehicular m WHERE " +
           "m.vehiculo = :vehiculo AND (" +
           "LOWER(m.vehiculo.modelo.nombreCompletoModelo) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.vehiculo.patente) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    Page<MantenimientoVehicular> buscarPorVehiculoYTermino(@Param("vehiculo") Vehiculo vehiculo, @Param("buscar") String buscar, Pageable pageable);

    Page<MantenimientoVehicular> findByVehiculo(Vehiculo vehiculo, Pageable pageable);

    List<MantenimientoVehicular> findByVehiculoModeloNombreCompletoModeloContainingIgnoreCaseOrVehiculoPatenteContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String modelo, String patente, String descripcion);

    @Query("SELECT m FROM MantenimientoVehicular m WHERE " +
           "(:vehiculo IS NULL OR m.vehiculo = :vehiculo) AND " +
           "(:fechaDesde IS NULL OR m.fechaAsignacion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR m.fechaAsignacion <= :fechaHasta) AND " +
           "(:fechaInicio IS NULL OR m.fechaAsignacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR m.fechaAsignacion <= :fechaFin) AND " +
           "(LOWER(m.vehiculo.modelo.nombreCompletoModelo) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.vehiculo.patente) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    List<MantenimientoVehicular> filtrarPorRangoYBusquedaSinPag(@Param("vehiculo") Vehiculo vehiculo,
                                                               @Param("fechaDesde") LocalDateTime fechaDesde, 
                                                               @Param("fechaHasta") LocalDateTime fechaHasta, 
                                                               @Param("fechaInicio") LocalDateTime fechaInicio,
                                                               @Param("fechaFin") LocalDateTime fechaFin,
                                                               @Param("buscar") String buscar);

    @Query("SELECT m FROM MantenimientoVehicular m WHERE " +
           "(:vehiculo IS NULL OR m.vehiculo = :vehiculo) AND " +
           "(:fechaDesde IS NULL OR m.fechaAsignacion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR m.fechaAsignacion <= :fechaHasta) AND " +
           "(:fechaInicio IS NULL OR m.fechaAsignacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR m.fechaAsignacion <= :fechaFin)")
    List<MantenimientoVehicular> filtrarPorRangoSinPag(@Param("vehiculo") Vehiculo vehiculo,
                                                       @Param("fechaDesde") LocalDateTime fechaDesde, 
                                                       @Param("fechaHasta") LocalDateTime fechaHasta,
                                                       @Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);

    List<MantenimientoVehicular> findByVehiculo(Vehiculo vehiculo);

    Optional<MantenimientoVehicular> findTopByVehiculoIdAndTipoAndFechaAsignacionIsNotNullOrderByFechaAsignacionDesc(Long vehiculoId, Tipo tipo);
}
