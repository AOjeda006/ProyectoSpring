package com.example.proyectospring.uicontrollers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.proyectospring.domain.exceptions.DuplicateResourceException;
import com.example.proyectospring.domain.exceptions.InvalidTokenException;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;

/**
 * Manejador centralizado de excepciones para todos los controladores REST.
 *
 * <p>Anotado con {@link ControllerAdvice}, traduce las excepciones que se
 * propagan desde cualquier {@code @RestController} a respuestas HTTP coherentes.
 * Salvo los errores de validación —que devuelven un mapa campo→mensaje— el resto
 * se serializa como {@link ProblemDetail} (RFC 7807). Reúne tanto las
 * excepciones de negocio propias como las de Spring Security.
 *
 * @see com.example.proyectospring.domain.exceptions.ResourceNotFoundException
 * @see com.example.proyectospring.domain.exceptions.DuplicateResourceException
 * @see com.example.proyectospring.domain.exceptions.InvalidTokenException
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Traduce los fallos de validación de bean validation a una respuesta {@code 400 Bad Request}.
     *
     * <p>Construye un mapa que asocia cada campo inválido con su mensaje de
     * validación, de modo que el cliente sepa exactamente qué corregir.
     *
     * @param ex excepción lanzada por Spring al fallar la validación de un {@code @Valid @RequestBody}
     * @return respuesta {@code 400} cuyo cuerpo es un mapa {@code campo -> mensaje}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errores.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errores);
    }

    /**
     * Traduce una {@link ResourceNotFoundException} a una respuesta {@code 404 Not Found}.
     *
     * @param ex excepción capturada; su mensaje se usa como detalle
     * @return el {@link ProblemDetail} con estado {@code 404}
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Recurso no encontrado");
        return problema;
    }

    /**
     * Traduce una {@link DuplicateResourceException} a una respuesta {@code 409 Conflict}.
     *
     * @param ex excepción capturada; su mensaje se usa como detalle
     * @return el {@link ProblemDetail} con estado {@code 409}
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problema.setTitle("Conflicto de datos");
        return problema;
    }

    /**
     * Traduce un fallo de autenticación a una respuesta {@code 401 Unauthorized}.
     *
     * <p>Devuelve un detalle genérico ("Credenciales incorrectas") para no
     * revelar si el fallo es por usuario inexistente o contraseña errónea.
     *
     * @param ex excepción de autenticación de Spring Security
     * @return el {@link ProblemDetail} con estado {@code 401}
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        problema.setTitle("No autenticado");
        return problema;
    }

    /**
     * Traduce una {@link InvalidTokenException} a una respuesta {@code 401 Unauthorized}.
     *
     * @param ex excepción capturada; su mensaje se usa como detalle
     * @return el {@link ProblemDetail} con estado {@code 401}
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(InvalidTokenException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problema.setTitle("Token inválido");
        return problema;
    }

    /**
     * Traduce una denegación de acceso a una respuesta {@code 403 Forbidden}.
     *
     * <p>Se dispara cuando un usuario autenticado carece del rol requerido por un
     * {@code @PreAuthorize}.
     *
     * @param ex excepción de acceso denegado de Spring Security
     * @return el {@link ProblemDetail} con estado {@code 403}
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "No tienes permisos suficientes para esta operación");
        problema.setTitle("Acceso denegado");
        return problema;
    }
}
