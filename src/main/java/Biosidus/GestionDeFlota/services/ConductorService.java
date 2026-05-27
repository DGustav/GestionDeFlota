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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<Conductor> listarConductores(String busqueda, int numeroPagina) {
        // Configuramos: página actual, tamaño (10 items) y orden ascendente por nombre
        Pageable pageable = PageRequest.of(numeroPagina, 5, Sort.by("nomApe").ascending());

        if (busqueda != null && !busqueda.isEmpty()) {
            return conductorRepositorio.findByNomApeContainingIgnoreCaseOrDniContainingOrLegajoContainingOrUsuarioNicknameContainingIgnoreCaseOrMailContainingIgnoreCase(
                    busqueda, busqueda, busqueda, busqueda, busqueda, pageable
            );
        }

        // El método findAll() heredado también acepta un Pageable
        return conductorRepositorio.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Conductor buscarPorId(Long id) {
        return conductorRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Conductor no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Conductor> listarConductoresParaReporte(String busqueda) {
        if (busqueda != null && !busqueda.isEmpty()) {
            return conductorRepositorio.findByNomApeContainingIgnoreCaseOrDniContainingOrLegajoContainingOrUsuarioNicknameContainingIgnoreCaseOrMailContainingIgnoreCase(
                    busqueda, busqueda, busqueda, busqueda, busqueda, Sort.by("nomApe").ascending()
            );
        }
        return conductorRepositorio.findAll(Sort.by("nomApe").ascending());
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

    @Transactional
    public void actualizarConductor(Long id, ConductorUsuarioDTO dto) {
        Conductor conductor = buscarPorId(id);
        Usuario usuario = conductor.getUsuario();

        // Validar DNI duplicado si cambió
        if (!conductor.getDni().equals(dto.getDni())) {
            if (conductorRepositorio.findByDni(dto.getDni()).isPresent()) {
                throw new DniDuplicadoException("Ya existe un conductor con el mismo DNI");
            }
        }

        // Validar Legajo duplicado si cambió
        if (!conductor.getLegajo().equals(dto.getLegajo())) {
            if (conductorRepositorio.findByLegajo(dto.getLegajo()).isPresent()) {
                throw new LegajoDuplicadoException("Ya existe un conductor con el mismo legajo");
            }
        }

        // Validar Nickname duplicado si cambió
        if (!usuario.getNickname().equals(dto.getNickname())) {
            if (usuarioRepositorio.findByNickname(dto.getNickname()).isPresent()) {
                throw new NicknameDuplicadoException("El nickname ya se encuentra en uso");
            }
        }

        // Actualizar datos del conductor
        conductor.setNomApe(dto.getNomApe());
        conductor.setDni(dto.getDni());
        conductor.setLegajo(dto.getLegajo());
        conductor.setMail(dto.getMail());
        conductor.setRango(dto.getRango());

        // Actualizar datos del usuario
        usuario.setNickname(dto.getNickname());
        usuario.setRol(dto.getRol());

        // Actualizar contraseña si se proporcionó
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            if (!dto.getContrasena().equals(dto.getVerificacionContrasena())) {
                throw new ContraseniasSinCoincidirException("Las contraseñas no coinciden");
            }
            usuario.setPassword(passwordEncoder.encode(dto.getContrasena()));
        }

        usuarioRepositorio.save(usuario);
        conductorRepositorio.save(conductor);
    }
}
