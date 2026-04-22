package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.ConductorUsuarioDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conductor")

public class ConductorController {

    @GetMapping("/crear")
    public String crearConductor(@ModelAttribute("conductorDto") ConductorUsuarioDTO conductorDto) {
        // Spring inyecta "conductorDto":
        // - Si viene de un redirect (FlashAttributes), reutiliza ese objeto con datos
        // - Si es la primera vez, crea uno vacío automáticamente
        return "crearConductor";
    }

    @GetMapping("/listado")
    public String listadoConductores(Model model) {
        return "listadoConductores";
    }

    @GetMapping("/modificar/{id}")
    public String modificarConductor(Model model) {
        return "modificarConductor";
    }

    @GetMapping("/detalle/{id}")
    public String detalleConductor(Model model) {
        return "detalleConductor";
    }
}
