package Biosidus.GestionDeFlota.domain.Documentos;



import java.time.LocalDateTime;

public class Documento {
    //@ID
    //private Long id;
    private TipoDocumento tipoDocumento;
    private LocalDateTime fechaVencimiento;
    private String imagenes;
}
