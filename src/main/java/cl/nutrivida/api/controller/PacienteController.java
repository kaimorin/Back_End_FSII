package cl.nutrivida.api.controller;

import cl.nutrivida.api.dto.PacienteDTO;
import cl.nutrivida.api.model.Paciente;
import cl.nutrivida.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Paciente> listar() { return service.listarTodos(); }

    @GetMapping("/{id}")
    public Paciente obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @GetMapping("/rut/{rut}")
    public Paciente porRut(@PathVariable String rut) { return service.obtenerPorRut(rut); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente crear(@Valid @RequestBody PacienteDTO dto) {
        return service.guardar(dto);
    }

    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id,
                               @Valid @RequestBody PacienteDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
