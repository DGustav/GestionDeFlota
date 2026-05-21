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
import java.time.LocalTime;
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

    @Transactional(readOnly = true)
    public Page<Asignacion> listarAsignaciones(String buscar, LocalDateTime fechaDesde, LocalDateTime fechaHasta, LocalDateTime fechaParticular, Long vehiculoId, Long conductorId, EstadoAsignacion estado, int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 5, Sort.by("id").descending());

        Specification<Asignacion> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (buscar != null && !buscar.isEmpty()) {
                String likePattern = "%" + buscar.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.join("vehiculo").get("patente")), likePattern),
                        cb.like(cb.lower(root.join("vehiculo").join("modelo").get("nombreCompletoModelo")), likePattern),
                        cb.like(cb.lower(root.join("conductor").get("nomApe")), likePattern),
                        cb.like(cb.lower(root.join("conductor").get("legajo")), likePattern),
                        cb.like(cb.lower(root.join("conductor").get("dni")), likePattern)
                ));
            }

            if (fechaDesde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaAsignacion"), fechaDesde));
            }

            if (fechaHasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaAsignacion"), fechaHasta));
            }

            if (fechaParticular != null) {
                LocalDateTime startOfDay = fechaParticular.toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = fechaParticular.toLocalDate().atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("fechaAsignacion"), startOfDay, endOfDay));
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
            if (asignacionDTO.getVehiculoId() == null) result.rejectValue("vehiculoId", "error.vehiculoId", "El vehículo seleccionado no existe");
            if (asignacionDTO.getConductorId() == null) result.rejectValue("conductorId", "error.conductorId", "El conductor seleccionado no existe");
            throw new RuntimeException("El vehículo o conductor seleccionado no existe");
        }

        Vehiculo vehiculo = vehiculoRepositorio.findById(asignacionDTO.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        Conductor conductor = conductorRepositorio.findById(asignacionDTO.getConductorId())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        Asignacion asignacion = new Asignacion();
        mapearDtoAEntidad(asignacionDTO, asignacion, vehiculo, conductor);
        
        // Regla de negocio específica para creación: el kilometraje inicial es el actual del vehículo
        asignacion.setKilometrajeInicial(vehiculo.getKilometrajeActual());
        vehiculo.setDisponible(false);
        
        asignacionRepositorio.save(asignacion);
    }

    @Transactional
    public void actualizarAsignacion(AsignacionDTO asignacionDTO, BindingResult result) {
        if (asignacionDTO.getId() == null) {
            throw new RuntimeException("ID de asignación no proporcionado");
        }

        if (asignacionDTO.getVehiculoId() == null || asignacionDTO.getConductorId() == null) {
            if (asignacionDTO.getVehiculoId() == null) result.rejectValue("vehiculoId", "error.vehiculoId", "El vehículo seleccionado no existe");
            if (asignacionDTO.getConductorId() == null) result.rejectValue("conductorId", "error.conductorId", "El conductor seleccionado no existe");
            throw new RuntimeException("El vehículo o conductor seleccionado no existe");
        }

        Asignacion asignacion = asignacionRepositorio.findById(asignacionDTO.getId())
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));

        Vehiculo vehiculo = vehiculoRepositorio.findById(asignacionDTO.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        Conductor conductor = conductorRepositorio.findById(asignacionDTO.getConductorId())
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        // Si cambia el vehículo, gestionar disponibilidad
        if (!asignacion.getVehiculo().getId().equals(vehiculo.getId())) {
            asignacion.getVehiculo().setDisponible(true);
            vehiculo.setDisponible(false);
        }

        mapearDtoAEntidad(asignacionDTO, asignacion, vehiculo, conductor);
        // En actualización permitimos modificar kilometrajes si es necesario
        asignacion.setKilometrajeInicial(asignacionDTO.getKilometrajeInicial());
        asignacion.setKilometrajeFinal(asignacionDTO.getKilometrajeFinal());
        asignacion.setKilometrosRecorridos(asignacionDTO.getKilometrosRecorridos());

        asignacionRepositorio.save(asignacion);
    }

    private void mapearDtoAEntidad(AsignacionDTO dto, Asignacion entidad, Vehiculo vehiculo, Conductor conductor) {
        entidad.setVehiculo(vehiculo);
        entidad.setConductor(conductor);
        entidad.setFechaAsignacion(dto.getFechaAsignacion());
        entidad.setFechaDevolucion(dto.getFechaDevolucion());
        entidad.setFechaDevolucionEstimada(dto.getFechaDevolucionEstimada());
        entidad.setEstado(dto.getEstado());
    }

    public AsignacionDTO convertirADataTransferObject(Asignacion asignacion) {
        AsignacionDTO dto = new AsignacionDTO();
        dto.setId(asignacion.getId());
        dto.setVehiculoId(asignacion.getVehiculo().getId());
        dto.setNombreVehiculo(asignacion.getVehiculo().getPatente() + " - " + asignacion.getVehiculo().getModelo().getNombreCompletoModelo());
        dto.setConductorId(asignacion.getConductor().getId());
        dto.setNombreConductor(asignacion.getConductor().getNomApe());
        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
        dto.setFechaDevolucion(asignacion.getFechaDevolucion());
        dto.setFechaDevolucionEstimada(asignacion.getFechaDevolucionEstimada());
        dto.setEstado(asignacion.getEstado());
        dto.setKilometrajeInicial(asignacion.getKilometrajeInicial());
        dto.setKilometrajeFinal(asignacion.getKilometrajeFinal());
        dto.setKilometrosRecorridos(asignacion.getKilometrosRecorridos());
        return dto;
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

    @Transactional(readOnly = true)
    public Asignacion obtenerPorId(Long id) {
        return asignacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));
    }

}
