package Biosidus.GestionDeFlota.controllers;
import Biosidus.GestionDeFlota.DTOs.VehiculoDTO;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.exceptions.PatenteDuplicadaException;
import Biosidus.GestionDeFlota.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/listado")
    public String listadoVehiculos(@RequestParam(name="buscar", required=false) String buscar, Model model, @RequestParam(name = "page", defaultValue = "0") int page){
        Page<Vehiculo> pagina = vehiculoService.listarVehiculos(buscar, page);
        model.addAttribute("vehiculos", pagina.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("tamanioPagina", pagina.getSize());
        model.addAttribute("totalItems", pagina.getTotalElements());
        model.addAttribute("busqueda", buscar);
        return "listadoVehiculos";
    }

    @GetMapping("/crear")
    public String crearVehiculo(Model model) {
        model.addAttribute("vehiculo", new VehiculoDTO());
        model.addAttribute("modelos", vehiculoService.listarModelos());
        return "crearVehiculo";
    }

    @PostMapping("/guardar")
    public String guardarVehiculo(@Valid @ModelAttribute("vehiculo") VehiculoDTO vehiculoDTO, 
                                 BindingResult result, 
                                 Model model, 
                                 RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("modelos", vehiculoService.listarModelos());
            return "crearVehiculo";
        }

        try {
            vehiculoService.guardarVehiculo(vehiculoDTO);
            redirectAttributes.addFlashAttribute("exito", "Vehículo guardado correctamente");
            return "redirect:/vehiculo/listado";
        } catch (PatenteDuplicadaException e) {
            model.addAttribute("modelos", vehiculoService.listarModelos());
            model.addAttribute("error", e.getMessage());
            return "crearVehiculo";
        }

    }

    @GetMapping("/modificar/{id}")
    public String modificarVehiculo(@PathVariable("id") Long id, Model model) {
        VehiculoDTO vehiculoDTO = vehiculoService.obtenerVehiculoDTO(id);
        model.addAttribute("vehiculo", vehiculoDTO);
        model.addAttribute("modelos", vehiculoService.listarModelos());
        model.addAttribute("id", id);
        return "modificarVehiculo";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarVehiculo(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("vehiculo") VehiculoDTO vehiculoDTO,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("modelos", vehiculoService.listarModelos());
            model.addAttribute("id", id);
            return "modificarVehiculo";
        }

        try {
            vehiculoService.actualizarVehiculo(id, vehiculoDTO);
            redirectAttributes.addFlashAttribute("exito", "Vehículo actualizado correctamente");
            return "redirect:/vehiculo/listado";
        } catch (PatenteDuplicadaException e) {
            model.addAttribute("modelos", vehiculoService.listarModelos());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("id", id);
            return "modificarVehiculo";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVehiculo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        // Implementar lógica de eliminación si es necesario
        return "redirect:/vehiculo/listado";
    }

    @GetMapping("/detalle/{id}")
    public String detalleVehiculo(Model model) {
        return "detalleVehiculo";
    }
}
