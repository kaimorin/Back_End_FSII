package cl.nutrivida.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(unique = true)
    private String correo;

    // RUT chileno: 12.345.678-9
    @Pattern(regexp = "\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]",
             message = "RUT inválido (formato: 12.345.678-9)")
    private String rut;

    private String telefono;

    // "PACIENTE" | "ADMIN"
    @Column(nullable = false)
    private String rol = "PACIENTE";

    // Contraseña hasheada (en producción usar BCrypt)
    @NotBlank(message = "La contraseña es obligatoria")
    private String clave;

    private boolean recibirNoticias = false;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public boolean isRecibirNoticias() { return recibirNoticias; }
    public void setRecibirNoticias(boolean recibirNoticias) { this.recibirNoticias = recibirNoticias; }
}
