package cl.nutrivida.api.controller;

import cl.nutrivida.api.dto.LoginDTO;
import cl.nutrivida.api.dto.UsuarioActualizarDTO;
import cl.nutrivida.api.dto.UsuarioRegistroDTO;
import cl.nutrivida.api.model.Usuario;
import cl.nutrivida.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/listar")                     // solo ADMIN
    public List<Usuario> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")             // autenticado
    public Usuario obtener(@PathVariable Long id) {
        Usuario u = service.obtenerPorId(id);
        u.setClave(null);
        return u;
    }

    @PostMapping("/registro")        // público
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        Usuario u = service.registrar(dto);
        u.setClave(null);
        return u;
    }

    @PostMapping("/login")           // público → devuelve token + datos
    public Map<String, Object> login(@Valid @RequestBody LoginDTO dto) {
        return service.login(dto);
    }

    @PutMapping("/{id}")             // autenticado
    public Usuario actualizar(@PathVariable Long id,
                              @Valid @RequestBody UsuarioActualizarDTO dto) {
        Usuario u = service.actualizar(id, dto);
        u.setClave(null);
        return u;
    }

    @DeleteMapping("/{id}")          // solo ADMIN
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
