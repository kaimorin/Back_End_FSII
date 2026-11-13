package cl.nutrivida.api.controller;

import cl.nutrivida.api.dto.LoginDTO;
import cl.nutrivida.api.dto.RefreshRequestDTO;
import cl.nutrivida.api.dto.UsuarioActualizarDTO;
import cl.nutrivida.api.dto.UsuarioRegistroDTO;
import cl.nutrivida.api.model.Usuario;
import cl.nutrivida.api.service.RefreshTokenService;
import cl.nutrivida.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService      service;
    private final RefreshTokenService refreshTokenService;

    public UsuarioController(UsuarioService service,
                             RefreshTokenService refreshTokenService) {
        this.service             = service;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/listar")                     // solo ADMIN
    public List<Usuario> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")                       // autenticado
    public Usuario obtener(@PathVariable Long id) {
        Usuario u = service.obtenerPorId(id);
        u.setClave(null);
        return u;
    }

    @PostMapping("/registro")                  // público
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        Usuario u = service.registrar(dto);
        u.setClave(null);
        return u;
    }

    @PostMapping("/login")                     // público → devuelve accessToken + refreshToken
    public Map<String, Object> login(@Valid @RequestBody LoginDTO dto) {
        return service.login(dto);
    }

    /*
      Llámalo cuando el frontend reciba 401 en cualquier request protegido.
     */
    @PostMapping("/refresh")                   // público (el refresh token autentica)
    public Map<String, Object> refresh(@Valid @RequestBody RefreshRequestDTO dto) {
        return service.renovarToken(dto.getRefreshToken());
    }

    /*
  
      Revoca el refresh token del dispositivo actual.
     */
    @PostMapping("/logout")                    // público (no necesita access token vigente)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequestDTO dto) {
        refreshTokenService.revocar(dto.getRefreshToken());
    }

    @PutMapping("/{id}")                       // autenticado
    public Usuario actualizar(@PathVariable Long id,
                              @Valid @RequestBody UsuarioActualizarDTO dto) {
        Usuario u = service.actualizar(id, dto);
        u.setClave(null);
        return u;
    }

    @DeleteMapping("/{id}")                    // solo ADMIN
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
