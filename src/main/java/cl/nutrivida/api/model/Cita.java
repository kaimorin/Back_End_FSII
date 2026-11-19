package cl.nutrivida.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del especialista seleccionado. Se guarda además el nombre como fotografía histórica.
    private Long especialistaId;

    @NotBlank(message = "El nombre del doctor es obligatorio")
    private String doctor;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    // Fecha legible para mostrarla en la aplicación.
    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    // Fecha normalizada yyyy-MM-dd para validar y consultar reservas.
    private String fechaISO;

    @NotBlank(message = "La hora es obligatoria")
    private String hora;

    private String metodoPago;

    @Column(nullable = false)
    private String estado = "Pendiente";

    private String pacienteNombre;
    private String pacienteApellido;
    private String pacienteRut;
    private String pacienteTelefono;

    // Estos montos quedan guardados en la cita y no cambian si el especialista modifica su precio.
    @Column(nullable = false, columnDefinition = "INT NOT NULL DEFAULT 35000")
    private int precioBase = 35000;

    @Column(nullable = false, columnDefinition = "INT NOT NULL DEFAULT 6650")
    private int iva = 6650;

    @Column(nullable = false, columnDefinition = "INT NOT NULL DEFAULT 41650")
    private int total = 41650;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEspecialistaId() { return especialistaId; }
    public void setEspecialistaId(Long especialistaId) { this.especialistaId = especialistaId; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

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

    public int getIva() { return iva; }
    public void setIva(int iva) { this.iva = iva; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
