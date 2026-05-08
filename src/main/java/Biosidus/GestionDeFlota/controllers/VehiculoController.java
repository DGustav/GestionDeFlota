package Biosidus.GestionDeFlota.controllers;
import Biosidus.GestionDeFlota.DTOs.VehiculoDTO;
import Biosidus.GestionDeFlota.domain.vehiculo.*;
import Biosidus.GestionDeFlota.exceptions.PatenteDuplicadaException;
import Biosidus.GestionDeFlota.services.VehiculoImportService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private VehiculoImportService vehiculoImportService;

    @GetMapping("/listado")
    public String listadoVehiculos(@RequestParam(name="buscar", required=false) String buscar,
                                 @RequestParam(name="disponible", required=false) Boolean disponible,
                                 @RequestParam(name="estado", required=false) EstadoVehiculo estado,
                                 @RequestParam(name="origen", required=false) OrigenVehiculo origen,
                                 @RequestParam(name="propietario", required=false) Propietario propietario,
                                 @RequestParam(name="tipoVehiculo", required=false) TipoVehiculo tipoVehiculo,
                                 Model model,
                                 @RequestParam(name = "page", defaultValue = "0") int page){
        Page<Vehiculo> pagina = vehiculoService.listarVehiculos(buscar, disponible, estado, origen, propietario, tipoVehiculo, page);
        model.addAttribute("vehiculos", pagina.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("tamanioPagina", pagina.getSize());
        model.addAttribute("totalItems", pagina.getTotalElements());
        model.addAttribute("busqueda", buscar);
        model.addAttribute("disponible", disponible);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("origenSeleccionado", origen);
        model.addAttribute("propietarioSeleccionado", propietario);
        model.addAttribute("tipoVehiculoSeleccionado", tipoVehiculo);
        model.addAttribute("estados", EstadoVehiculo.values());
        model.addAttribute("origenes", OrigenVehiculo.values());
        model.addAttribute("propietarios", Propietario.values());
        model.addAttribute("tiposVehiculo", TipoVehiculo.values());
        return "listadoVehiculos";
    }

    @GetMapping("/crear")
    public String crearVehiculo(Model model) {
        model.addAttribute("vehiculo", new VehiculoDTO());
        model.addAttribute("modelos", vehiculoService.listarModelos());
        model.addAttribute("estados", EstadoVehiculo.values());
        model.addAttribute("origenes", OrigenVehiculo.values());
        model.addAttribute("propietarios", Propietario.values());
        model.addAttribute("tiposVehiculo", TipoVehiculo.values());
        return "crearVehiculo";
    }

    @PostMapping("/guardar")
    public String guardarVehiculo(@Valid @ModelAttribute("vehiculo") VehiculoDTO vehiculoDTO, 
                                 BindingResult result, 
                                 Model model, 
                                 RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("modelos", vehiculoService.listarModelos());
            model.addAttribute("estados", EstadoVehiculo.values());
            model.addAttribute("origenes", OrigenVehiculo.values());
            model.addAttribute("propietarios", Propietario.values());
            model.addAttribute("tiposVehiculo", TipoVehiculo.values());
            return "crearVehiculo";
        }

        try {
            vehiculoService.guardarVehiculo(vehiculoDTO);
            redirectAttributes.addFlashAttribute("exito", "Vehículo guardado correctamente");
            return "redirect:/vehiculo/listado";
        } catch (PatenteDuplicadaException e) {
            model.addAttribute("modelos", vehiculoService.listarModelos());
            model.addAttribute("estados", EstadoVehiculo.values());
            model.addAttribute("origenes", OrigenVehiculo.values());
            model.addAttribute("propietarios", Propietario.values());
            model.addAttribute("tiposVehiculo", TipoVehiculo.values());
            model.addAttribute("error", e.getMessage());
            return "crearVehiculo";
        }

    }

    @GetMapping("/modificar/{id}")
    public String modificarVehiculo(@PathVariable("id") Long id, Model model) {
        VehiculoDTO vehiculoDTO = vehiculoService.obtenerVehiculoDTO(id);
        model.addAttribute("vehiculo", vehiculoDTO);
        model.addAttribute("modelos", vehiculoService.listarModelos());
        model.addAttribute("estados", EstadoVehiculo.values());
        model.addAttribute("origenes", OrigenVehiculo.values());
        model.addAttribute("propietarios", Propietario.values());
        model.addAttribute("tiposVehiculo", TipoVehiculo.values());
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
            model.addAttribute("estados", EstadoVehiculo.values());
            model.addAttribute("origenes", OrigenVehiculo.values());
            model.addAttribute("propietarios", Propietario.values());
            model.addAttribute("tiposVehiculo", TipoVehiculo.values());
            model.addAttribute("id", id);
            return "modificarVehiculo";
        }

        try {
            vehiculoService.actualizarVehiculo(id, vehiculoDTO);
            redirectAttributes.addFlashAttribute("exito", "Vehículo actualizado correctamente");
            return "redirect:/vehiculo/listado";
        } catch (PatenteDuplicadaException e) {
            model.addAttribute("modelos", vehiculoService.listarModelos());
            model.addAttribute("estados", EstadoVehiculo.values());
            model.addAttribute("origenes", OrigenVehiculo.values());
            model.addAttribute("propietarios", Propietario.values());
            model.addAttribute("tiposVehiculo", TipoVehiculo.values());
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
    public String detalleVehiculo(@PathVariable("id") Long id, Model model) {
        Vehiculo vehiculo = vehiculoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        model.addAttribute("vehiculo", vehiculo);
        return "detalleVehiculo";
    }

    @PostMapping("/importarVehiculos")
    public String cargarExcel(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        try {
            vehiculoImportService.importarExcel(file);
            ra.addFlashAttribute("ImportacionExito", "La flota se ha cargado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("ImportacionError", "Error al procesar el archivo: " + e.getMessage());
        }
        return "redirect:/vehiculo/listado";
    }

    @GetMapping("/importarVehiculos")
    public String plantilaDeCargaMasivaDeVehiculos() {
        return "importarVehiculos";
    }
}
