package Biosidus.GestionDeFlota.domain.medioDeNotificacion;

import org.springframework.mail.SimpleMailMessage;

public interface MedioDeNotificacion {
    public void notificar(String destinatario, String mensaje, String asunto);
}
