package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.AsignacionDTO;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.asignacion.EstadoAsignacion;
import Biosidus.GestionDeFlota.services.AsignacionService;
import Biosidus.GestionDeFlota.services.ConductorService;
import Biosidus.GestionDeFlota.services.CustomUserDetailsService;
import Biosidus.GestionDeFlota.services.ExcelReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/asignacion")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private ConductorService conductorService;

    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping("/listado")
    public String listadoAsignaciones(@RequestParam(name = "buscar", required = false) String buscar,
                                     @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                     @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                     @RequestParam(name = "fechaParticular", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaParticular,
                                     @RequestParam(name = "vehiculoId", required = false) Long vehiculoId,
                                     @RequestParam(name = "conductorId", required = false) Long conductorId,
                                     @RequestParam(name = "estado", required = false) EstadoAsignacion estado,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     Model model) {
        
        LocalDateTime desde = (fechaDesde != null) ? fechaDesde.atStartOfDay() : null;
        LocalDateTime hasta = (fechaHasta != null) ? fechaHasta.atTime(LocalTime.MAX) : null;
        LocalDateTime particular = (fechaParticular != null) ? fechaParticular.atStartOfDay() : null;

        Page<Asignacion> pagina = asignacionService.listarAsignaciones(buscar, desde, hasta, particular, vehiculoId, conductorId, estado, page);
        
        model.addAttribute("asignaciones", pagina.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("tamanioPagina", pagina.getSize());
        model.addAttribute("totalItems", pagina.getTotalElements());
        
        model.addAttribute("busqueda", buscar);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("fechaParticular", fechaParticular);
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

    @GetMapping("/detalle/{id}")
    public String detalleAsignacion(@PathVariable Long id, Model model) {
        Asignacion asignacion = asignacionService.obtenerPorId(id);
        model.addAttribute("asignacion", asignacion);
        return "detalleAsignacion";
    }

    @GetMapping("/modificar/{id}")
    public String modificarAsignacion(@PathVariable Long id, Model model) {
        Asignacion asignacion = asignacionService.obtenerPorId(id);
        model.addAttribute("asignacion", asignacionService.convertirADataTransferObject(asignacion));
        model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
        model.addAttribute("conductores", asignacionService.listarConductores());
        model.addAttribute("estados", EstadoAsignacion.values());
        return "modificarAsignacion";
    }

    @PostMapping("/actualizar")
    public String actualizarAsignacion(@Valid @ModelAttribute("asignacion") AsignacionDTO asignacionDTO,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
            model.addAttribute("conductores", asignacionService.listarConductores());
            model.addAttribute("estados", EstadoAsignacion.values());
            return "modificarAsignacion";
        }

        try {
            asignacionService.actualizarAsignacion(asignacionDTO, result);
            redirectAttributes.addFlashAttribute("exito", "Asignación actualizada correctamente");
            return "redirect:/asignacion/listado";
        } catch (Exception e) {
            model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
            model.addAttribute("conductores", asignacionService.listarConductores());
            model.addAttribute("estados", EstadoAsignacion.values());
            model.addAttribute("error", "Error al actualizar la asignación: " + e.getMessage());
            return "modificarAsignacion";
        }
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<InputStreamResource> exportarExcel(
            @RequestParam(name = "buscar", required = false) String buscar,
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(name = "fechaParticular", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaParticular,
            @RequestParam(name = "vehiculoId", required = false) Long vehiculoId,
            @RequestParam(name = "conductorId", required = false) Long conductorId,
            @RequestParam(name = "estado", required = false) EstadoAsignacion estado) {

        try {
            LocalDateTime desde = (fechaDesde != null) ? fechaDesde.atStartOfDay() : null;
            LocalDateTime hasta = (fechaHasta != null) ? fechaHasta.atTime(LocalTime.MAX) : null;
            LocalDateTime particular = (fechaParticular != null) ? fechaParticular.atStartOfDay() : null;

            List<Asignacion> asignacionesFiltradas = asignacionService.listarAsignacionesParaReporte(buscar, desde, hasta, particular, vehiculoId, conductorId, estado);

            ByteArrayInputStream stream = excelReportService.generarReporteExcelAsignacion(asignacionesFiltradas);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_asignaciones.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(stream));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/mis-asignaciones")
    public String obtenerMisAsignaciones(Model model, Authentication authentication){
        List<Asignacion> asignaciones = asignacionService.obtenerAsignacionesPorConductor(conductorService.obtenerConductorPorUsuarioNickname(authentication.getName()).getId());
        model.addAttribute("asignaciones", asignaciones);
        return "listadoAsignaciones";
    }
}
