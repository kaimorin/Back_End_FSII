package cl.nutrivida.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil – pruebas unitarias")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    // La clave debe tener al menos 32 caracteres para HMAC-SHA256
    private static final String SECRET =
            "clave-secreta-para-tests-con-al-menos-32-caracteres";
    private static final long EXPIRATION_MS = 86_400_000L; // 24 h

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",       SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", EXPIRATION_MS);
    }

    // ── generarToken ──────────────────────────────────────────────────────

    @Test
    @DisplayName("generarToken devuelve un string no vacío con dos puntos (formato JWT)")
    void generarToken_devuelveStringConFormatoJwt() {
        String token = jwtUtil.generarToken("usuario@test.cl", "ADMIN");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    // ── esValido ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("esValido con token recién generado devuelve true")
    void esValido_tokenRecienGenerado_devuelveTrue() {
        String token = jwtUtil.generarToken("usuario@test.cl", "PACIENTE");

        assertThat(jwtUtil.esValido(token)).isTrue();
    }

    @Test
    @DisplayName("esValido con token mal formado devuelve false")
    void esValido_tokenMalFormado_devuelveFalse() {
        assertThat(jwtUtil.esValido("esto.no.esUnToken")).isFalse();
    }

    @Test
    @DisplayName("esValido con string vacío devuelve false")
    void esValido_stringVacio_devuelveFalse() {
        assertThat(jwtUtil.esValido("")).isFalse();
    }

    // ── obtenerCorreo ─────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerCorreo devuelve el correo incluido en el token")
    void obtenerCorreo_devuelveCorreoCorrecto() {
        String token = jwtUtil.generarToken("admin@clinica.cl", "ADMIN");

        assertThat(jwtUtil.obtenerCorreo(token)).isEqualTo("admin@clinica.cl");
    }

    // ── obtenerRol ────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerRol devuelve el rol incluido en el token")
    void obtenerRol_devuelveRolCorrecto() {
        String token = jwtUtil.generarToken("admin@clinica.cl", "ADMIN");

        assertThat(jwtUtil.obtenerRol(token)).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("obtenerRol para rol PACIENTE devuelve PACIENTE")
    void obtenerRol_conRolPaciente_devuelvePaciente() {
        String token = jwtUtil.generarToken("paciente@test.cl", "PACIENTE");

        assertThat(jwtUtil.obtenerRol(token)).isEqualTo("PACIENTE");
    }
}
