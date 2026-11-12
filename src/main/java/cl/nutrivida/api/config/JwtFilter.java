package cl.nutrivida.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // ── ESTO ES LO QUE FALTABA ──
    // Le dice a Spring: a estas rutas ni siquiera entres al filtro
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // Preflight de CORS (Postman no lo usa, pero el frontend sí)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Rutas públicas que NO necesitan token
        if (path.equals("/api/usuarios/login") && method.equals("POST")) {
            return true;
        }
        if (path.equals("/api/usuarios/registro") && method.equals("POST")) {
            return true;
        }
        if (path.equals("/api/especialistas/activos") && method.equals("GET")) {
            return true;
        }
        if (path.equals("/actuator/health")) {
            return true;
        }
        if (path.equals("/error")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.esValido(token)) {
                String correo = jwtUtil.obtenerCorreo(token);
                String rol = jwtUtil.obtenerRol(token);

                // Evita ROLE_ROLE_ADMIN si el rol ya viene con prefijo
                String authority = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;

                var auth = new UsernamePasswordAuthenticationToken(
                        correo,
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}