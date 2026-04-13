package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/siniestro")
public class SiniestroController {
    @GetMapping("/listado")
    public String listadoSiniestros(Model model) {
        return "listadoSiniestros";
    }

    @GetMapping("/crear")
    public String crearSiniestro(Model model) {
        return "crearSiniestro";
    }

    @GetMapping("/modificar/{id}")
    public String modificarSiniestro(Model model) {
        return "modificarSiniestro";
    }

    @GetMapping("/detalle/{id}")
    public String detalleSiniestro(Model model) {
        return "detalleSiniestro";
    }
}
