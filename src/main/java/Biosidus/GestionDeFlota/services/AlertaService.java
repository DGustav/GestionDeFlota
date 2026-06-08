package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.domain.alerta.Alerta;
import Biosidus.GestionDeFlota.domain.alerta.EstadoAlerta;
import Biosidus.GestionDeFlota.domain.alerta.TipoAlerta;
import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.repositorios.AlertaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AlertaService {
    @Autowired
    private AlertaRepositorio alertaRepo;

    @Transactional
    public void guardarAlerta(Alerta alerta) {
        alertaRepo.save(alerta);
    }

    @Transactional(readOnly = true)
    public Alerta obtenerAlerta(Long id) {
        return alertaRepo.findById(id).orElse(null);
    }

    @Transactional
    public Boolean existeAlertaPorDocumentoYtipo(Long id, TipoAlerta tipo, EstadoAlerta estado) {
        return alertaRepo.existsByDocumentoIdAndTipoAndEstado(id, tipo, estado);
    }

    @Transactional
    public Boolean existeAlertaPorVehiculoYtipo(Long id, TipoAlerta tipo, EstadoAlerta estado) {
        return alertaRepo.existsByVehiculoIdAndTipoAndEstado(id, tipo, EstadoAlerta.ACTIVA);
    }

    @Transactional
    public Optional<Alerta> obtenerDocumentoConAlertaActiva(Documento documento) {
        return alertaRepo.findFirstByDocumentoIdAndEstado(documento.getId(), EstadoAlerta.ACTIVA);
    }

    @Transactional
    public Optional<Alerta> obtenerAlertaActivaPorKm(Vehiculo vehiculo, TipoAlerta tipo, EstadoAlerta estado) {
        return alertaRepo.findFirstByVehiculoIdAndTipoAndEstado(vehiculo.getId(),tipo, estado);
    }

    @Transactional
    public Optional<Alerta> obtenerActivasPorVehiculoYTipo(Long vehiculoId, TipoAlerta tipo) {
        return alertaRepo.findFirstByVehiculoIdAndTipoAndEstado(vehiculoId, tipo, EstadoAlerta.ACTIVA);
    }
}
