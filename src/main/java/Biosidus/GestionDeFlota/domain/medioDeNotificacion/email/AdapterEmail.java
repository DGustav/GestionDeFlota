package Biosidus.GestionDeFlota.domain.medioDeNotificacion.email;

public interface AdapterEmail {
    public void enviar(String destinatario, String mensaje, String asunto);
}
