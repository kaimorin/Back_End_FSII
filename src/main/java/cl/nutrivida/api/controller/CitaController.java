package cl.nutrivida.api.controller;

import cl.nutrivida.api.dto.CitaDTO;
import cl.nutrivida.api.model.Cita;
import cl.nutrivida.api.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cita> listar() { return service.listarTodas(); }

    @GetMapping("/{id}")
    public Cita obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @GetMapping("/estado/{estado}")
    public List<Cita> porEstado(@PathVariable String estado) { return service.porEstado(estado); }

    @GetMapping("/paciente/{rut}")
    public List<Cita> porPaciente(@PathVariable String rut) { return service.porPaciente(rut); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cita crear(@Valid @RequestBody CitaDTO dto) {
        return service.crear(dto);
    }

    @PatchMapping("/{id}/estado")
    public Cita cambiarEstado(@PathVariable Long id,
                              @RequestBody Map<String, String> body) {
        return service.cambiarEstado(id, body.get("estado"));
    }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable Long id,
                           @Valid @RequestBody CitaDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
