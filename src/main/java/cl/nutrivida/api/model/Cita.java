package cl.nutrivida.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia al paciente (nombre del doctor como en la vista Agenda)
    @NotBlank(message = "El nombre del doctor es obligatorio")
    private String doctor;

    // nutricion-clinica | nutricion-deportiva | salud-digestiva | control-peso
    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    // Presencial | Online
    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    // Fecha como texto, ej: "Lunes, 10 de febrero de 2025"
    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    // Hora como texto, ej: "11:00 AM"
    @NotBlank(message = "La hora es obligatoria")
    private String hora;

    // Método de pago: webpay | mercadopago
    private String metodoPago;

    // Confirmado | Pendiente | Cancelado | Atendido
    @Column(nullable = false)
    private String estado = "Pendiente";

    // Datos del paciente (desnormalizados para confirmación rápida)
    private String pacienteNombre;
    private String pacienteApellido;
    private String pacienteRut;
    private String pacienteTelefono;

    // Precio en pesos chilenos
    private int precioBase = 35000;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public String getPacienteApellido() { return pacienteApellido; }
    public void setPacienteApellido(String pacienteApellido) { this.pacienteApellido = pacienteApellido; }

    public String getPacienteRut() { return pacienteRut; }
    public void setPacienteRut(String pacienteRut) { this.pacienteRut = pacienteRut; }

    public String getPacienteTelefono() { return pacienteTelefono; }
    public void setPacienteTelefono(String pacienteTelefono) { this.pacienteTelefono = pacienteTelefono; }

    public int getPrecioBase() { return precioBase; }
    public void setPrecioBase(int precioBase) { this.precioBase = precioBase; }
}
