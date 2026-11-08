package cl.nutrivida.api.controller;

import cl.nutrivida.api.model.Usuario;
import cl.nutrivida.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usuario> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario registrar(@Valid @RequestBody Usuario usuario) {
        Usuario guardado = service.registrar(usuario);
        guardado.setClave(null);
        return guardado;
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody Map<String, String> credenciales) {
        Usuario usuario = service.login(
                credenciales.get("correo"),
                credenciales.get("clave")
        );
        usuario.setClave(null);
        return usuario;
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @Valid @RequestBody Usuario datos) {
        Usuario usuario = service.actualizar(id, datos);
        usuario.setClave(null);
        return usuario;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
