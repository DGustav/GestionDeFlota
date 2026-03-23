package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
    @GetMapping
    public String login(Model model) {
        return "index";
    }

    @RequestMapping("/crearConductor")
    public String crearConductor(Model model) {
        return "crearConductor";
    }

    @GetMapping("/listadoConductores")
    public String listadoConductores(Model model) {
        return "listadoConductores";
    }

    @GetMapping("/modificarConductor/{id}")
    public String modificarConductor(Model model) {
        return "modificarConductor";
    }
}
