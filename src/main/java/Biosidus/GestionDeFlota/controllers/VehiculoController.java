package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.service.implementacion.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VehiculoController {
    @Autowired
    private VehiculoService service;
}
