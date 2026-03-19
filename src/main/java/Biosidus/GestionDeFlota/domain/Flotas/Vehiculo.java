package Biosidus.GestionDeFlota.domain.Flotas;

import Biosidus.GestionDeFlota.domain.Documentos.Documento;

import java.time.LocalDate;
import java.util.List;

public class Vehiculo {
    //@ID
    //private Long id;
    private String patente;
    private Modelo modelo;
    private LocalDate fechaAdquisicion;
    private Integer kilometrajeActual;
    private List<Documento> documentos;
    private Boolean disponible;
    private Boolean enService;
    private LocalDate ultimoService;
}
