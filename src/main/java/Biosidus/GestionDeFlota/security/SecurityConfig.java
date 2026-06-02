package Biosidus.GestionDeFlota.security;

import Biosidus.GestionDeFlota.domain.empleado.Rol;
import Biosidus.GestionDeFlota.domain.empleado.Usuario;
import Biosidus.GestionDeFlota.repositorios.UsuarioRepositorio;
import Biosidus.GestionDeFlota.services.CustomUserDetailsService;
import Biosidus.GestionDeFlota.services.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Map;

@Configuration
/* Esta anotación es de Spring Core (no es exclusiva de seguridad).
Indica que la clase es una fuente de definiciones de beans
 */
@EnableWebSecurity
// Habilita la seguridad de Spring Security
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/forbidden", "/css/**", "/js/**", "/images/**").permitAll()

                        // Vista principal de asignaciones para usuarios autenticados
                        .requestMatchers("/tableroDeControl").hasAnyRole("ADMINISTRADOR")
                        .requestMatchers("/asignacion/mis-asignaciones").hasRole("CONDUCTOR")
                        .requestMatchers("/siniestro/mis-siniestros").hasRole("CONDUCTOR")
                        .requestMatchers("/mantenimientoVehicular/mis-mantenimientosVehiculares").hasRole("CONDUCTOR")
                        .requestMatchers("/asignacion/detalle/{id}").hasAnyRole("CONDUCTOR", "OPERARIO","ADMINISTRADOR")
                        .requestMatchers("/vehiculo/**").hasAnyRole("OPERARIO","ADMINISTRADOR")
                        .requestMatchers("/vehiculo/detalle/{id}").hasRole("CONDUCTOR")
                        .requestMatchers("/mantenimientoVehicular/detalle/{id}").hasAnyRole("CONDUCTOR", "OPERARIO","ADMINISTRADOR")
                        // El detalle lo pueden ver los 3 roles
                        .requestMatchers("/siniestro/detalle/{id}").hasAnyRole("CONDUCTOR", "OPERARIO", "ADMINISTRADOR")

                        .requestMatchers("/conductor/**").hasAnyRole("OPERARIO","ADMINISTRADOR")


                        .requestMatchers("/documento/**").hasAnyRole("ADMINISTRADOR")


                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/asignacion/listado", true)
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/forbidden")
                );

        return http.build();
    }


    @Bean
    public CommandLineRunner initAdmin(PasswordEncoder passwordEncoder, UsuarioService usuarioService) {
        return args -> {
            if (usuarioService.buscarPorNickname("admin").isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setNickname("admin");
                usuario.setPassword(passwordEncoder.encode("admin123"));
                usuario.setRol(Rol.ADMINISTRADOR);

                usuarioService.guardar(usuario);
            }
            if (usuarioService.buscarPorNickname("operario").isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setNickname("operario");
                usuario.setPassword(passwordEncoder.encode("admin123"));
                usuario.setRol(Rol.OPERARIO);

                usuarioService.guardar(usuario);
            }
            if (usuarioService.buscarPorNickname("conductor").isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setNickname("conductor");
                usuario.setPassword(passwordEncoder.encode("admin123"));
                usuario.setRol(Rol.CONDUCTOR);

                usuarioService.guardar(usuario);
            }
        };
    }
}


