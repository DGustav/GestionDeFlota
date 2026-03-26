package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Flotas.Modelo;
import java.util.List;

public interface ModeloServiceI {
    Modelo saveModelo(Modelo modelo);
    List<Modelo> fetchModeloList();
    void deleteModelo(Long id);
    Modelo findModeloById(Long id);
}
