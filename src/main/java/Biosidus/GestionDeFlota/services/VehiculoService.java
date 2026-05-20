package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.VehiculoDTO;
import Biosidus.GestionDeFlota.domain.vehiculo.EstadoVehiculo;
import Biosidus.GestionDeFlota.domain.vehiculo.Modelo;
import Biosidus.GestionDeFlota.domain.vehiculo.OrigenVehiculo;
import Biosidus.GestionDeFlota.domain.vehiculo.Propietario;
import Biosidus.GestionDeFlota.domain.vehiculo.TipoVehiculo;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.exceptions.PatenteDuplicadaException;
import Biosidus.GestionDeFlota.repositorios.ModeloRepositorio;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;

    @Autowired
    private ModeloRepositorio modeloRepositorio;

    @Transactional(readOnly = true)
    public Page<Vehiculo> listarVehiculos(String busqueda, Boolean disponible, EstadoVehiculo estado, OrigenVehiculo origen, Propietario propietario, TipoVehiculo tipoVehiculo, int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 7, Sort.by("patente").ascending());

        Specification<Vehiculo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (busqueda != null && !busqueda.isEmpty()) {
                String likePattern = "%" + busqueda.toLowerCase() + "%";
                
                // Intentar convertir la búsqueda a número para comparar con el año
                Predicate anioPredicate = null;
                try {
                    Integer busquedaAnio = Integer.parseInt(busqueda);
                    anioPredicate = cb.equal(root.join("modelo").get("anio"), busquedaAnio);
                } catch (NumberFormatException e) {
                    // No es un número, no se busca por año exacto
                }

                if (anioPredicate != null) {
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("patente")), likePattern),
                            cb.like(cb.lower(root.join("modelo").get("nombreCompletoModelo")), likePattern),
                            anioPredicate
                    ));
                } else {
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("patente")), likePattern),
                            cb.like(cb.lower(root.join("modelo").get("nombreCompletoModelo")), likePattern)
                    ));
                }
            }

            if (disponible != null) {
                predicates.add(cb.equal(root.get("disponible"), disponible));
            }

            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }

            if (origen != null) {
                predicates.add(cb.equal(root.get("origen"), origen));
            }

            if (propietario != null) {
                predicates.add(cb.equal(root.get("propietario"), propietario));
            }

            if (tipoVehiculo != null) {
                predicates.add(cb.equal(root.join("modelo").get("tipoVehiculo"), tipoVehiculo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return vehiculoRepositorio.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<Modelo> listarModelos() {
        return modeloRepositorio.findAll();
    }

    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepositorio.findAll();
    }

    @Transactional
    public void guardarVehiculo(VehiculoDTO vehiculoDTO) {
        if(vehiculoRepositorio.findByPatente(vehiculoDTO.getPatente().toUpperCase().trim()).isPresent()) {
            throw new PatenteDuplicadaException("Ya existe un vehículo con la misma patente");
        }

        Vehiculo vehiculo = Vehiculo.builder()
                .patente(vehiculoDTO.getPatente().toUpperCase().replace("-", ""))
                .kilometrajeActual(vehiculoDTO.getKilometrajeActual())
                .fechaAdquisicion(vehiculoDTO.getFechaAdquisicion())
                .disponible(vehiculoDTO.getDisponible())
                .estado(vehiculoDTO.getEstado())
                .chasis(vehiculoDTO.getChasis())
                .motor(vehiculoDTO.getMotor())
                .origen(vehiculoDTO.getOrigen())
                .propietario(vehiculoDTO.getPropietario())
                .build();

        if (vehiculoDTO.getModeloId() != null) {
            modeloRepositorio.findById(vehiculoDTO.getModeloId()).ifPresent(m -> {
                m.setAnio(vehiculoDTO.getAnio());
                m.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
                modeloRepositorio.save(m);
                vehiculo.setModelo(m);
            });
        } else if (vehiculoDTO.getNombreModelo() != null && !vehiculoDTO.getNombreModelo().isBlank()) {
            Modelo modelo = modeloRepositorio.findByNombreCompletoModelo(vehiculoDTO.getNombreModelo())
                    .orElseGet(() -> {
                        Modelo nuevoModelo = new Modelo();
                        nuevoModelo.setNombreCompletoModelo(vehiculoDTO.getNombreModelo());
                        nuevoModelo.setAnio(vehiculoDTO.getAnio());
                        nuevoModelo.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
                        return modeloRepositorio.save(nuevoModelo);
                    });
            modelo.setAnio(vehiculoDTO.getAnio());
            modelo.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
            modeloRepositorio.save(modelo);
            vehiculo.setModelo(modelo);
        }

        vehiculoRepositorio.save(vehiculo);
    }

    @Transactional(readOnly = true)
    public Optional<Vehiculo> buscarPorId(Long id) {
        return vehiculoRepositorio.findById(id);
    }

    @Transactional(readOnly = true)
    public VehiculoDTO obtenerVehiculoDTO(Long id) {
        Vehiculo v = vehiculoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        VehiculoDTO dto = new VehiculoDTO();
        dto.setPatente(v.getPatente());
        dto.setKilometrajeActual(v.getKilometrajeActual());
        dto.setFechaAdquisicion(v.getFechaAdquisicion());
        dto.setDisponible(v.getDisponible());
        dto.setEstado(v.getEstado());
        dto.setChasis(v.getChasis());
        dto.setMotor(v.getMotor());
        dto.setOrigen(v.getOrigen());
        dto.setPropietario(v.getPropietario());
        
        if (v.getModelo() != null) {
            dto.setModeloId(v.getModelo().getId());
            dto.setNombreModelo(v.getModelo().getNombreCompletoModelo());
            dto.setAnio(v.getModelo().getAnio());
            dto.setTipoVehiculo(v.getModelo().getTipoVehiculo());
        }
        
        return dto;
    }

    @Transactional
    public void actualizarVehiculo(Long id, VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = vehiculoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        String nuevaPatente = vehiculoDTO.getPatente().toUpperCase().replace("-", "").trim();
        
        // Validar patente duplicada solo si cambió
        if (!vehiculo.getPatente().equalsIgnoreCase(nuevaPatente)) {
            if(vehiculoRepositorio.findByPatente(nuevaPatente).isPresent()) {
                throw new PatenteDuplicadaException("Ya existe un vehículo con la misma patente");
            }
        }

        vehiculo.setPatente(nuevaPatente);
        vehiculo.setKilometrajeActual(vehiculoDTO.getKilometrajeActual());
        vehiculo.setFechaAdquisicion(vehiculoDTO.getFechaAdquisicion());
        vehiculo.setDisponible(vehiculoDTO.getDisponible());
        vehiculo.setEstado(vehiculoDTO.getEstado());
        vehiculo.setChasis(vehiculoDTO.getChasis());
        vehiculo.setMotor(vehiculoDTO.getMotor());
        vehiculo.setOrigen(vehiculoDTO.getOrigen());
        vehiculo.setPropietario(vehiculoDTO.getPropietario());

        if (vehiculoDTO.getModeloId() != null) {
            modeloRepositorio.findById(vehiculoDTO.getModeloId()).ifPresent(m -> {
                m.setAnio(vehiculoDTO.getAnio());
                m.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
                modeloRepositorio.save(m);
                vehiculo.setModelo(m);
            });
        } else if (vehiculoDTO.getNombreModelo() != null && !vehiculoDTO.getNombreModelo().isBlank()) {
            Modelo modelo = modeloRepositorio.findByNombreCompletoModelo(vehiculoDTO.getNombreModelo())
                    .orElseGet(() -> {
                        Modelo nuevoModelo = new Modelo();
                        nuevoModelo.setNombreCompletoModelo(vehiculoDTO.getNombreModelo());
                        nuevoModelo.setAnio(vehiculoDTO.getAnio());
                        nuevoModelo.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
                        return modeloRepositorio.save(nuevoModelo);
                    });
            modelo.setAnio(vehiculoDTO.getAnio());
            modelo.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
            modeloRepositorio.save(modelo);
            vehiculo.setModelo(modelo);
        }

        vehiculoRepositorio.save(vehiculo);
    }
}
