package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.repositorios.DocumentoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActualizadorEstadoDocumentosService {

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private ParametrosGlobalesService parametrosGlobalesService;

    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    public void actualizarEstadoDocumentos() {

        List<Documento> documentos = documentoService.listarDocumentos();

        Integer diasAnticipacion =
                parametrosGlobalesService.obtenerValorEntero(
                        "DIAS_ALERTA_VENCIMIENTO",
                        30
                );

        for (Documento doc : documentos) {
            doc.setEstadoDocumento(
                    doc.calcularEstado(diasAnticipacion)
            );
            documentoRepositorio.save(doc);
        }
    }
}
