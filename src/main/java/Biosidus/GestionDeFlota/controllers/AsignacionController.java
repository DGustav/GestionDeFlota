package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.services.AsignacionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/asignacion")
public class AsignacionController {

    private AsignacionService asignacionService;
    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @GetMapping("/listado")
    public String listadoAsignacion(Model model) {
        return "listadoAsignaciones";
    }

    @GetMapping("/crear")
    public String crearAsignacion(Model model) {
        return "crearAsignacion";
    }

    @GetMapping("/modificar/{id}")
    public String modificarAsignacion(Model model) {
        return "modificarAsignacion";
    }

    @GetMapping("/detalle/{id}")
    public String detalleAsignacion(Model model) {
        return "detalleAsignacion";
    }

    @GetMapping("/mis-asignaciones")
    public String listarMisAsignaciones(Authentication authentication, Model model) {
        String nickname = authentication.getName();

        List<Asignacion> asignaciones = asignacionService.buscarPorConductor(nickname);

        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("titulo", "Mis asignaciones");

        return "listadoAsignaciones";
    }

}
