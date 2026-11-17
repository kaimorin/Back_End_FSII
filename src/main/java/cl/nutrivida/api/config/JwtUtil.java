package cl.nutrivida.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // HMAC-SHA256 requiere mínimo 32 bytes; falla rápido si el secret es corto
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                "JWT secret demasiado corto: se requieren al menos 32 caracteres.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    public Claims parsearToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String obtenerCorreo(String token) {
        return parsearToken(token).getSubject();
    }

    public String obtenerRol(String token) {
        return parsearToken(token).get("rol", String.class);
    }

    /**
     * Devuelve true solo si el token es criptográficamente válido y no está expirado.
     * No registra el detalle del error para evitar info leakage.
     */
    public boolean esValido(String token) {
        try {
            parsearToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
