package Biosidus.GestionDeFlota.domain.CronJob.Alertas.Criterios;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class AnalizadorKilometros {
    private Integer kilometroLimite = 10000;

    public Boolean usado(Integer kilometros){
        if(kilometros > kilometroLimite){
            return TRUE;
        }
        else{
            return FALSE;
        }
    }
}
