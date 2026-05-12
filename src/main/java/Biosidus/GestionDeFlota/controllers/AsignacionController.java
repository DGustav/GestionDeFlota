package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.AsignacionDTO;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import Biosidus.GestionDeFlota.services.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/asignacion")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping("/listado")
    public String listadoAsignaciones(@RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                     @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                     @RequestParam(name = "vehiculoId", required = false) Long vehiculoId,
                                     @RequestParam(name = "conductorId", required = false) Long conductorId,
                                     @RequestParam(name = "estado", required = false) EstadoAsignacion estado,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     Model model) {
        
        LocalDateTime desde = (fechaDesde != null) ? fechaDesde.atStartOfDay() : null;
        LocalDateTime hasta = (fechaHasta != null) ? fechaHasta.atTime(LocalTime.MAX) : null;

        Page<Asignacion> pagina = asignacionService.listarAsignaciones(desde, hasta, vehiculoId, conductorId, estado, page);
        
        model.addAttribute("asignaciones", pagina.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("tamanioPagina", pagina.getSize());
        model.addAttribute("totalItems", pagina.getTotalElements());
        
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("vehiculoIdSeleccionado", vehiculoId);
        model.addAttribute("conductorIdSeleccionado", conductorId);
        model.addAttribute("estadoSeleccionado", estado);
        
        model.addAttribute("estados", EstadoAsignacion.values());
        model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
        model.addAttribute("conductores", asignacionService.listarConductores());
        
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
            asignacionService.guardarAsignacion(asignacionDTO, result);
            redirectAttributes.addFlashAttribute("exito", "Asignación guardada correctamente");
            return "redirect:/asignacion/listado";
        } catch (Exception e) {
            model.addAttribute("vehiculos", asignacionService.listarVehiculosDisponibles());
            model.addAttribute("conductores", asignacionService.listarConductores());
            model.addAttribute("estados", EstadoAsignacion.values());
            return "crearAsignacion";
        }
    }
}
