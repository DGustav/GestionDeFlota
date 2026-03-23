package Biosidus.GestionDeFlota.domain.Documentos;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="documento")

public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated (EnumType.STRING)
    @Column(name="tipo_documento",nullable = false)
    private TipoDocumento tipoDocumento;
    @Column(name="fecha_vencimiento",nullable = false)
    private LocalDateTime fechaVencimiento;
    @Column(name="fotos")
    private String imagenes;
}
