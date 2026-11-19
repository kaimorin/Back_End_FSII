package cl.nutrivida.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Datos que el frontend envía al crear una reserva. Los precios y el nombre del especialista
 * se obtienen en el backend a partir de especialistaId; no se confía en importes del navegador. */
public class CitaDTO {

    @NotNull(message = "Debes seleccionar un especialista")
    private Long especialistaId;

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "La fecha de reserva es obligatoria")
    private String fechaISO;

    @NotBlank(message = "La hora es obligatoria")
    private String hora;

    private String metodoPago;
    private String pacienteNombre;
    private String pacienteApellido;
    private String pacienteRut;
    private String pacienteTelefono;

    public Long getEspecialistaId() { return especialistaId; }
    public void setEspecialistaId(Long especialistaId) { this.especialistaId = especialistaId; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getFechaISO() { return fechaISO; }
    public void setFechaISO(String fechaISO) { this.fechaISO = fechaISO; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public String getPacienteApellido() { return pacienteApellido; }
    public void setPacienteApellido(String pacienteApellido) { this.pacienteApellido = pacienteApellido; }

    public String getPacienteRut() { return pacienteRut; }
    public void setPacienteRut(String pacienteRut) { this.pacienteRut = pacienteRut; }

    public String getPacienteTelefono() { return pacienteTelefono; }
    public void setPacienteTelefono(String pacienteTelefono) { this.pacienteTelefono = pacienteTelefono; }
}
