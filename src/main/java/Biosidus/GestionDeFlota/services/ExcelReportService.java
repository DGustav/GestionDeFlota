package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.domain.documento.Documento;
import Biosidus.GestionDeFlota.domain.asignacion.Asignacion;
import Biosidus.GestionDeFlota.domain.empleado.Conductor;
import Biosidus.GestionDeFlota.domain.siniestro.Siniestro;
import Biosidus.GestionDeFlota.domain.mantenimientoVehicular.MantenimientoVehicular;
import Biosidus.GestionDeFlota.domain.vehiculo.Vehiculo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelReportService {

    public ByteArrayInputStream generarReporteExcelSiniestro(List<Siniestro> siniestros) throws IOException {
        String[] columnas = {"ID Siniestro", "Fecha y Hora", "Patente", "Vehículo / Modelo", "Descripción del Hecho", "Cantidad de Pruebas"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Siniestros");

            // --- ESTILOS ---
            // Fuente para cabecera
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            // Estilo para celda cabecera (Azul Corporativo Biosidus)
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            // Estilo para fechas/datos comunes
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);

            // --- RENDERIZAR CABECERA ---
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- RENDERIZAR DATOS ---
            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Siniestro siniestro : siniestros) {
                Row row = sheet.createRow(rowIdx++);

                // ID
                Cell cellId = row.createCell(0);
                cellId.setCellValue(siniestro.getId());
                cellId.setCellStyle(dataCellStyle);

                // Fecha y Hora
                Cell cellFecha = row.createCell(1);
                cellFecha.setCellValue(siniestro.getFechaSiniestro().format(formatter));
                cellFecha.setCellStyle(dataCellStyle);

                // Patente
                Cell cellPatente = row.createCell(2);
                cellPatente.setCellValue(siniestro.getVehiculo().getPatente());
                cellPatente.setCellStyle(dataCellStyle);

                // Vehículo Modelo
                Cell cellModelo = row.createCell(3);
                cellModelo.setCellValue(siniestro.getVehiculo().getModelo().getNombreCompletoModelo());
                cellModelo.setCellStyle(dataCellStyle);

                // Descripción
                Cell cellDesc = row.createCell(4);
                cellDesc.setCellValue(siniestro.getDescripcion() != null ? siniestro.getDescripcion() : "-");
                cellDesc.setCellStyle(dataCellStyle);

                // Cantidad de Pruebas asignadas en el ElementCollection
                Cell cellPruebas = row.createCell(5);
                int totalPruebas = siniestro.getPruebas() != null ? siniestro.getPruebas().size() : 0;
                cellPruebas.setCellValue(totalPruebas);
                cellPruebas.setCellStyle(dataCellStyle);
            }

            // Autoajustar el ancho de las columnas según el contenido
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReporteExcelVehiculo(List<Vehiculo> vehiculos) throws IOException {
        String[] columnas = {"ID", "Patente", "Modelo", "Año", "Tipo", "Fecha Adquisición", "Kilometraje", "Disponible", "Estado", "Origen", "Propietario"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Vehículos");

            // --- ESTILOS ---
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);

            // --- RENDERIZAR CABECERA ---
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- RENDERIZAR DATOS ---
            int rowIdx = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Vehiculo vehiculo : vehiculos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(vehiculo.getId());
                row.createCell(1).setCellValue(vehiculo.getPatente());
                row.createCell(2).setCellValue(vehiculo.getModelo() != null ? vehiculo.getModelo().getNombreCompletoModelo() : "-");
                row.createCell(3).setCellValue(vehiculo.getModelo() != null && vehiculo.getModelo().getAnio() != null ? vehiculo.getModelo().getAnio().toString() : "-");
                row.createCell(4).setCellValue(vehiculo.getModelo() != null && vehiculo.getModelo().getTipoVehiculo() != null ? vehiculo.getModelo().getTipoVehiculo().toString() : "-");
                row.createCell(5).setCellValue(vehiculo.getFechaAdquisicion() != null ? vehiculo.getFechaAdquisicion().format(dateFormatter) : "-");
                row.createCell(6).setCellValue(vehiculo.getKilometrajeActual() != null ? vehiculo.getKilometrajeActual() : 0);
                row.createCell(7).setCellValue(vehiculo.getDisponible() ? "SÍ" : "NO");
                row.createCell(8).setCellValue(vehiculo.getEstado() != null ? vehiculo.getEstado().toString() : "-");
                row.createCell(9).setCellValue(vehiculo.getOrigen() != null ? vehiculo.getOrigen().toString() : "-");
                row.createCell(10).setCellValue(vehiculo.getPropietario() != null ? vehiculo.getPropietario().toString() : "-");

                for (int i = 0; i < columnas.length; i++) {
                    row.getCell(i).setCellStyle(dataCellStyle);
                }
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReporteExcelAsignacion(List<Asignacion> asignaciones) throws IOException {
        String[] columnas = {"ID", "Vehículo", "Patente", "Conductor", "Legajo", "Estado", "Fecha Asignación", "Fecha Dev. Estimada", "Fecha Dev. Real", "Km Inicial", "Km Recorridos", "Km Final"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Asignaciones");

            // --- ESTILOS ---
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);

            // --- RENDERIZAR CABECERA ---
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- RENDERIZAR DATOS ---
            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Asignacion asignacion : asignaciones) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(asignacion.getId());
                row.createCell(1).setCellValue(asignacion.getVehiculo().getModelo().getNombreCompletoModelo());
                row.createCell(2).setCellValue(asignacion.getVehiculo().getPatente());
                row.createCell(3).setCellValue(asignacion.getConductor().getNomApe());
                row.createCell(4).setCellValue(asignacion.getConductor().getLegajo());
                row.createCell(5).setCellValue(asignacion.getEstado().toString());
                row.createCell(6).setCellValue(asignacion.getFechaAsignacion().format(formatter));
                row.createCell(7).setCellValue(asignacion.getFechaDevolucionEstimada() != null ? asignacion.getFechaDevolucionEstimada().format(formatter) : "-");
                row.createCell(8).setCellValue(asignacion.getFechaDevolucion() != null ? asignacion.getFechaDevolucion().format(formatter) : "-");
                row.createCell(9).setCellValue(asignacion.getKilometrajeInicial() != null ? asignacion.getKilometrajeInicial() : 0);
                row.createCell(10).setCellValue(asignacion.getKilometrosRecorridos() != null ? asignacion.getKilometrosRecorridos() : 0);
                row.createCell(11).setCellValue(asignacion.getKilometrajeFinal() != null ? asignacion.getKilometrajeFinal() : 0);

                for (int i = 0; i < columnas.length; i++) {
                    row.getCell(i).setCellStyle(dataCellStyle);
                }
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReporteExcelConductor(List<Conductor> conductores) throws IOException {
        String[] columnas = {"ID", "Nombre y Apellido", "DNI", "Legajo", "Usuario", "E-mail", "Categoría Bio"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Conductores");

            // --- ESTILOS ---
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);

            // --- RENDERIZAR CABECERA ---
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- RENDERIZAR DATOS ---
            int rowIdx = 1;
            for (Conductor conductor : conductores) {
                Row row = sheet.createRow(rowIdx++);

                Cell cellId = row.createCell(0);
                cellId.setCellValue(conductor.getId());
                cellId.setCellStyle(dataCellStyle);

                Cell cellNomApe = row.createCell(1);
                cellNomApe.setCellValue(conductor.getNomApe());
                cellNomApe.setCellStyle(dataCellStyle);

                Cell cellDni = row.createCell(2);
                cellDni.setCellValue(conductor.getDni());
                cellDni.setCellStyle(dataCellStyle);

                Cell cellLegajo = row.createCell(3);
                cellLegajo.setCellValue(conductor.getLegajo());
                cellLegajo.setCellStyle(dataCellStyle);

                Cell cellUsuario = row.createCell(4);
                cellUsuario.setCellValue(conductor.getUsuario().getNickname());
                cellUsuario.setCellStyle(dataCellStyle);

                Cell cellMail = row.createCell(5);
                cellMail.setCellValue(conductor.getMail());
                cellMail.setCellStyle(dataCellStyle);

                Cell cellRango = row.createCell(6);
                cellRango.setCellValue(conductor.getRango().toString());
                cellRango.setCellStyle(dataCellStyle);
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReporteExcelMantenimiento(List<MantenimientoVehicular> mantenimientos) throws IOException {
        String[] columnas = {"ID", "Patente", "Modelo", "Tipo", "Descripción", "Fecha de Service", "Devolución Estimada", "Devolución Real", "Kilometraje"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Mantenimientos");

            // --- ESTILOS ---
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);

            // --- RENDERIZAR CABECERA ---
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- RENDERIZAR DATOS ---
            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (MantenimientoVehicular m : mantenimientos) {
                Row row = sheet.createRow(rowIdx++);

                // ID
                Cell cellId = row.createCell(0);
                cellId.setCellValue(m.getId());
                cellId.setCellStyle(dataCellStyle);

                // Patente
                Cell cellPatente = row.createCell(1);
                cellPatente.setCellValue(m.getVehiculo().getPatente());
                cellPatente.setCellStyle(dataCellStyle);

                // Modelo
                Cell cellModelo = row.createCell(2);
                cellModelo.setCellValue(m.getVehiculo().getModelo().getNombreCompletoModelo());
                cellModelo.setCellStyle(dataCellStyle);

                // Tipo
                Cell cellTipo = row.createCell(3);
                cellTipo.setCellValue(m.getTipo() != null ? m.getTipo().toString() : "-");
                cellTipo.setCellStyle(dataCellStyle);

                // Descripción
                Cell cellDesc = row.createCell(4);
                cellDesc.setCellValue(m.getDescripcion() != null ? m.getDescripcion() : "-");
                cellDesc.setCellStyle(dataCellStyle);

                // Fecha Service
                Cell cellFecha = row.createCell(5);
                cellFecha.setCellValue(m.getFechaAsignacion() != null ? m.getFechaAsignacion().format(formatter) : "-");
                cellFecha.setCellStyle(dataCellStyle);

                // Devolución Estimada
                Cell cellEstimada = row.createCell(6);
                cellEstimada.setCellValue(m.getFechaDevolucionEstimada() != null ? m.getFechaDevolucionEstimada().format(formatter) : "-");
                cellEstimada.setCellStyle(dataCellStyle);

                // Devolución Real
                Cell cellReal = row.createCell(7);
                cellReal.setCellValue(m.getFechaDevolucion() != null ? m.getFechaDevolucion().format(formatter) : "-");
                cellReal.setCellStyle(dataCellStyle);

                // Kilometraje
                Cell cellKm = row.createCell(8);
                cellKm.setCellValue(m.getKilometrajeActual() != null ? m.getKilometrajeActual() : 0);
                cellKm.setCellStyle(dataCellStyle);
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReporteExcelDocumento(List<Documento> documentos) throws IOException {
        String[] columnas = {"ID", "Tipo de Documento", "Vencimiento", "Asociado a", "Estado"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Documentos");

            // --- ESTILOS ---
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);

            // --- RENDERIZAR CABECERA ---
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // --- RENDERIZAR DATOS ---
            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Documento doc : documentos) {
                Row row = sheet.createRow(rowIdx++);

                // ID
                Cell cellId = row.createCell(0);
                cellId.setCellValue(doc.getId());
                cellId.setCellStyle(dataCellStyle);

                // Tipo
                Cell cellTipo = row.createCell(1);
                cellTipo.setCellValue(doc.getTipoDocumento().toString());
                cellTipo.setCellStyle(dataCellStyle);

                // Vencimiento
                Cell cellVenc = row.createCell(2);
                cellVenc.setCellValue(doc.getFechaVencimiento() != null ? doc.getFechaVencimiento().format(formatter) : "-");
                cellVenc.setCellStyle(dataCellStyle);

                // Asociado a
                Cell cellAsoc = row.createCell(3);
                String asociadoA = "-";
                if (doc.getVehiculo() != null) {
                    asociadoA = "Vehículo: " + doc.getVehiculo().getPatente();
                } else if (doc.getConductor() != null) {
                    asociadoA = "Conductor: " + doc.getConductor().getNomApe();
                }
                cellAsoc.setCellValue(asociadoA);
                cellAsoc.setCellStyle(dataCellStyle);

                // Estado
                Cell cellEstado = row.createCell(4);
                cellEstado.setCellValue(doc.getEstadoDocumento().toString());
                cellEstado.setCellStyle(dataCellStyle);
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
