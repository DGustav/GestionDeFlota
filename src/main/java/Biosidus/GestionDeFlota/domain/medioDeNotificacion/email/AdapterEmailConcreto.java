package Biosidus.GestionDeFlota.domain.medioDeNotificacion.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class AdapterEmailConcreto implements AdapterEmail{
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void enviar(String correo, String mensaje , String asunto) {
        try {
            // Instanciamos un correo de texto plano básico
            SimpleMailMessage email = new SimpleMailMessage();

            // Configuramos los datos principales
            email.setTo(correo);
            email.setSubject(asunto);
            email.setText(mensaje); // Aquí insertamos directamente el String que llega por parámetro

            // Enviamos el correo
            mailSender.send(email);
            System.out.println("Correo enviado exitosamente a: " + correo);

        } catch (Exception e) {
            System.err.println("Error general al enviar el correo a " + correo + ": " + e.getMessage());
        }
    }
}
