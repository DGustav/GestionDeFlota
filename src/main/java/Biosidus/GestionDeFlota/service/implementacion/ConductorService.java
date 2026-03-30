package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Empleados.Conductor;
import Biosidus.GestionDeFlota.repository.ConductorRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.ConductorServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConductorService implements ConductorServiceI {
    //@Autowired
    private ConductorRepositorio repositorio;

    @Override
    public Conductor saveConductor(Conductor conductor) {
        return repositorio.save(conductor);
    }

    @Override
    public List<Conductor> fetchConductorList() {
        return (List<Conductor>) repositorio.findAll();
    }

    @Override
    public void deleteConductor(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public Conductor findConductorById(Long id) {
        return repositorio.findById(id).orElse(null);
    }
}
