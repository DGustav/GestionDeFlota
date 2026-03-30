package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Flotas.Service;
import Biosidus.GestionDeFlota.repository.ServiceRepositorio;
import Biosidus.GestionDeFlota.repository.UsuarioRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.ServiceServiceI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service

public class ServiceService implements ServiceServiceI {
    //@Autowired
    private ServiceRepositorio repositorio;

    @Override
    public Service saveService(Service service) {
        return repositorio.save(service);
    }

    @Override
    public List<Service> fetchServiceList() {
        return (List<Service>) repositorio.findAll();
    }

    @Override
    public Service findServiceById(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Override
    public void deleteService(Long id) {
        repositorio.deleteById(id);
    }

}
