package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.ConductorUsuarioDTO;
import Biosidus.GestionDeFlota.domain.empleado.Rango;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.empleado.Rol;
import Biosidus.GestionDeFlota.exceptions.DniDuplicadoException;
import Biosidus.GestionDeFlota.services.ConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/conductor")

public class ConductorController {

    @Autowired
    private ConductorService conductorService;

    @GetMapping("/crear")
    public String crearConductor(@ModelAttribute("conductorDto") ConductorUsuarioDTO conductorDto, Model model) {
        model.addAttribute("rangos", Rango.values());
        // Spring inyecta "conductorDto":
        // - Si viene de un redirect (FlashAttributes), reutiliza ese objeto con datos
        // - Si es la primera vez, crea uno vacío automáticamente
        return "crearConductor";
    }

    @GetMapping("/listado")
    public String listadoConductores(@RequestParam(name="buscar", required=false) String buscar, Model model, @RequestParam(name = "page", defaultValue = "0") int page){
        Page<Conductor> pagina = conductorService.listarConductores(buscar, page);
        model.addAttribute("conductores", pagina.getContent()); // Los registros de la página actual
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("totalItems", pagina.getTotalElements());
        model.addAttribute("busqueda", buscar);
        return "listadoConductores";
    }

    @GetMapping("/modificar/{id}")
    public String modificarConductor(@PathVariable("id") Long id, Model model) {
        Conductor conductor = conductorService.buscarPorId(id);
        

       ConductorUsuarioDTO conductorDto = new ConductorUsuarioDTO();
        conductorDto.setNomApe(conductor.getNomApe());
        conductorDto.setDni(conductor.getDni());
        conductorDto.setLegajo(conductor.getLegajo());
        conductorDto.setMail(conductor.getMail());
        conductorDto.setRango(conductor.getRango());
        conductorDto.setNickname(conductor.getUsuario().getNickname());
        conductorDto.setRol(conductor.getUsuario().getRol());
        
        model.addAttribute("conductorDto", conductorDto);
        model.addAttribute("id", id);
        model.addAttribute("rangos", Rango.values());
        model.addAttribute("roles", Rol.values());
        
        return "modificarConductor";
    }

    @PostMapping("/modificar/{id}")
    public String procesarModificarConductor(@PathVariable("id") Long id, @ModelAttribute("conductorDto") ConductorUsuarioDTO conductorDto, RedirectAttributes redirectAttributes) {
        try {
            conductorService.actualizarConductor(id, conductorDto);
            redirectAttributes.addFlashAttribute("success", "Conductor actualizado correctamente");
            return "redirect:/conductor/listado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/conductor/modificar/" + id;
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalleConductor(@PathVariable("id") Long id, Model model) {
        Conductor conductor = conductorService.buscarPorId(id);
        model.addAttribute("conductor", conductor);
        return "detalleConductor";
    }
}
