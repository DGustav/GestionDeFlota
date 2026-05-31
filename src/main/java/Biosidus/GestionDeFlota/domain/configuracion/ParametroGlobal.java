package Biosidus.GestionDeFlota.domain.configuracion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parametrosGlobales")
@Getter
@Setter
public class ParametroGlobal {

    @Id
    @Column(name = "clave", nullable = false, unique = true)
    private String clave; // Ej: "DIAS_ALERTA_VENCIMIENTO"

    @Column(name = "valor", nullable = false)
    private String valor; // Ej: "30"

    @Column(name = "descripcion")
    private String descripcion; // Ej: "Días de anticipación para avisar vencimientos"
}
