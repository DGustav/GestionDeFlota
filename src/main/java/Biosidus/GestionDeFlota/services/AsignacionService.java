package Biosidus.GestionDeFlota.services;

import org.springframework.stereotype.Service;

import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.repositorios.AsignacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsignacionService {

    private AsignacionRepositorio asignacionRepositorio;

    public AsignacionService(AsignacionRepositorio asignacionRepositorio) {
        this.asignacionRepositorio = asignacionRepositorio;
    }

    public List<Asignacion> buscarPorConductor(String conductorNickname) {
        return asignacionRepositorio.findByConductor_Usuario_Nickname(conductorNickname);
    }

    public Optional<Asignacion> buscarPorId(Long id){
        return Optional.ofNullable(asignacionRepositorio.findById(id).orElse(null));
    }
    public Asignacion guardar(Asignacion asignacion){
        return asignacionRepositorio.save(asignacion);
    }

    public List<Asignacion> listar(){
        return asignacionRepositorio.findAll();
    }
}
