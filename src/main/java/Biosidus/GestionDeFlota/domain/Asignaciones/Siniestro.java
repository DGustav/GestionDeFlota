package Biosidus.GestionDeFlota.domain.Asignaciones;

import java.time.LocalDateTime;
import java.util.List;

public class Siniestro {
    //@ID
    //private Long id;
    private Asignacion contexto;
    private LocalDateTime fechaSiniestro;
    private String descripcion;
    private List<String> prubeas;
}
