package cl.nutrivida.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body para POST /api/usuarios/refresh y POST /api/usuarios/logout.
 */
public class RefreshRequestDTO {

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
