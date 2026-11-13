package cl.nutrivida.api.service;

import cl.nutrivida.api.config.JwtUtil;
import cl.nutrivida.api.dto.LoginDTO;
import cl.nutrivida.api.dto.UsuarioActualizarDTO;
import cl.nutrivida.api.dto.UsuarioRegistroDTO;
import cl.nutrivida.api.model.RefreshToken;
import cl.nutrivida.api.model.Usuario;
import cl.nutrivida.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService – pruebas unitarias")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setNombre("Juan");
        usuarioEjemplo.setApellido("Pérez");
        usuarioEjemplo.setCorreo("juan@test.cl");
        usuarioEjemplo.setRut("12.345.678-9");
        usuarioEjemplo.setClave("$2a$10$hashBcryptFalso");
        usuarioEjemplo.setRol("PACIENTE");
    }

    // ── registrar ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("registrar con datos nuevos guarda el usuario con rol PACIENTE")
    void registrar_conDatosNuevos_devuelveUsuario() {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setCorreo("juan@test.cl");
        dto.setRut("12.345.678-9");
        dto.setClave("Pass1234!");

        when(usuarioRepository.existsByCorreo("juan@test.cl")).thenReturn(false);
        when(usuarioRepository.existsByRut("12.345.678-9")).thenReturn(false);
        when(passwordEncoder.encode("Pass1234!")).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.registrar(dto);

        assertThat(resultado.getRol()).isEqualTo("PACIENTE");
        assertThat(resultado.getClave()).isEqualTo("$2a$10$hash");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrar con correo duplicado lanza 409")
    void registrar_conCorreoDuplicado_lanza409() {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setCorreo("juan@test.cl");
        dto.setRut("12.345.678-9");
        dto.setClave("Pass1234!");

        when(usuarioRepository.existsByCorreo("juan@test.cl")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ya existe una cuenta con ese correo");
    }

    @Test
    @DisplayName("registrar con RUT duplicado lanza 409")
    void registrar_conRutDuplicado_lanza409() {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setCorreo("nuevo@test.cl");
        dto.setRut("12.345.678-9");
        dto.setClave("Pass1234!");

        when(usuarioRepository.existsByCorreo("nuevo@test.cl")).thenReturn(false);
        when(usuarioRepository.existsByRut("12.345.678-9")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ya existe una cuenta con ese RUT");
    }

    // ── login ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login con credenciales válidas devuelve mapa con token y datos del usuario")
    void login_conCredencialesValidas_devuelveMapaConToken() {
        LoginDTO dto = new LoginDTO();
        dto.setCorreo("juan@test.cl");
        dto.setClave("Pass1234!");

        RefreshToken refreshTokenMock = new RefreshToken();
        refreshTokenMock.setToken("refresh.token.mock");

        when(usuarioRepository.findByCorreo("juan@test.cl")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches("Pass1234!", "$2a$10$hashBcryptFalso")).thenReturn(true);
        when(jwtUtil.generarToken("juan@test.cl", "PACIENTE")).thenReturn("jwt.token.mock");
        when(refreshTokenService.crear("juan@test.cl")).thenReturn(refreshTokenMock);

        Map<String, Object> resultado = usuarioService.login(dto);

        assertThat(resultado).containsKey("token");
        assertThat(resultado.get("token")).isEqualTo("jwt.token.mock");
        assertThat(resultado.get("rol")).isEqualTo("PACIENTE");
        assertThat(resultado).containsKey("refreshToken");
    }

    @Test
    @DisplayName("login con correo inexistente lanza 401")
    void login_conCorreoInexistente_lanza401() {
        LoginDTO dto = new LoginDTO();
        dto.setCorreo("noexiste@test.cl");
        dto.setClave("Pass1234!");

        when(usuarioRepository.findByCorreo("noexiste@test.cl")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.login(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Correo o contraseña incorrectos");
    }

    @Test
    @DisplayName("login con clave incorrecta lanza 401")
    void login_conClaveIncorrecta_lanza401() {
        LoginDTO dto = new LoginDTO();
        dto.setCorreo("juan@test.cl");
        dto.setClave("ClaveWrong!");

        when(usuarioRepository.findByCorreo("juan@test.cl")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches(eq("ClaveWrong!"), anyString())).thenReturn(false);  // ← fix

        assertThatThrownBy(() -> usuarioService.login(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Correo o contraseña incorrectos");
    }

    // ── actualizar ────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar con usuario existente actualiza nombre y apellido")
    void actualizar_usuarioExistente_actualizaDatos() {
        UsuarioActualizarDTO dto = new UsuarioActualizarDTO();
        dto.setNombre("Juan Carlos");
        dto.setApellido("Pérez Soto");
        dto.setTelefono("+56912345678");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.actualizar(1L, dto);

        assertThat(resultado.getNombre()).isEqualTo("Juan Carlos");
        assertThat(resultado.getApellido()).isEqualTo("Pérez Soto");
    }

    // ── eliminar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar con ID existente llama deleteById")
    void eliminar_usuarioExistente_llamaDeleteById() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.eliminar(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar con ID inexistente lanza 404")
    void eliminar_usuarioInexistente_lanza404() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    // ── listarTodos ───────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodos delega en repositorio")
    void listarTodos_devuelveListaDelRepo() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioEjemplo));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertThat(resultado).containsExactly(usuarioEjemplo);
    }
}