package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Empleados.Conductor;

import java.util.List;

public interface ConductorServiceI {
    Conductor saveConductor(Conductor conductor);
    List<Conductor> fetchConductorList();
    void deleteConductor(Long id);
    Conductor findConductorById(Long id);
}
