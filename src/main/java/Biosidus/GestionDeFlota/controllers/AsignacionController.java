package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.AsignacionDTO;
import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import Biosidus.GestionDeFlota.services.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asignacion")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping("/listado")
    public String listadoAsignaciones(Model model) {
        model.addAttribute("asignaciones", asignacionService.listarAsignaciones());
        return "listadoAsignaciones";
    }

    @GetMapping("/crear")
    public String crearAsignacion(Model model) {
        model.addAttribute("asignacion", new AsignacionDTO());
        model.addAttribute("vehiculos", asignacionService.listarVehiculosDisponibles());
        model.addAttribute("conductores", asignacionService.listarConductores());
        model.addAttribute("estados", EstadoAsignacion.values());
        return "crearAsignacion";
    }

    @PostMapping("/guardar")
    public String guardarAsignacion(@Valid @ModelAttribute("asignacion") AsignacionDTO asignacionDTO,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("vehiculos", asignacionService.listarVehiculosDisponibles());
            model.addAttribute("conductores", asignacionService.listarConductores());
            model.addAttribute("estados", EstadoAsignacion.values());
            return "crearAsignacion";
        }

        try {
            asignacionService.guardarAsignacion(asignacionDTO);
            redirectAttributes.addFlashAttribute("exito", "Asignación guardada correctamente");
            return "redirect:/asignacion/listado";
        } catch (Exception e) {
            model.addAttribute("vehiculos", asignacionService.listarVehiculosDisponibles());
            model.addAttribute("conductores", asignacionService.listarConductores());
            model.addAttribute("estados", EstadoAsignacion.values());
            model.addAttribute("error", e.getMessage());
            return "crearAsignacion";
        }
    }
}
