package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Asignaciones.Siniestro;
import Biosidus.GestionDeFlota.repository.SiniestroRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.SiniestroServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiniestroService implements SiniestroServiceI {
    @Autowired
    private SiniestroRepositorio repositorio;

    @Override
    public Siniestro saveSiniestro(Siniestro siniestro) {
        return repositorio.save(siniestro);
    }

    @Override
    public List<Siniestro> fetchSiniestroList() {
        return List.of();
    }

    @Override
    public void deleteSiniestro(Long id) {

    }

    @Override
    public Siniestro findSiniestroById(Long id) {
        return null;
    }
}
