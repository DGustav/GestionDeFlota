package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.domain.alerta.Alerta;
import Biosidus.GestionDeFlota.domain.alerta.TipoAlerta;
import Biosidus.GestionDeFlota.repositorios.AlertaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Boolean existeAlertaPorDocumentoYtipo(Long id, TipoAlerta tipo) {
        return alertaRepo.existsByDocumentoIdAndTipo(id, tipo);
    }

    @Transactional
    public Boolean existeAlertaPorVehiculoYtipo(Long id, TipoAlerta tipo) {
        return alertaRepo.existsByVehiculoIdAndTipo(id, tipo);
    }
}
