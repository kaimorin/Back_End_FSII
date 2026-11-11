package cl.nutrivida.api.service;

import cl.nutrivida.api.config.JwtUtil;
import cl.nutrivida.api.dto.LoginDTO;
import cl.nutrivida.api.dto.UsuarioActualizarDTO;
import cl.nutrivida.api.dto.UsuarioRegistroDTO;
import cl.nutrivida.api.model.Usuario;
import cl.nutrivida.api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repository, JwtUtil jwtUtil) {
        this.repository = repository;
        this.jwtUtil    = jwtUtil;
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
        if (repository.existsByCorreo(dto.getCorreo()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        if (dto.getRut() != null && repository.existsByRut(dto.getRut()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese RUT.");

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRut(dto.getRut());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRecibirNoticias(dto.isRecibirNoticias());
        usuario.setRol("PACIENTE");
        usuario.setClave(passwordEncoder.encode(dto.getClave()));
        return repository.save(usuario);
    }

    public Map<String, Object> login(LoginDTO dto) {
        Usuario usuario = repository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(dto.getClave(), usuario.getClave()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());

        return Map.of(
                "token",    token,
                "id",       usuario.getId(),
                "nombre",   usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "correo",   usuario.getCorreo(),
                "rol",      usuario.getRol()
        );
    }

    public Usuario actualizar(Long id, UsuarioActualizarDTO dto) {
        Usuario usuario = obtenerPorId(id);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRecibirNoticias(dto.isRecibirNoticias());
        return repository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        repository.deleteById(id);
    }
}