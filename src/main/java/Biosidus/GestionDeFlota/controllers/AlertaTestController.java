package Biosidus.GestionDeFlota.controllers;

import Biosidus.GestionDeFlota.domain.medioDeNotificacion.MedioDeNotificacion;
import Biosidus.GestionDeFlota.domain.medioDeNotificacion.email.AdapterEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alertas")
public class AlertaTestController {

    @Autowired
    private MedioDeNotificacion adapterEmail; // Inyectamos nuestra clase de envío de correos

    @GetMapping("/prueba")
    public String probarEnvioCorreo() {
        // Reemplaza por un correo real tuyo donde quieras recibir la prueba
        String correoDestino = "g.porcel@biosidus.com.ar";
        String mensaje = "¡Hola! Esta es una prueba de conexión exitosa desde tu Sistema de Gestión de Flota.";

        adapterEmail.notificar(correoDestino, mensaje, "Prueba");

        return "Orden de enviar correo ejecutada. Revisa la consola y tu bandeja de entrada.";
    }
}