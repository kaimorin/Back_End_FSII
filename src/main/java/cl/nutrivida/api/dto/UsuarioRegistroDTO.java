package cl.nutrivida.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Campos que el frontend puede enviar al registrarse.
 * Excluye: id, rol (siempre "PACIENTE"), nunca lo asigna el cliente.
 */
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Pattern(regexp = "\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]",
             message = "RUT inválido (formato: 12.345.678-9)")
    private String rut;

    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    private String clave;

    private boolean recibirNoticias = false;

    // ── Getters & Setters ──────────────────────────────────────────────
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public boolean isRecibirNoticias() { return recibirNoticias; }
    public void setRecibirNoticias(boolean recibirNoticias) { this.recibirNoticias = recibirNoticias; }
}
