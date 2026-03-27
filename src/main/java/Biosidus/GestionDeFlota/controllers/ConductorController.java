package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.domain.Documentos.Documento;
import Biosidus.GestionDeFlota.domain.Empleados.Area;
import Biosidus.GestionDeFlota.domain.Empleados.Conductor;
import Biosidus.GestionDeFlota.domain.Empleados.Usuario;
import Biosidus.GestionDeFlota.service.implementacion.ConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConductorController {
    @Autowired
    private ConductorService service;

    public void crearConductor(String nombre, String dni, String mail, String legajo,
                               Area area, Documento doc, Usuario us){
        Conductor conductor = new Conductor();
        conductor.setNomApe(nombre);
        conductor.setDni(dni);
        conductor.setMail(mail);
        conductor.setLegajo(legajo);
        conductor.setArea(area);
        conductor.setUsuario(us);
        conductor.setLicencia(doc);
        service.saveConductor(conductor);
    };
}
