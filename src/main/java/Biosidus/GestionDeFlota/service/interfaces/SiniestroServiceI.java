package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Asignaciones.Siniestro;

import java.util.List;

public interface SiniestroServiceI {
    Siniestro saveSiniestro(Siniestro siniestro);
    List<Siniestro> fetchSiniestroList();
    void deleteSiniestro(Long id);
    Siniestro findSiniestroById(Long id);
}
