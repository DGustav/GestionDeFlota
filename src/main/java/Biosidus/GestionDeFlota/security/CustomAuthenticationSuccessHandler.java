package Biosidus.GestionDeFlota.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Map<String, String> roleRedirectMap = Map.of(
            "ROLE_ADMINISTRADOR", "/tableroDeControl",
            "ROLE_OPERARIO", "/asignacion/listado",
            "ROLE_CONDUCTOR", "/asignacion/mis-asignaciones"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(roleRedirectMap::containsKey)
                .map(roleRedirectMap::get)
                .findFirst()
                .orElse("/login");

        response.sendRedirect(redirectUrl);
    }
}
