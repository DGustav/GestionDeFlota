package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.service.implementacion.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsignacionController {
    @Autowired
    private AsignacionService service;
}
