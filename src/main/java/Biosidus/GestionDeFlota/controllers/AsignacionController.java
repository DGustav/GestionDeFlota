package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.service.implementacion.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class AsignacionController {
    //@Autowired
    private AsignacionService service;
}
