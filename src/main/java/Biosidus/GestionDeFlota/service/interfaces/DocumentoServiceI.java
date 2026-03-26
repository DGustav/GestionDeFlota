package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Documentos.Documento;
import java.util.List;

public interface DocumentoServiceI {
    Documento saveDocumento(Documento documento);
    List<Documento> fetchDocumentoList();
    void deleteDocumento(Long id);
    Documento findDocumentoById(Long id);

}
