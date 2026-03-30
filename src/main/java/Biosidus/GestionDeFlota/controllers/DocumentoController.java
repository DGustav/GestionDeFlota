package Biosidus.GestionDeFlota.controllers;


import Biosidus.GestionDeFlota.domain.Documentos.Documento;
import Biosidus.GestionDeFlota.domain.Documentos.TipoDocumento;
import Biosidus.GestionDeFlota.service.implementacion.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DocumentoController {
    //@Autowired
    private DocumentoService service;

    public void crearDocumento(TipoDocumento tipo, String path, LocalDateTime fecha){
        Documento doc = new Documento();
        doc.setTipoDocumento(tipo);
        doc.setArchivo(path);
        doc.setFechaVencimiento(fecha);
        service.saveDocumento(doc);
    }
    public void eliminarUsuario(Long id){
        service.deleteDocumento(id);
    }

    public Documento buscarPorId(Long id){
        return service.findDocumentoById(id);
    }

    public List<Documento> buscarDocumento(){
        return service.fetchDocumentoList();
    }

}
