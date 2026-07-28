package Biosidus.GestionDeFlota.services;

import Biosidus.GestionDeFlota.domain.vehiculo.*;
import Biosidus.GestionDeFlota.repositorios.ModeloRepositorio;
import Biosidus.GestionDeFlota.repositorios.VehiculoRepositorio;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class VehiculoImportService {

    @Autowired
    private VehiculoRepositorio vehiculoRepo;

    @Autowired
    private ModeloRepositorio modeloRepo;

    @Transactional
    public void importarExcel(MultipartFile archivo) throws IOException {

        Workbook workbook = new XSSFWorkbook(archivo.getInputStream());

        System.out.println("=== HOJAS ENCONTRADAS ===");

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            System.out.println(workbook.getSheetName(i));
        }

        Map<String, Modelo> cacheModelos = new HashMap<>();

        List<Vehiculo> vehiculosAGuardar = new ArrayList<>();

        procesarHoja(
                workbook.getSheet("Activos"),
                EstadoVehiculo.ACTIVO,
                cacheModelos,
                vehiculosAGuardar
        );

        procesarHoja(
                workbook.getSheet("bajas"),
                EstadoVehiculo.BAJA,
                cacheModelos,
                vehiculosAGuardar
        );

        procesarHoja(
                workbook.getSheet("leasing"),
                EstadoVehiculo.ACTIVO,
                cacheModelos,
                vehiculosAGuardar
        );

        procesarHoja(
                workbook.getSheet("pendientes_de_baja"),
                EstadoVehiculo.PENDIENTE_DE_BAJA,
                cacheModelos,
                vehiculosAGuardar
        );

        vehiculoRepo.saveAll(vehiculosAGuardar);

        workbook.close();

        System.out.println("Vehículos importados: " + vehiculosAGuardar.size());
    }

    private void procesarHoja(
            Sheet hoja,
            EstadoVehiculo estadoDefault,
            Map<String, Modelo> cacheModelos,
            List<Vehiculo> vehiculosAGuardar
    ) {

        if (hoja == null) {
            System.out.println("La hoja no existe.");
            throw new RuntimeException("La hoja no tiene el formato esperado.");
        }

        System.out.println("Procesando hoja: " + hoja.getSheetName());

        int filasVaciasSeguidas = 0;

        for (int i = 2; i <= hoja.getLastRowNum(); i++) {

            try {

                Row fila = hoja.getRow(i);

                // Ignorar filas vacías
                if (fila == null || filaVacia(fila)) {

                    filasVaciasSeguidas++;

                    // Cortar si hay demasiadas vacías seguidas
                    if (filasVaciasSeguidas >= 50) {
                        System.out.println("Fin detectado en hoja: " + hoja.getSheetName());
                        break;
                    }

                    continue;
                }

                filasVaciasSeguidas = 0;

                String patente = getCellValue(fila.getCell(7))
                        .replaceAll("\n+", "")
                        .strip()
                        .trim()
                        .toUpperCase();

                if (patente.isBlank()) {
                    continue;
                }

                // Evitar duplicados
                if (vehiculoRepo.findByPatente(patente).isPresent()) {
                    System.out.println("Patente duplicada: " + patente);
                    continue;
                }

                Vehiculo vehiculo = new Vehiculo();

                // =========================
                // PROPIETARIO
                // =========================

                vehiculo.setPropietario(
                        parseEnum(
                                Propietario.class,
                                getCellValue(fila.getCell(1))
                        )
                );

                // =========================
                // ORIGEN
                // =========================

                vehiculo.setOrigen(
                        parseEnum(
                                OrigenVehiculo.class,
                                getCellValue(fila.getCell(3))
                        )
                );

                // =========================
                // DATOS VEHÍCULO
                // =========================

                vehiculo.setPatente(patente);

                vehiculo.setMotor(
                        getCellValue(fila.getCell(8)).trim()
                );

                vehiculo.setChasis(
                        getCellValue(fila.getCell(9)).trim()
                );

                // =========================
                // NOTA
                // =========================
                vehiculo.setNota(
                        getCellValue(fila.getCell(10)).trim()
                );
                // =========================
                // MODELO
                // =========================

                String nombreModelo = getCellValue(fila.getCell(5))
                        .trim()
                        .toUpperCase();

                Modelo modelo = cacheModelos.get(nombreModelo);

                if (modelo == null) {

                    modelo = modeloRepo
                            .findByNombreCompletoModelo(nombreModelo)
                            .orElseGet(() -> {

                                Modelo nuevoModelo = new Modelo();

                                nuevoModelo.setNombreCompletoModelo(nombreModelo);

                                String anioStr = getCellValue(fila.getCell(6));

                                if (!anioStr.isBlank()) {
                                    nuevoModelo.setAnio(
                                            Integer.parseInt(anioStr)
                                    );
                                }

                                if(getCellValue(fila.getCell(4)).trim().equalsIgnoreCase("4X4")){
                                    nuevoModelo.setTipoVehiculo(TipoVehiculo.CUATRO_X4);
                                }else{
                                    nuevoModelo.setTipoVehiculo(
                                            parseEnum(
                                                    TipoVehiculo.class,
                                                    getCellValue(fila.getCell(4))
                                            )
                                    );
                                }
                                return modeloRepo.save(nuevoModelo);
                            });

                    cacheModelos.put(nombreModelo, modelo);
                }

                vehiculo.setModelo(modelo);

                // =========================
                // ESTADO
                // =========================

                vehiculo.setEstado(estadoDefault);

                vehiculo.setDisponible(
                        estadoDefault == EstadoVehiculo.ACTIVO
                );

                // =========================
                // VALORES DEFAULT
                // =========================

                vehiculo.setKilometrajeActual(0);

                vehiculo.setFechaAdquisicion(null);

                vehiculosAGuardar.add(vehiculo);

                System.out.println("Importado: " + patente);

            } catch (Exception e) {

                System.out.println(
                        "ERROR en hoja "
                                + hoja.getSheetName()
                                + " fila "
                                + i
                );

                e.printStackTrace();
            }
        }
    }

    private boolean filaVacia(Row fila) {

        if (fila == null) {
            return true;
        }

        for (Cell cell : fila) {

            if (cell != null &&
                    cell.getCellType() != CellType.BLANK &&
                    !getCellValue(cell).trim().isEmpty()) {

                return false;
            }
        }

        return true;
    }

    private String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {

            case STRING ->
                    cell.getStringCellValue().trim();

            case NUMERIC -> {

                double value = cell.getNumericCellValue();

                // Evitar 123.0
                if (value == (long) value) {
                    yield String.valueOf((long) value);
                }

                yield String.valueOf(value);
            }

            case BOOLEAN ->
                    String.valueOf(cell.getBooleanCellValue());

            case FORMULA -> {

                try {
                    yield cell.getStringCellValue().trim();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }

            default -> "";
        };
    }

    private <T extends Enum<T>> T parseEnum(
            Class<T> enumClass,
            String valor
    ) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return Enum.valueOf(
                enumClass,
                valor.trim()
                        .toUpperCase()
                        .replaceAll("\\s+", "_")
        );
    }
}