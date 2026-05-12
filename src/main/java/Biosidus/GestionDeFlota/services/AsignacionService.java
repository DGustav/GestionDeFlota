package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.AsignacionDTO;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.repositorios.AsignacionRepositorio;
import Biosidus.GestionDeFlota.repositorios.ConductorRepositorio;
import Biosidus.GestionDeFlota.repositorios.VehiculoRepositorio;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public Page<Asignacion> listarAsignaciones(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Long vehiculoId, Long conductorId, EstadoAsignacion estado, int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 5, Sort.by("id").descending());

        Specification<Asignacion> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (fechaDesde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaAsignacion"), fechaDesde));
            }

            if (fechaHasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaAsignacion"), fechaHasta));
            }

            if (vehiculoId != null) {
                predicates.add(cb.equal(root.get("vehiculo").get("id"), vehiculoId));
            }

            if (conductorId != null) {
                predicates.add(cb.equal(root.get("conductor").get("id"), conductorId));
            }

            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return asignacionRepositorio.findAll(spec, pageable);
    }

    @Transactional
    public void guardarAsignacion(AsignacionDTO asignacionDTO, BindingResult result) {
        if (asignacionDTO.getVehiculoId() == null || asignacionDTO.getConductorId() == null) {
            result.rejectValue("conductorId", "error.conductorId", "El conductor seleccionado no existe");
            result.rejectValue("vehiculoId", "error.vehiculoId", "El vehículo seleccionado no existe");
            throw new RuntimeException("El vehículo  o conductor seleccionado no existe");
        }

        Vehiculo vehiculo = vehiculoRepositorio.findById(asignacionDTO.getVehiculoId()).get();
        
        Conductor conductor = conductorRepositorio.findById(asignacionDTO.getConductorId()).get();

        Asignacion asignacion = new Asignacion();
        asignacion.setVehiculo(vehiculo);
        asignacion.setConductor(conductor);
        asignacion.setFechaAsignacion(asignacionDTO.getFechaAsignacion());
        asignacion.setFechaDevolucion(asignacionDTO.getFechaDevolucion());
        asignacion.setFechaDevolucionEstimada(asignacionDTO.getFechaDevolucionEstimada());
        asignacion.setEstado(asignacionDTO.getEstado());
        asignacion.setKilometrajeInicial(vehiculo.getKilometrajeActual());
        asignacion.getVehiculo().setDisponible(false);
        asignacionRepositorio.save(asignacion);
    }

    public List<Vehiculo> listarVehiculosDisponibles() {
        return vehiculoRepositorio.findByDisponibleTrue(); 
    }

    public List<Vehiculo> listarTodosLosVehiculos() {
        return vehiculoRepositorio.findAll();
    }
    
    public List<Conductor> listarConductores() {
        return conductorRepositorio.findAll();
    }
}
