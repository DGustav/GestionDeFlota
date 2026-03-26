package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Flotas.Service;

import java.util.List;

public interface ServiceServiceI {
    Service saveService(Service service);
    List<Service> fetchServiceList();
    void deleteService(Long id);
    Service findServiceById(Long id);
}
