package cl.nutrivida.api.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja excepciones globalmente y devuelve respuestas JSON uniformes.
 *
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} → 400 con detalle de cada campo inválido</li>
 *   <li>{@link ResponseStatusException}         → reusa el status y mensaje del lanzador</li>
 *   <li>{@link Exception}                       → 500 genérico (evita stacktraces en producción)</li>
 * </ul>
 */
// FIX 1: Solo atrapa TUS controladores, NO los de springdoc / actuator.
// FIX 2: @Hidden hace que springdoc NI SIQUIERA intente leer este handler.
// Sin esto, springdoc entra a getGenericMapResponse() y revienta con
// NoSuchMethodError: ControllerAdviceBean.<init>(Object)
@Hidden
@RestControllerAdvice(basePackages = "cl.nutrivida.api.controller")
public class GlobalExceptionHandler {

    // 400  Errores de validación Jakarta @NotBlank, @Email, @Rut

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> campos = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            campos.put(fe.getField(), fe.getDefaultMessage());
        }

        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status",    HttpStatus.BAD_REQUEST.value());
        cuerpo.put("error",     "Validación fallida");
        cuerpo.put("campos",    campos);

        return ResponseEntity.badRequest().body(cuerpo);
    }

    // Excepciones de negocio

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex) {

        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status",    ex.getStatusCode().value());
        cuerpo.put("error",     ex.getReason());

        return ResponseEntity.status(ex.getStatusCode()).body(cuerpo);
    }

    // error genericos

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        ex.printStackTrace();

        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status",    HttpStatus.INTERNAL_SERVER_ERROR.value());
        cuerpo.put("error",     "Error interno del servidor");
        // Temporal para debug: te muestra la causa en el JSON también
        // Cuando ya funcione, borrá estas 2 líneas
        cuerpo.put("detalle", String.valueOf(ex.getMessage()));
        cuerpo.put("clase", ex.getClass().getName());

        return ResponseEntity.internalServerError().body(cuerpo);
    }
}