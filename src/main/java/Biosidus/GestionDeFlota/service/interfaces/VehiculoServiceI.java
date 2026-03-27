package Biosidus.GestionDeFlota.service.interfaces;

import Biosidus.GestionDeFlota.domain.Flotas.Vehiculo;
import java.util.List;

public interface VehiculoServiceI {
    Vehiculo saveVehiculo(Vehiculo conductor);
    List<Vehiculo> fetchVehiculoList();
    void deleteVehiculo(Long id);
    Vehiculo findVehiculoById(Long id);

}
