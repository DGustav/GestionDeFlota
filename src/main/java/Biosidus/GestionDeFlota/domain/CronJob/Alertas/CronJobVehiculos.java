package Biosidus.GestionDeFlota.domain.CronJob.Alertas;

import Biosidus.GestionDeFlota.domain.CronJob.Alertas.Alertas.Command;
import Biosidus.GestionDeFlota.domain.Flotas.Vehiculo;

import java.util.List;

public class CronJobVehiculos {
    private List<Command> alertas;
    private List<Vehiculo> flota;
}
