package cl.nutrivida.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Campos que el frontend envía desde Datos.jsx.
 * Excluye: id (lo genera la BD).
 */
public class PacienteDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Pattern(regexp = "\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]",
             message = "RUT inválido (formato: 12.345.678-9)")
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    private String telefono;
    private String comuna;
    private String ciudad;
    private String patologias;
    private String destinatario = "para-mi";

    // ── Getters & Setters ──────────────────────────────────────────────
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPatologias() { return patologias; }
    public void setPatologias(String patologias) { this.patologias = patologias; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
}
