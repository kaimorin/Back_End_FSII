package cl.nutrivida.api.controller;

import cl.nutrivida.api.dto.EspecialistaDTO;
import cl.nutrivida.api.model.Especialista;
import cl.nutrivida.api.service.EspecialistaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialistas")
public class EspecialistaController {

    private final EspecialistaService service;

    public EspecialistaController(EspecialistaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Especialista> listar() { return service.listarTodos(); }

    @GetMapping("/activos")
    public List<Especialista> activos() { return service.listarActivos(); }

    @GetMapping("/{id}")
    public Especialista obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Especialista crear(@Valid @RequestBody EspecialistaDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public Especialista actualizar(@PathVariable Long id,
                                   @Valid @RequestBody EspecialistaDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
