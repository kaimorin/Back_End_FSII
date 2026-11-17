package cl.nutrivida.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter simple por IP para los endpoints de autenticación.
 * Máximo 10 intentos por ventana de 60 segundos.
 * Para producción, considerar una solución distribuida (Redis + Bucket4j).
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS  = 10;
    private static final long WINDOW_MS    = 60_000L;

    private final ConcurrentHashMap<String, long[]> contadores = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.equals("/api/usuarios/login")
            && !path.equals("/api/usuarios/registro")
            && !path.equals("/api/usuarios/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String ip = obtenerIp(request);
        long ahora = Instant.now().toEpochMilli();

        long[] estado = contadores.compute(ip, (k, v) -> {
            if (v == null || ahora - v[1] > WINDOW_MS) {
                return new long[]{1, ahora};   // [contador, inicio_ventana]
            }
            v[0]++;
            return v;
        });

        if (estado[0] > MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                objectMapper.writeValueAsString(
                    Map.of("error",   "Demasiados intentos",
                           "mensaje", "Espera un momento antes de volver a intentarlo.")));
            return;
        }

        chain.doFilter(request, response);
    }

    private String obtenerIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
