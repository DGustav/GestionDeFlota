package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.domain.Flotas.Service;
import Biosidus.GestionDeFlota.service.implementacion.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {
    @Autowired
    private ServiceService servicio;

}
