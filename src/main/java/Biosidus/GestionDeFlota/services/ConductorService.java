package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.DTOs.ConductorUsuarioDTO;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.empleado.Usuario;
import Biosidus.GestionDeFlota.exceptions.ContraseniasSinCoincidirException;
import Biosidus.GestionDeFlota.exceptions.DniDuplicadoException;
import Biosidus.GestionDeFlota.exceptions.LegajoDuplicadoException;
import Biosidus.GestionDeFlota.exceptions.NicknameDuplicadoException;
import Biosidus.GestionDeFlota.repositorios.ConductorRepositorio;
import Biosidus.GestionDeFlota.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Service
public class ConductorService {

    @Autowired
    private ConductorRepositorio conductorRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Conductor> listarConductores() {
        return conductorRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Conductor buscarPorId(Long id) {
        return conductorRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Conductor no encontrado"));
    }

    @Transactional
    public void registrarConductor(ConductorUsuarioDTO dto) {
        if (dto.getContrasena() == null || !dto.getContrasena().equals(dto.getVerificacionContrasena())) {
            throw new ContraseniasSinCoincidirException("Las contraseñas no coinciden");
        }

        if (usuarioRepositorio.findByNickname(dto.getNickname()).isPresent()) {
            throw new NicknameDuplicadoException("El nickname ya se encuentra en uso");
        }

        if (conductorRepositorio.findByDni(dto.getDni()).isPresent()) {
            throw new DniDuplicadoException("Ya existe un conductor con el mismo DNI");
        }

        if (conductorRepositorio.findByLegajo(dto.getLegajo()).isPresent()) {
            throw new LegajoDuplicadoException("Ya existe un conductor con el mismo legajo");
        }

        Usuario usuario = Usuario.builder()
                .nickname(dto.getNickname())
                .password(passwordEncoder.encode(dto.getContrasena()))
                .rol(dto.getRol())
                .build();

        usuarioRepositorio.save(usuario);

        Conductor conductor = Conductor.builder()
                .nomApe(dto.getNomApe())
                .dni(dto.getDni())
                .legajo(dto.getLegajo())
                .mail(dto.getMail())
                .rango(dto.getRango())
                .usuario(usuario)
                .build();

        conductorRepositorio.save(conductor);
    }
}
