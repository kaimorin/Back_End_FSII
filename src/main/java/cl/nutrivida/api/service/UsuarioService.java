package cl.nutrivida.api.service;

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

    public Usuario registrar(Usuario usuario) {
        if (repository.existsByCorreo(usuario.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        }
        if (usuario.getRut() != null && repository.existsByRut(usuario.getRut())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una cuenta con ese RUT.");
        }
        usuario.setId(null);
        usuario.setRol("PACIENTE");
        // PRODUCCIÓN: usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return repository.save(usuario);
    }

    public Usuario login(String correo, String clave) {
        Usuario usuario = repository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));

        // PRODUCCIÓN: usar BCrypt.matches(clave, usuario.getClave())
        if (!usuario.getClave().equals(clave)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }
        return usuario;
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario usuario = obtenerPorId(id);
        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setTelefono(datos.getTelefono());
        usuario.setRecibirNoticias(datos.isRecibirNoticias());
        return repository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        repository.deleteById(id);
    }
}
