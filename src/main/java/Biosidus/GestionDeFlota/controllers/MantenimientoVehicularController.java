package Biosidus.GestionDeFlota.controllers;
import Biosidus.GestionDeFlota.DTOs.MantenimientoVehicularDTO;
import Biosidus.GestionDeFlota.domain.vehiculo.EstadoVehiculo;
import Biosidus.GestionDeFlota.domain.vehiculo.MantenimientoVehicular;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.services.MantenimientoVehicularService;
import Biosidus.GestionDeFlota.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.format.annotation.DateTimeFormat;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/mantenimientoVehicular")
public class MantenimientoVehicularController {

    @Autowired
    private MantenimientoVehicularService mantenimientoService;

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/listado")
    public String listadoMantenimientos(Model model, 
                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(name = "buscar", required = false) String buscar,
                                       @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                       @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                       @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Page<MantenimientoVehicular> pagina = mantenimientoService.listarMantenimientos(page, buscar, fecha, fechaDesde, fechaHasta);
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
    public String crearMantenimiento(Model model) {
        List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("mantenimiento", new MantenimientoVehicularDTO());
        return "crearMantenimiento";
    }

    @PostMapping("/crear")
    public String guardarMantenimiento(@Valid @ModelAttribute("mantenimiento") MantenimientoVehicularDTO mantenimientoDTO,
                                       BindingResult result,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Vehiculo> vehiculos = vehiculoService.listarVehiculos().stream().filter(v -> v.getEstado() != EstadoVehiculo.EN_SERVICE).toList();
            model.addAttribute("vehiculos", vehiculos);
            return "crearMantenimiento";
        }

        try {
            mantenimientoService.guardarMantenimiento(mantenimientoDTO);
            redirectAttributes.addFlashAttribute("exito", "El mantenimiento se ha registrado correctamente.");
            return "redirect:/mantenimientoVehicular/listado";
        } catch (IOException e) {
            // Manejar error de guardado de archivo
            model.addAttribute("error", "Error al guardar el comprobante: " + e.getMessage());
            List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
            model.addAttribute("vehiculos", vehiculos);
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
}
