package Biosidus.GestionDeFlota.controllers;
import Biosidus.GestionDeFlota.DTOs.MantenimientoVehicularDTO;
import Biosidus.GestionDeFlota.domain.mantenimientoVehicular.MantenimientoVehicular;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.services.AsignacionService;
import Biosidus.GestionDeFlota.services.ExcelReportService;
import Biosidus.GestionDeFlota.services.MantenimientoVehicularService;
import Biosidus.GestionDeFlota.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mantenimientoVehicular")
public class MantenimientoVehicularController {

    @Autowired
    private MantenimientoVehicularService mantenimientoService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ExcelReportService excelReportService;

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping("/listado")
    public String listadoMantenimientos(Model model, 
                                       Authentication authentication,
                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(name = "buscar", required = false) String buscar,
                                       @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                       @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                       @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        
        Vehiculo vehiculoFiltrado = null;
        boolean esConductor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTOR"));

        if (esConductor) {
            Optional<Asignacion> asignacionActiva = asignacionService.obtenerUltimaAsignacionActivaPorUsuario(authentication.getName());
            if (asignacionActiva.isPresent()) {
                vehiculoFiltrado = asignacionActiva.get().getVehiculo();
            } else {
                // Si el conductor no tiene vehículo asignado, devolvemos una página vacía o lista vacía
                model.addAttribute("mantenimientos", List.of());
                model.addAttribute("paginaActual", 0);
                model.addAttribute("totalPages", 0);
                model.addAttribute("totalItems", 0);
                return "listadoMantenimientos";
            }
        }

        Page<MantenimientoVehicular> pagina = mantenimientoService.listarMantenimientos(page, buscar, fecha, fechaDesde, fechaHasta, vehiculoFiltrado);
        
        model.addAttribute("mantenimientos", pagina.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("tamanioPagina", pagina.getSize());
        model.addAttribute("totalItems", pagina.getTotalElements());
        model.addAttribute("busqueda", buscar);
        model.addAttribute("fecha", fecha);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        return "listadoMantenimientos";
    }

    @GetMapping("/crear")
    public String crearMantenimiento(Model model, Authentication authentication) {

        MantenimientoVehicularDTO dto = new MantenimientoVehicularDTO();

        boolean esConductor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTOR"));

        if (esConductor) {

            Asignacion asignacion = asignacionService
                    .obtenerUltimaAsignacionActivaPorUsuario(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("No posee un vehículo asignado"));

            dto.setVehiculoId(asignacion.getVehiculo().getId());
            dto.setNombreVehiculo(
                    STR."\{asignacion.getVehiculo().getPatente()} - \{asignacion.getVehiculo().getModelo().getNombreCompletoModelo()}"
            );

        } else {
            model.addAttribute("vehiculos", vehiculoService.listarVehiculos());
        }

        model.addAttribute("esConductor", esConductor);
        model.addAttribute("mantenimiento", dto);

        return "crearMantenimiento";
    }

    @PostMapping("/crear")
    public String guardarMantenimiento(@Valid @ModelAttribute("mantenimiento") MantenimientoVehicularDTO mantenimientoDTO,
                                       BindingResult result,
                                       Model model,
                                       RedirectAttributes redirectAttributes,
                                       Authentication authentication) {
        if (result.hasErrors()) {
            List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
            model.addAttribute("vehiculos", vehiculos);
            return "crearMantenimiento";
        }
        boolean esConductor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTOR"));
        try {

            if (esConductor) {

                Asignacion asignacion = asignacionService
                        .obtenerUltimaAsignacionActivaPorUsuario(authentication.getName())
                        .orElseThrow(() -> new RuntimeException("No posee vehículo asignado"));

                mantenimientoDTO.setVehiculoId(asignacion.getVehiculo().getId());
            }

            if (result.hasErrors()
                    || (!esConductor && mantenimientoDTO.getVehiculoId() == null)) {

                if (!esConductor) {
                    model.addAttribute("vehiculos",
                            vehiculoService.listarVehiculos());
                }

                model.addAttribute("esConductor", esConductor);
                return "crearMantenimiento";
            }

            mantenimientoService.guardarMantenimiento(mantenimientoDTO);

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "El mantenimiento se ha registrado correctamente.");

            return "redirect:/mantenimientoVehicular/listado";

        } catch (IOException e) {

            model.addAttribute("error",
                    "Error al guardar el comprobante: " + e.getMessage());

            if (!esConductor) {
                model.addAttribute("vehiculos",
                        vehiculoService.listarVehiculos());
            }

            model.addAttribute("esConductor", esConductor);
            return "crearMantenimiento";

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());

            if (!esConductor) {
                model.addAttribute("vehiculos",
                        vehiculoService.listarVehiculos());
            }

            model.addAttribute("esConductor", esConductor);
            return "crearMantenimiento";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificarMantenimiento(@PathVariable("id") Long id, Model model) {
        MantenimientoVehicularDTO mantenimientoDTO = mantenimientoService.buscarDTOporId(id);
        List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("mantenimiento", mantenimientoDTO);
        return "modificarMantenimiento";
    }

    @PostMapping("/modificar")
    public String actualizarMantenimiento(@Valid @ModelAttribute("mantenimiento") MantenimientoVehicularDTO mantenimientoDTO,
                                          BindingResult result,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
            model.addAttribute("vehiculos", vehiculos);
            return "modificarMantenimiento";
        }

        try {
            mantenimientoService.guardarMantenimiento(mantenimientoDTO);
            redirectAttributes.addFlashAttribute("exito", "El mantenimiento se ha actualizado correctamente.");
            return "redirect:/mantenimientoVehicular/listado";
        } catch (IOException e) {
            model.addAttribute("error", "Error al actualizar el comprobante: " + e.getMessage());
            List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
            model.addAttribute("vehiculos", vehiculos);
            return "modificarMantenimiento";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalleMantenimiento(@PathVariable("id") Long id, Model model) {
        MantenimientoVehicular mantenimiento = mantenimientoService.buscarPorId(id);
        model.addAttribute("mantenimiento", mantenimiento);
        return "detalleMantenimiento";
    }

    @GetMapping("/descargar-comprobante/{id}")
    public ResponseEntity<Resource> descargarComprobante(@PathVariable Long id) {
        try {
            return mantenimientoService.descargarComprobante(id);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<InputStreamResource> exportarExcel(Authentication authentication,
                                                            @RequestParam(name = "buscar", required = false) String buscar,
                                                            @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                                            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) throws IOException {
        
        Vehiculo vehiculoFiltrado = null;
        boolean esConductor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTOR"));

        if (esConductor) {
            Optional<Asignacion> asignacionActiva = asignacionService.obtenerUltimaAsignacionActivaPorUsuario(authentication.getName());
            if (asignacionActiva.isPresent()) {
                vehiculoFiltrado = asignacionActiva.get().getVehiculo();
            } else {
                return ResponseEntity.ok().build(); // O manejar de otra forma
            }
        }

        List<MantenimientoVehicular> mantenimientos = mantenimientoService.listarMantenimientos(buscar, fecha, fechaDesde, fechaHasta, vehiculoFiltrado);
        InputStreamResource file = new InputStreamResource(excelReportService.generarReporteExcelMantenimiento(mantenimientos));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mantenimientos.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @GetMapping("/mis-mantenimientosVehiculares")
    public String mostrarMisMantenimientosVehiculares() {
        return "redirect:/mantenimientoVehicular/listado";
    }
}
