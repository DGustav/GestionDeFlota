package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Documentos.Documento;
import Biosidus.GestionDeFlota.repository.DocumentoRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.DocumentoServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoService implements DocumentoServiceI {
    //@Autowired
    private DocumentoRepositorio repositorio;

    @Override
    public Documento saveDocumento(Documento documento) {
        return repositorio.save(documento);
    }

    @Override
    public List<Documento> fetchDocumentoList() {
        return (List<Documento>) repositorio.findAll();
    }

    @Override
    public void deleteDocumento(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public Documento findDocumentoById(Long id) {
        return repositorio.findById(id).orElse(null);
    }
}
