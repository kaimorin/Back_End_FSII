package cl.nutrivida.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Rutas públicas (sin token) ─────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registro").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/especialistas/activos").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll() 

                // ── Solo ADMIN ────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/usuarios/listar").hasRole("ADMIN")  // ← ruta corregida
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/pacientes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/especialistas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/especialistas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/especialistas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/citas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/citas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/citas/**").hasRole("ADMIN")

                // ── ADMIN y PACIENTE (autenticados) ───────────────────
                .requestMatchers(HttpMethod.POST, "/api/pacientes").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/citas").authenticated()
                .requestMatchers(HttpMethod.GET,  "/api/citas/paciente/**").authenticated()
                .requestMatchers(HttpMethod.GET,  "/api/citas/**").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/api/usuarios/**").authenticated()

                // cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        return new UrlBasedCorsConfigurationSource() {{
            registerCorsConfiguration("/api/**", config);
        }};
    }
}
