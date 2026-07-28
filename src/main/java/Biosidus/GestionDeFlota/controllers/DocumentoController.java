package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.DocumentoDTO;
import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.documento.EstadoDocumento;
import Biosidus.GestionDeFlota.domain.documento.TipoDocumento;
import Biosidus.GestionDeFlota.services.AsignacionService;
import Biosidus.GestionDeFlota.services.DocumentoService;
import Biosidus.GestionDeFlota.services.ExcelReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/documento")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private ExcelReportService excelReportService;

    @GetMapping("/listado")
    public String listadoDocumentos(Model model,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "estado", required = false) EstadoDocumento estado,
                                   @RequestParam(name = "tipo", required = false) TipoDocumento tipo,
                                   @RequestParam(name = "buscar", required = false) String buscar) {

        if(buscar != null){
            buscar = buscar.strip();
        }

        Page<Documento> pagina = documentoService.listarTodosLosDocumentos(page, estado, tipo, buscar);
        
        model.addAttribute("documentos", pagina.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("tamanioPagina", pagina.getSize());
        model.addAttribute("totalItems", pagina.getTotalElements());
        
        model.addAttribute("tipos", TipoDocumento.values());
        model.addAttribute("estados", EstadoDocumento.values());
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("busqueda", buscar);
        model.addAttribute("searchAction", "/documento/listado");
        
        return "listadoDocumentos";
    }

    @GetMapping("/crear")
    public String crearDocumento(Model model) {
        model.addAttribute("documento", new DocumentoDTO());
        model.addAttribute("tipos", TipoDocumento.values());
        model.addAttribute("estados", EstadoDocumento.values());
        model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
        model.addAttribute("conductores", asignacionService.listarConductores());
        return "crearDocumento";
    }

    @PostMapping("/guardar")
    public String guardarDocumento(@Valid @ModelAttribute("documento") DocumentoDTO documentoDTO,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoDocumento.values());
            model.addAttribute("estados", EstadoDocumento.values());
            model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
            model.addAttribute("conductores", asignacionService.listarConductores());
            return "crearDocumento";
        }

        try {
            documentoService.guardarDocumento(documentoDTO);
            redirectAttributes.addFlashAttribute("exito", "Documento guardado correctamente");
            return "redirect:/documento/listado";
            } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el documento: " + e.getMessage());
            model.addAttribute("tipos", TipoDocumento.values());
            model.addAttribute("estados", EstadoDocumento.values());
            model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
            model.addAttribute("conductores", asignacionService.listarConductores());
            return "crearDocumento";
        }

    }

    @GetMapping("/modificar/{id}")
    public String modificarDocumento(@PathVariable Long id, Model model) {
        DocumentoDTO documentoDTO = documentoService.buscarDTOporId(id);
        model.addAttribute("documento", documentoDTO);
        model.addAttribute("tipos", TipoDocumento.values());
        model.addAttribute("estados", EstadoDocumento.values());
        model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
        model.addAttribute("conductores", asignacionService.listarConductores());
        return "modificarDocumento";
    }

    @PostMapping("/actualizar")
    public String actualizarDocumento(@Valid @ModelAttribute("documento") DocumentoDTO documentoDTO,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoDocumento.values());
            model.addAttribute("estados", EstadoDocumento.values());
            model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
            model.addAttribute("conductores", asignacionService.listarConductores());
            return "modificarDocumento";
        }

        try {
            documentoService.guardarDocumento(documentoDTO);
            redirectAttributes.addFlashAttribute("exito", "Documento actualizado correctamente");
            return "redirect:/documento/listado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el documento: " + e.getMessage());
            model.addAttribute("tipos", TipoDocumento.values());
            model.addAttribute("estados", EstadoDocumento.values());
            model.addAttribute("vehiculos", asignacionService.listarTodosLosVehiculos());
            model.addAttribute("conductores", asignacionService.listarConductores());
            return "modificarDocumento";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalleDocumento(@PathVariable Long id, Model model) {
        Documento documento = documentoService.buscarPorId(id);
        model.addAttribute("documento", documento);
        return "detalleDocumento";
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long id) {
        try {
            return documentoService.descargarArchivo(id);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<InputStreamResource> exportarExcel(
            @RequestParam(name = "estado", required = false) EstadoDocumento estado,
            @RequestParam(name = "tipo", required = false) TipoDocumento tipo,
            @RequestParam(name = "buscar", required = false) String buscar) {

        try {
            List<Documento> documentos = documentoService.listarDocumentosParaReporte(estado, tipo, buscar);
            ByteArrayInputStream stream = excelReportService.generarReporteExcelDocumento(documentos);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_documentos.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(stream));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
