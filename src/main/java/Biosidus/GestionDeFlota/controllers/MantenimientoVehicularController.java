package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mantenimientoVehicular")
public class MantenimientoVehicularController {
    @GetMapping("/listado")
    public String listadoMantenimientos(Model model) {
        return "listadoMantenimientos";
    }

    @GetMapping("/crear")
    public String crearMantenimiento(Model model) {
        return "crearMantenimiento";
    }

    @GetMapping("/modificar/{id}")
    public String modificarMantenimiento(Model model) {
        return "modificarMantenimiento";
    }

    @GetMapping("/detalle/{id}")
    public String detalleMantenimiento(Model model) {
        return "detalleMantenimiento";
    }
}
