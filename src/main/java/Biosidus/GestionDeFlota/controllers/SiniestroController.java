package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.DTOs.SiniestroDTO;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.siniestro.Siniestro;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import Biosidus.GestionDeFlota.services.ExcelReportService;
import Biosidus.GestionDeFlota.services.SiniestroService;
import Biosidus.GestionDeFlota.services.VehiculoService;
import Biosidus.GestionDeFlota.services.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/siniestro")
public class SiniestroController {
    @Value("${spring.servlet.multipart.location}")
    private String ruta_uploads;

    @Autowired
    private SiniestroService siniestroService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ExcelReportService excelReportService;

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping("/listado")
    public String listadoSiniestros(Model model,
                                    @RequestParam(name = "buscar", required = false) String buscar,
                                    @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                    @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                                    @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                                    @RequestParam(defaultValue = "0") int page,
                                    Authentication authentication) {

        Long vehiculoId = null;
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTOR"))) {
            vehiculoId = asignacionService.obtenerUltimaAsignacionActivaPorUsuario(authentication.getName())
                    .map(a -> a.getVehiculo().getId())
                    .orElse(-1L); // -1L para que no devuelva nada si no tiene vehículo
        }

        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "fechaSiniestro"));
        Page<Siniestro> siniestrosPage = siniestroService.listarSiniestros(buscar, fecha, fechaDesde, fechaHasta, vehiculoId, pageable);

        if(buscar != null){
            buscar = buscar.strip();
        }

        model.addAttribute("siniestros", siniestrosPage.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPages", siniestrosPage.getTotalPages());
        model.addAttribute("totalItems", siniestrosPage.getTotalElements());
        model.addAttribute("tamanioPagina", pageSize);
        model.addAttribute("busqueda", buscar);
        model.addAttribute("fecha", fecha);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "listadoSiniestros";
    }

    @GetMapping("/crear")
    public String crearSiniestro(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {

        SiniestroDTO dto = new SiniestroDTO();

        boolean esConductor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTOR"));

        if (esConductor) {

            Optional<Asignacion> asignacionOpt = asignacionService
                    .obtenerUltimaAsignacionActivaPorUsuario(authentication.getName());

            if (asignacionOpt.isEmpty()) {
                model.addAttribute("error", "No posee un vehículo asignado. Debe tener una asignación activa para registrar un siniestro.");
                model.addAttribute("esConductor", true);
                model.addAttribute("siniestro", dto);
                return "crearSiniestro";
            }

            Asignacion asignacion = asignacionOpt.get();
            dto.setVehiculoId(asignacion.getVehiculo().getId());
            dto.setNombreVehiculo(
                    STR."\{asignacion.getVehiculo().getPatente()} - \{asignacion.getVehiculo().getModelo().getNombreCompletoModelo()}"
            );

            model.addAttribute("esConductor", true);

        } else {

            model.addAttribute("vehiculos", vehiculoService.listarVehiculos());
            model.addAttribute("esConductor", false);
        }

        model.addAttribute("siniestro", dto);

        return "crearSiniestro";
    }

    @PostMapping("/crear")
    public String guardarSiniestro(@Valid @ModelAttribute("siniestro") SiniestroDTO siniestroDTO,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) {

        boolean esConductor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTOR"));

        try {

            // Si es conductor, el vehículo se obtiene automáticamente
            if (esConductor) {
                Asignacion asignacion = asignacionService
                        .obtenerUltimaAsignacionActivaPorUsuario(authentication.getName())
                        .orElseThrow(() -> new RuntimeException("No posee vehículo asignado"));

                siniestroDTO.setVehiculoId(asignacion.getVehiculo().getId());
            }

            // Validaciones
            if (result.hasErrors() || (!esConductor && siniestroDTO.getVehiculoId() == null)) {

                if (!esConductor &&
                        siniestroDTO.getVehiculoId() == null &&
                        !result.hasFieldErrors("vehiculoId")) {

                    model.addAttribute("error",
                            "Debe seleccionar un vehículo de la lista.");
                }

                if (!esConductor) {
                    model.addAttribute("vehiculos",
                            vehiculoService.listarVehiculos());
                }

                model.addAttribute("esConductor", esConductor);
                model.addAttribute("siniestro", siniestroDTO);

                return "crearSiniestro";
            }

            siniestroService.guardarSiniestro(siniestroDTO);

            redirectAttributes.addFlashAttribute(
                    "exito",
                    "El siniestro se ha registrado correctamente."
            );

            return "redirect:/siniestro/listado";

        } catch (IOException e) {

            model.addAttribute("error",
                    "Error al guardar los archivos de prueba: " + e.getMessage());

            if (!esConductor) {
                model.addAttribute("vehiculos",
                        vehiculoService.listarVehiculos());
            }

            model.addAttribute("esConductor", esConductor);
            model.addAttribute("siniestro", siniestroDTO);

            return "crearSiniestro";

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());

            if (!esConductor) {
                model.addAttribute("vehiculos",
                        vehiculoService.listarVehiculos());
            }

            model.addAttribute("esConductor", esConductor);
            model.addAttribute("siniestro", siniestroDTO);

            return "crearSiniestro";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificarSiniestro(@PathVariable Long id, Model model) {
        try {
            SiniestroDTO siniestroDTO = siniestroService.obtenerSiniestroDTOPorId(id);
            Siniestro siniestro = siniestroService.obtenerSiniestroPorId(id);
            model.addAttribute("ruta_uploads", ruta_uploads);
            model.addAttribute("siniestro", siniestroDTO);
            model.addAttribute("siniestroOriginal", siniestro); // Para ver las pruebas actuales
            return "modificarSiniestro";
        } catch (RuntimeException e) {
            return "redirect:/siniestro/listado";
        }
    }

    @PostMapping("/modificar/{id}")
    public String actualizarSiniestro(@PathVariable Long id,
                                      @Valid @ModelAttribute("siniestro") SiniestroDTO siniestroDTO,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {


        if (result.hasErrors()) {
            Siniestro siniestro = siniestroService.obtenerSiniestroPorId(id);
            model.addAttribute("ruta_uploads", ruta_uploads);
            model.addAttribute("siniestroOriginal", siniestro);
            // Aseguramos que el nombre del vehículo esté presente si hubo error de validación
            siniestroDTO.setNombreVehiculo(siniestro.getVehiculo().getPatente() + " - " + siniestro.getVehiculo().getModelo().getNombreCompletoModelo());
            return "modificarSiniestro";
        }

        try {
            siniestroDTO.setId(id);
            siniestroService.actualizarSiniestro(siniestroDTO);
            redirectAttributes.addFlashAttribute("exito", "El siniestro se ha actualizado correctamente.");
            return "redirect:/siniestro/listado";
        } catch (IOException e) {
            model.addAttribute("error", "Error al guardar los archivos: " + e.getMessage());
            Siniestro siniestro = siniestroService.obtenerSiniestroPorId(id);
            model.addAttribute("ruta_uploads", ruta_uploads);
            model.addAttribute("siniestroOriginal", siniestro);
            // Aseguramos que el nombre del vehículo esté presente si hubo error
            siniestroDTO.setNombreVehiculo(siniestro.getVehiculo().getPatente() + " - " + siniestro.getVehiculo().getModelo().getNombreCompletoModelo());
            return "modificarSiniestro";
        }
    }


    @GetMapping("/detalle/{id}")
    public String detalleSiniestro(@PathVariable Long id, Model model) {
        Siniestro siniestro = siniestroService.obtenerSiniestroPorId(id);
        model.addAttribute("ruta_uploads", ruta_uploads);
        model.addAttribute("siniestro", siniestro);
        return "detalleSiniestro";
    }

    @GetMapping("/mis-siniestro")
    public String misSiniestro(Model model, Authentication principal) {
        return "redirect:/siniestro/listado";
    }

    @GetMapping("/descargar-prueba")
    public ResponseEntity<Resource> descargarPrueba(@RequestParam String ruta) {
        try {
            return siniestroService.descargarPrueba(ruta);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<org.springframework.core.io.InputStreamResource> exportarExcel(
            @RequestParam(name = "buscar", required = false) String buscar,
            @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {

        try {
            // 1. Buscamos la totalidad de registros que coinciden con los filtros actuales de la pantalla
            List<Siniestro> siniestrosFiltrados = siniestroService.listarSiniestrosParaReporte(buscar, fecha, fechaDesde, fechaHasta);

            // 2. Generamos el flujo binario del Excel
            ByteArrayInputStream stream = excelReportService.generarReporteExcelSiniestro(siniestrosFiltrados);

            // 3. Preparamos los encabezados HTTP para la descarga del archivo (.xlsx)
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_siniestros.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new org.springframework.core.io.InputStreamResource(stream));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/mis-siniestros")
    public String obtenerMisSiniestros(Model model, Authentication authentication){
        return "redirect:/siniestro/listado";
    }
}
