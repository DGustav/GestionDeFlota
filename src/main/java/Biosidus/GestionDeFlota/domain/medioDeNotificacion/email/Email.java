package Biosidus.GestionDeFlota.domain.medioDeNotificacion.email;

import Biosidus.GestionDeFlota.domain.medioDeNotificacion.MedioDeNotificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class Email implements MedioDeNotificacion{
    @Autowired
    private AdapterEmail adapter;

    @Override
    public void notificar(String correo, String mensaje , String asunto) {
        adapter.enviar(correo, mensaje, asunto);
    }
}
