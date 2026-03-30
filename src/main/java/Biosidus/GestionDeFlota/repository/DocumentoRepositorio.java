package Biosidus.GestionDeFlota.repository;

import Biosidus.GestionDeFlota.domain.Documentos.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepositorio extends JpaRepository<Documento,Long> {
}
