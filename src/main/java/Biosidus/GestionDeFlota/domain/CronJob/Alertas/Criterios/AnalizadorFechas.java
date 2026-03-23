package Biosidus.GestionDeFlota.domain.CronJob.Alertas.Criterios;

import java.time.LocalDate;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class AnalizadorFechas {

    public Boolean vencio(LocalDate fecha) {
        if (fecha.isAfter(LocalDate.now())) {
            return TRUE;
        }
        else {
            return FALSE;
        }
    }
}

