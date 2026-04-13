package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conductor")
public class ConductorController {
    @RequestMapping("/crear")
    public String crearConductor(Model model) {
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
