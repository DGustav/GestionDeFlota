package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.ConductorUsuarioDTO;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.documento.EstadoDocumento;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.siniestro.Siniestro;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.exceptions.DniDuplicadoException;
import Biosidus.GestionDeFlota.exceptions.LegajoDuplicadoException;
import Biosidus.GestionDeFlota.exceptions.NicknameDuplicadoException;
import Biosidus.GestionDeFlota.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private ConductorService conductorService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private SiniestroService siniestroService;

    @Autowired
    private DocumentoService documentoService;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/tableroDeControl")
    public String tableroDeControl(Model model) {
        List<Vehiculo> vehiculos = vehiculoService.listarVehiculos();
        model.addAttribute("totalVehiculos", vehiculos.size());

        List<Asignacion> asignaciones = asignacionService.obtenerAsignaciones();
        model.addAttribute("totalAsignaciones", asignaciones.size());

        List<Conductor> conductores = conductorService.listarConductores();
        model.addAttribute("totalConductores", conductores.size());

        List<Siniestro> siniestros = siniestroService.listarSiniestros();
        model.addAttribute("totalSiniestros", siniestros.size());

        model.addAttribute("documentosPorvencer", documentoService.listarDocumentosPorEstadoDocumento(EstadoDocumento.POR_VENCER));
        model.addAttribute("documentosVigentes", documentoService.listarDocumentosPorEstadoDocumento(EstadoDocumento.VIGENTE));
        model.addAttribute("documentosVencidos", documentoService.listarDocumentosPorEstadoDocumento(EstadoDocumento.VENCIDO));


        return "tableroDeControl";
    }

    @GetMapping("/alertas")
    public String alertas(Model model) {
        return "alertas";
    }

    @GetMapping("/forbidden")
    public String forbidden() {
        return "forbidden";
    }

    @PostMapping("/registrarConductor")
    public String registrarConductor(@ModelAttribute ConductorUsuarioDTO dto, RedirectAttributes redirectAttributes) {

        try {
            conductorService.registrarConductor(dto);
            redirectAttributes.addFlashAttribute("exito", "Conductor y usuario creados exitosamente");
            return "redirect:/conductor/listado";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("conductorDto", dto);
            return "redirect:/conductor/crear";
        }
    }


}
