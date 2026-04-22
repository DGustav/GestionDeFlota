package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.ConductorUsuarioDTO;
import Biosidus.GestionDeFlota.exceptions.DniDuplicadoException;
import Biosidus.GestionDeFlota.exceptions.LegajoDuplicadoException;
import Biosidus.GestionDeFlota.exceptions.NicknameDuplicadoException;
import Biosidus.GestionDeFlota.services.ConductorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private ConductorService conductorService;

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
        if (dto.getContrasena() == null || !dto.getContrasena().equals(dto.getVerificacionContrasena())) {
            redirectAttributes.addFlashAttribute("errorPassword", "Las contraseñas no coinciden");
            redirectAttributes.addFlashAttribute("conductorDto", dto);

            return "redirect:/conductor/crear";
        }

        try {
            conductorService.registrarConductor(dto);
            redirectAttributes.addFlashAttribute("exito", "Conductor y usuario creados exitosamente");
            return "redirect:/tableroDeControl";
        } catch (DniDuplicadoException e) {
            redirectAttributes.addFlashAttribute("errorDni", e.getMessage());
            redirectAttributes.addFlashAttribute("conductorDto", dto);
            return "redirect:/conductor/crear";
        } catch (LegajoDuplicadoException e) {
            redirectAttributes.addFlashAttribute("errorLegajo", e.getMessage());
            redirectAttributes.addFlashAttribute("conductorDto", dto);
            return "redirect:/conductor/crear";
        } catch (NicknameDuplicadoException e) {
            redirectAttributes.addFlashAttribute("errorNickname", e.getMessage());
            redirectAttributes.addFlashAttribute("conductorDto", dto);
            return "redirect:/conductor/crear";
        }
    }
}
