package cl.nutrivida.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa un refresh token almacenado en BD.
 * Permite renovar el access token sin volver a pedir credenciales
 * y también permite revocar sesiones (logout).
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UUID aleatorio — único e irrepetible */
    @Column(nullable = false, unique = true, length = 36)
    private String token;

    /** Correo del usuario dueño del token */
    @Column(nullable = false)
    private String correo;

    /** Fecha/hora de expiración */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
