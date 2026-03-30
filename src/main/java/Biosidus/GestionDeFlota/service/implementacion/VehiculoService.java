package Biosidus.GestionDeFlota.service.implementacion;

import Biosidus.GestionDeFlota.domain.Flotas.Vehiculo;
import Biosidus.GestionDeFlota.repository.VehiculoRepositorio;
import Biosidus.GestionDeFlota.service.interfaces.VehiculoServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService implements VehiculoServiceI {
    //@Autowired
    private VehiculoRepositorio repositorio;

    @Override
    public Vehiculo saveVehiculo(Vehiculo vehiculo) {
        return saveVehiculo(vehiculo);
    }

    @Override
    public List<Vehiculo> fetchVehiculoList() {
        return (List<Vehiculo>) repositorio.findAll();
    }

    @Override
    public void deleteVehiculo(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public Vehiculo findVehiculoById(Long id) {
        return repositorio.findById(id).orElse(null);
    }
}
