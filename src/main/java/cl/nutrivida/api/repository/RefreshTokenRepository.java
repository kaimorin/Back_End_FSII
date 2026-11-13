package cl.nutrivida.api.repository;

import cl.nutrivida.api.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    /** Elimina todos los refresh tokens de un usuario (logout en todos los dispositivos) */
    void deleteByCorreo(String correo);

    /** Elimina un refresh token específico (logout en un dispositivo) */
    void deleteByToken(String token);
}
