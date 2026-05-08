package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.AsignacionDTO;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.repositorios.AsignacionRepositorio;
import Biosidus.GestionDeFlota.repositorios.ConductorRepositorio;
import Biosidus.GestionDeFlota.repositorios.VehiculoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignacionService {

    @Autowired
    private AsignacionRepositorio asignacionRepositorio;

    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;

    @Autowired
    private ConductorRepositorio conductorRepositorio;

    public List<Asignacion> listarAsignaciones() {
        return asignacionRepositorio.findAll();
    }

    @Transactional
    public void guardarAsignacion(AsignacionDTO asignacionDTO) {
        Vehiculo vehiculo = vehiculoRepositorio.findById(asignacionDTO.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        Conductor conductor = conductorRepositorio.findById(asignacionDTO.getConductorId())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        Asignacion asignacion = new Asignacion();
        asignacion.setVehiculo(vehiculo);
        asignacion.setConductor(conductor);
        asignacion.setFechaAsignacion(asignacionDTO.getFechaAsignacion());
        asignacion.setFechaDevolucion(asignacionDTO.getFechaDevolucion());
        asignacion.setFechaDevolucionEstimada(asignacionDTO.getFechaDevolucionEstimada());
        asignacion.setEstado(asignacionDTO.getEstado());
        asignacion.setKilometrajeInicial(asignacionDTO.getKilometrajeInicial());
        asignacion.setKilometrajeFinal(asignacionDTO.getKilometrajeFinal() != null ? asignacionDTO.getKilometrajeFinal() : 0);
        asignacion.setKilometrosRecorridos(asignacionDTO.getKilometrosRecorridos() != null ? asignacionDTO.getKilometrosRecorridos() : 0);

        asignacionRepositorio.save(asignacion);
    }
    
    public List<Vehiculo> listarVehiculosDisponibles() {
        return vehiculoRepositorio.findByDisponibleTrue(); 
    }
    
    public List<Conductor> listarConductores() {
        return conductorRepositorio.findAll();
    }
}
