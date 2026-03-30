package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.service.implementacion.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class VehiculoController {
    //@Autowired
    private VehiculoService service;
}
