package cl.nutrivida.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // Beans de seguridad 

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Filter chain ──────────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Respuesta JSON para 401 (token ausente / inválido)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(
                        objectMapper.writeValueAsString(
                            Map.of("error",   "No autenticado",
                                   "mensaje", "Se requiere un token JWT válido")));
                })
                // Respuesta JSON para 403 (token válido pero sin permisos)
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(
                        objectMapper.writeValueAsString(
                            Map.of("error",   "Acceso denegado",
                                   "mensaje", "No tienes permisos para esta operación")));
                })
            )

            .authorizeHttpRequests(auth -> auth

                // ── Rutas públicas (sin token) ─────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registro").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/refresh").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/logout").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/especialistas/activos").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // ── Solo ADMIN ────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,    "/api/usuarios/listar").hasRole("ADMIN")
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
