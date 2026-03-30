package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Asignaciones.Asignacion;
import Biosidus.GestionDeFlota.repository.AsignacionRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.AsignacionServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsignacionService implements AsignacionServiceI {
    //@Autowired
    private AsignacionRepositorio repositorio;

    @Override
    public Asignacion saveAsignacion(Asignacion asignacion) {
        return repositorio.save(asignacion);
    }

    @Override
    public List<Asignacion> fetchAsignacionloList() {
        return List.of();
    }

    @Override
    public void deleteAsignacion(Long id) {

    }

    @Override
    public Asignacion findAsignacionById(Long id) {
        return null;
    }
}
