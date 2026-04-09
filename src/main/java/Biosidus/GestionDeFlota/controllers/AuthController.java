package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
    @GetMapping("/tableroDeControl")
    public String tableroDeControl(Model model) {
        return "tableroDeControl";
    }

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

    @GetMapping("/detalleConductor/{id}")
    public String detalleConductor(Model model) {
        return "detalleConductor";
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

    @GetMapping("/detalleVehiculo/{id}")
    public String detalleVehiculo(Model model) {
        return "detalleVehiculo";
    }

    @GetMapping("/crearAsignacion")
    public String crearAsignacion(Model model) {
        return "crearAsignacion";
    }

    @GetMapping("/modificarAsignacion/{id}")
    public String modificarAsignacion(Model model) {
        return "modificarAsignacion";
    }

    @GetMapping("/detalleAsignacion/{id}")
    public String detalleAsignacion(Model model) {
        return "detalleAsignacion";
    }

    @GetMapping("/listadoSiniestros")
    public String listadoSiniestros(Model model) {
        return "listadoSiniestros";
    }

    @GetMapping("/crearSiniestro")
    public String crearSiniestro(Model model) {
        return "crearSiniestro";
    }

    @GetMapping("/modificarSiniestro/{id}")
    public String modificarSiniestro(Model model) {
        return "modificarSiniestro";
    }

    @GetMapping("/detalleSiniestro/{id}")
    public String detalleSiniestro(Model model) {
        return "detalleSiniestro";
    }

    @GetMapping("/listadoMantenimientos")
    public String listadoMantenimientos(Model model) {
        return "listadoMantenimientos";
    }

    @GetMapping("/crearMantenimiento")
    public String crearMantenimiento(Model model) {
        return "crearMantenimiento";
    }

    @GetMapping("/modificarMantenimiento/{id}")
    public String modificarMantenimiento(Model model) {
        return "modificarMantenimiento";
    }

    @GetMapping("/detalleMantenimiento/{id}")
    public String detalleMantenimiento(Model model) {
        return "detalleMantenimiento";
    }

    @GetMapping("/listadoDocumentos")
    public String listadoDocumentos(Model model) {
        return "listadoDocumentos";
    }

    @GetMapping("/crearDocumento")
    public String crearDocumento(Model model) {
        return "crearDocumento";
    }

    @GetMapping("/modificarDocumento/{id}")
    public String modificarDocumento(Model model) {
        return "modificarDocumento";
    }

    @GetMapping("/detalleDocumento/{id}")
    public String detalleDocumento(Model model) {
        return "detalleDocumento";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {
        return "alertas";
    }
}
