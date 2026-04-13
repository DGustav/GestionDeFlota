package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController {
    @GetMapping("/listado")
    public String listadoVehiculos(Model model) {
        return "listadoVehiculos";
    }

    @GetMapping("/crear")
    public String crearVehiculo(Model model) {
        return "crearVehiculo";
    }

    @GetMapping("/modificar/{id}")
    public String modificarVehiculo(Model model) {
        return "modificarVehiculo";
    }

    @GetMapping("/detalle/{id}")
    public String detalleVehiculo(Model model) {
        return "detalleVehiculo";
    }
}
