package Biosidus.GestionDeFlota.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documento")
public class DocumentoController {
    @GetMapping("/listado")
    public String listadoDocumentos(Model model) {
        return "listadoDocumentos";
    }

    @GetMapping("/crear")
    public String crearDocumento(Model model) {
        return "crearDocumento";
    }

    @GetMapping("/modificar/{id}")
    public String modificarDocumento(Model model) {
        return "modificarDocumento";
    }

    @GetMapping("/detalle/{id}")
    public String detalleDocumento(Model model) {
        return "detalleDocumento";
    }
}
