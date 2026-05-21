package Biosidus.GestionDeFlota.repositorios;

import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.documento.EstadoDocumento;
import Biosidus.GestionDeFlota.domain.documento.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
