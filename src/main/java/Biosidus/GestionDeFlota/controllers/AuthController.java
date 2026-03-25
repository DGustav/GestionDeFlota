package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
    @GetMapping
    public String listadoAsignaciones(){
        return "listadoAsignaciones";
    }

    @GetMapping("/listadoAsignaciones")
    public String listadoAsignacionesRedirect(Model model) {
        return "listadoAsignaciones";
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

    @GetMapping("/listadoVehiculos")
    public String listadoVehiculos(Model model) {
        return "listadoVehiculos";
    }

    @GetMapping("/crearVehiculo")
    public String crearVehiculo(Model model) {
        return "crearVehiculo";
    }

    @GetMapping("/modificarVehiculo/{id}")
    public String modificarVehiculo(Model model) {
        return "modificarVehiculo";
    }

    @GetMapping("/crearAsignacion")
    public String crearAsignacion(Model model) {
        return "crearAsignacion";
    }

    @GetMapping("/modificarAsignacion/{id}")
    public String modificarAsignacion(Model model) {
        return "modificarAsignacion";
    }
}
