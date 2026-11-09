package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.LoginDTO;
import cl.nutrivida.api.dto.UsuarioActualizarDTO;
import cl.nutrivida.api.dto.UsuarioRegistroDTO;
import cl.nutrivida.api.model.Usuario;
import cl.nutrivida.api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario registrar(UsuarioRegistroDTO dto) {
        if (repository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        }
        if (dto.getRut() != null && repository.existsByRut(dto.getRut())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una cuenta con ese RUT.");
        }
        // Mapeo DTO → Entidad (el rol lo fija el backend, nunca el cliente)
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRut(dto.getRut());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRecibirNoticias(dto.isRecibirNoticias());
        usuario.setRol("PACIENTE");
        // PRODUCCIÓN: usuario.setClave(passwordEncoder.encode(dto.getClave()));
        usuario.setClave(dto.getClave());
        return repository.save(usuario);
    }

    public Usuario login(LoginDTO dto) {
        Usuario usuario = repository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
        // PRODUCCIÓN: BCrypt.matches(dto.getClave(), usuario.getClave())
        if (!usuario.getClave().equals(dto.getClave())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }
        return usuario;
    }

    public Usuario actualizar(Long id, UsuarioActualizarDTO dto) {
        Usuario usuario = obtenerPorId(id);
        // Solo los campos permitidos — rol y correo NO se tocan
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRecibirNoticias(dto.isRecibirNoticias());
        return repository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        repository.deleteById(id);
    }
}
