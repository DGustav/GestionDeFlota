package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.VehiculoDTO;
import Biosidus.GestionDeFlota.domain.vehiculo.Modelo;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.exceptions.PatenteDuplicadaException;
import Biosidus.GestionDeFlota.repositorios.ModeloRepositorio;
import Biosidus.GestionDeFlota.repositorios.VehiculoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;

    @Autowired
    private ModeloRepositorio modeloRepositorio;

    @Transactional(readOnly = true)
    public Page<Vehiculo> listarVehiculos(String busqueda, int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 5, Sort.by("patente").ascending());

        if (busqueda != null && !busqueda.isEmpty()) {
            return vehiculoRepositorio.findByPatenteContainingIgnoreCaseOrModeloNombreCompletoModeloContainingIgnoreCase(
                    busqueda, busqueda, pageable
            );
        }

        return vehiculoRepositorio.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Modelo> listarModelos() {
        return modeloRepositorio.findAll();
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
                .esLeasing(vehiculoDTO.getEsLeasing()).build();

        if (vehiculoDTO.getModeloId() != null) {
            modeloRepositorio.findById(vehiculoDTO.getModeloId()).ifPresent(vehiculo::setModelo);
        } else if (vehiculoDTO.getNombreModelo() != null && !vehiculoDTO.getNombreModelo().isBlank()) {
            Modelo modelo = modeloRepositorio.findByNombreCompletoModelo(vehiculoDTO.getNombreModelo())
                    .orElseGet(() -> {
                        Modelo nuevoModelo = new Modelo();
                        nuevoModelo.setNombreCompletoModelo(vehiculoDTO.getNombreModelo());
                        return modeloRepositorio.save(nuevoModelo);
                    });
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
        dto.setEsLeasing(v.getEsLeasing());
        
        if (v.getModelo() != null) {
            dto.setModeloId(v.getModelo().getId());
            dto.setNombreModelo(v.getModelo().getNombreCompletoModelo());
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
        vehiculo.setEsLeasing(vehiculoDTO.getEsLeasing());

        if (vehiculoDTO.getModeloId() != null) {
            modeloRepositorio.findById(vehiculoDTO.getModeloId()).ifPresent(vehiculo::setModelo);
        } else if (vehiculoDTO.getNombreModelo() != null && !vehiculoDTO.getNombreModelo().isBlank()) {
            Modelo modelo = modeloRepositorio.findByNombreCompletoModelo(vehiculoDTO.getNombreModelo())
                    .orElseGet(() -> {
                        Modelo nuevoModelo = new Modelo();
                        nuevoModelo.setNombreCompletoModelo(vehiculoDTO.getNombreModelo());
                        return modeloRepositorio.save(nuevoModelo);
                    });
            vehiculo.setModelo(modelo);
        }

        vehiculoRepositorio.save(vehiculo);
    }
}
