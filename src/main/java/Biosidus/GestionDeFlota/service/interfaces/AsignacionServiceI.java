package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Asignaciones.Asignacion;
import Biosidus.GestionDeFlota.domain.Flotas.Vehiculo;

import java.util.List;

public interface AsignacionServiceI {
    Asignacion saveAsignacion(Asignacion asignacion);
    List<Asignacion> fetchAsignacionloList();
    void deleteAsignacion(Long id);
    Asignacion findAsignacionById(Long id);
}
