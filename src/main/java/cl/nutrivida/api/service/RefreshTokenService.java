package cl.nutrivida.api.service;

import cl.nutrivida.api.model.RefreshToken;
import cl.nutrivida.api.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // 7 días por defecto
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea un nuevo refresh token para el correo dado.
     * Elimina los anteriores del mismo usuario para evitar acumulación.
     */
    @Transactional
    public RefreshToken crear(String correo) {
        // Limpiar tokens anteriores del mismo usuario
        repository.deleteByCorreo(correo);

        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setCorreo(correo);
        rt.setExpiresAt(LocalDateTime.now()
                .plusSeconds(refreshExpirationMs / 1000));

        return repository.save(rt);
    }

    /**
     * Valida el token: existe en BD y no ha expirado.
     * Si expiró, lo elimina y lanza 401.
     */
    @Transactional
    public RefreshToken validar(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Refresh token inválido o no encontrado"));

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            repository.delete(rt);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token expirado, inicia sesión nuevamente");
        }

        return rt;
    }

    /**
     * Revoca el token indicado (logout en un dispositivo).
     */
    @Transactional
    public void revocar(String token) {
        repository.deleteByToken(token);
    }

    /**
     * Revoca todos los tokens del usuario (logout en todos los dispositivos).
     */
    @Transactional
    public void revocarTodos(String correo) {
        repository.deleteByCorreo(correo);
    }
}
