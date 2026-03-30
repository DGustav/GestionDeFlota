package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Flotas.Modelo;
import Biosidus.GestionDeFlota.repository.ModeloRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.ModeloServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeloService implements ModeloServiceI {
    //@Autowired
    private ModeloRepositorio repositorio;

    @Override
    public Modelo saveModelo(Modelo modelo) {
        return repositorio.save(modelo);
    }

    @Override
    public List<Modelo> fetchModeloList() {
        return (List<Modelo>) repositorio.findAll();
    }

    @Override
    public void deleteModelo(Long modeloId) {
        repositorio.deleteById(modeloId);
    }

    @Override
    public Modelo findModeloById(Long id) {
        return repositorio.findById(id).orElse(null);
    }
}
