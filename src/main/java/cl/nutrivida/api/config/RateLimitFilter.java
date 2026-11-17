package cl.nutrivida.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Simple: 100 requests por minuto por IP. Cambia el número si quieres.
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private final Map<String, RequestCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        return path.startsWith("/swagger-ui")
            || path.equals("/swagger-ui.html")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/swagger-resources")
            || path.startsWith("/webjars")
            || path.equals("/actuator/health")
            || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String ip = request.getRemoteAddr();
            if (ip == null) ip = "unknown";

            RequestCounter counter = counters.computeIfAbsent(ip, k -> new RequestCounter());
            if (!counter.allow()) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Demasiadas peticiones\",\"mensaje\":\"Intenta de nuevo en un minuto\"}");
                return;
            }
        } catch (Exception e) {
            // Un filtro de rate limit NUNCA debe tumbar la API con 500
            // Si algo falla, deja pasar la request
            e.printStackTrace();
        }

        chain.doFilter(request, response);
    }

    private static class RequestCounter {
        private long windowStart = System.currentTimeMillis();
        private int count = 0;

        synchronized boolean allow() {
            long now = System.currentTimeMillis();
            if (now - windowStart > 60_000) {
                windowStart = now;
                count = 0;
            }
            count++;
            return count <= MAX_REQUESTS_PER_MINUTE;
        }
    }
}
