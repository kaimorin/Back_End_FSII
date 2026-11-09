package cl.nutrivida.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Campos que el frontend envía al crear una cita (Pago.jsx).
 * Excluye: id, estado (siempre empieza en "Pendiente"), precioBase (lo fija el backend).
 */
public class CitaDTO {

    @NotBlank(message = "El doctor es obligatorio")
    private String doctor;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "La hora es obligatoria")
    private String hora;

    private String metodoPago;

    // Datos del paciente (desnormalizados para confirmación rápida)
    private String pacienteNombre;
    private String pacienteApellido;
    private String pacienteRut;
    private String pacienteTelefono;

    // ── Getters & Setters ──────────────────────────────────────────────
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

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
