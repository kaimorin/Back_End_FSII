package cl.nutrivida.api.service;

import cl.nutrivida.api.config.JwtUtil;
import cl.nutrivida.api.dto.LoginDTO;
import cl.nutrivida.api.dto.UsuarioActualizarDTO;
import cl.nutrivida.api.dto.UsuarioRegistroDTO;
import cl.nutrivida.api.model.RefreshToken;
import cl.nutrivida.api.model.Usuario;
import cl.nutrivida.api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository    repository;
    private final JwtUtil              jwtUtil;
    private final PasswordEncoder      passwordEncoder;
    private final RefreshTokenService  refreshTokenService;

    public UsuarioService(UsuarioRepository repository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          RefreshTokenService refreshTokenService) {
        this.repository          = repository;
        this.jwtUtil             = jwtUtil;
        this.passwordEncoder     = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    // ── Spring Security ───────────────────────────────────────────────
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = repository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + correo));

        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getClave(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }

    // ── Lógica de negocio ─────────────────────────────────────────────

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario registrar(UsuarioRegistroDTO dto) {
        if (repository.existsByCorreo(dto.getCorreo().toLowerCase().trim()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        if (dto.getRut() != null && repository.existsByRut(dto.getRut()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese RUT.");

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setCorreo(dto.getCorreo().toLowerCase().trim());
        usuario.setRut(dto.getRut());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRecibirNoticias(dto.isRecibirNoticias());
        usuario.setRol("PACIENTE");
        usuario.setClave(passwordEncoder.encode(dto.getClave()));
        return repository.save(usuario);
    }

    /**
     * Autentica al usuario y devuelve accessToken (15 min) + refreshToken.
     * Usa el mismo mensaje de error para correo y contraseña incorrectos
     * para evitar enumeración de usuarios.
     */
    public Map<String, Object> login(LoginDTO dto) {
        // Mensaje genérico para evitar enumeración de usuarios
        final String MSG_INVALIDO = "Correo o contraseña incorrectos.";

        Usuario usuario = repository.findByCorreo(dto.getCorreo().toLowerCase().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, MSG_INVALIDO));

        if (!passwordEncoder.matches(dto.getClave(), usuario.getClave()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MSG_INVALIDO);

        String accessToken = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
        RefreshToken refreshToken = refreshTokenService.crear(usuario.getCorreo());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("accessToken",  accessToken);
        respuesta.put("refreshToken", refreshToken.getToken());
        respuesta.put("id",           usuario.getId());
        respuesta.put("nombre",       usuario.getNombre());
        respuesta.put("apellido",     usuario.getApellido());
        respuesta.put("correo",       usuario.getCorreo());
        respuesta.put("rol",          usuario.getRol());
        return respuesta;
    }

    /**
     * Usa el refresh token para emitir un nuevo access token.
     */
    public Map<String, Object> renovarToken(String refreshTokenStr) {
        RefreshToken rt = refreshTokenService.validar(refreshTokenStr);

        Usuario usuario = repository.findByCorreo(rt.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        String nuevoAccessToken = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());

        return Map.of("accessToken", nuevoAccessToken);
    }

    /**
     * Solo el propio usuario (o un ADMIN) puede actualizar su perfil.
     */
    public Usuario actualizar(Long id, UsuarioActualizarDTO dto) {
        verificarPropietario(id);
        Usuario usuario = obtenerPorId(id);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRecibirNoticias(dto.isRecibirNoticias());
        return repository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        repository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────

    /**
     * Lanza 403 si el usuario autenticado no es el dueño del recurso ni ADMIN.
     */
    private void verificarPropietario(Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return; // el filtro ya rechazó la petición antes

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (esAdmin) return;

        Usuario objetivo = obtenerPorId(id);
        if (!objetivo.getCorreo().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para modificar este usuario.");
        }
    }
}
