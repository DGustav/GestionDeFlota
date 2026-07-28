package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.documento.EstadoDocumento;
import Biosidus.GestionDeFlota.domain.documento.TipoDocumento;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {

    @Query("SELECT d FROM Documento d LEFT JOIN d.vehiculo v LEFT JOIN v.modelo m LEFT JOIN d.conductor c WHERE " +
           "(:estado IS NULL OR d.estadoDocumento = :estado) AND " +
           "(:tipo IS NULL OR d.tipoDocumento = :tipo) AND " +
           "(:buscar IS NULL OR :buscar = '' OR " +
           "LOWER(v.patente) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(m.nombreCompletoModelo) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(c.legajo) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(c.nomApe) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(CAST(d.tipoDocumento AS string)) LIKE LOWER(CONCAT('%', :buscar, '%')))")
    Page<Documento> filtrarDocumentos(@Param("estado") EstadoDocumento estado,
                                      @Param("tipo") TipoDocumento tipo,
                                      @Param("buscar") String buscar,
                                      Pageable pageable);

    List<Documento> findByVehiculoId(Long vehiculoId);
    List<Documento> findByConductorId(Long conductorId);

    List<Documento> findByFechaVencimientoLessThanEqual(LocalDate fechaLimite);

    List<Documento> findByEstadoDocumento(EstadoDocumento estadoDocumento);

    @Query("""
    SELECT DISTINCT c
    FROM Conductor c
    JOIN Documento d ON d.conductor = c
    WHERE d.tipoDocumento = :tipoDocumento
      AND (
            d.estadoDocumento = "VIGENTE"
            OR d.estadoDocumento = "POR_VENCER"
          )
""")
    List<Conductor> findConductoresDisponibles(
            @Param("tipoDocumento") TipoDocumento tipoDocumento);
}

