package com.example.proyectospring.domain.exceptions;

/**
 * Excepción que indica que un refresh token es inválido, ha expirado o no es del tipo esperado.
 *
 * <p>La lanza {@link com.example.proyectospring.service.AuthService} durante la
 * renovación de tokens cuando el token recibido no es un refresh token válido o
 * su usuario ya no existe.
 * {@link com.example.proyectospring.uicontrollers.GlobalExceptionHandler} la
 * traduce a una respuesta {@code 401 Unauthorized}. Es una excepción no comprobada.
 *
 * @see com.example.proyectospring.service.AuthService#refresh(String)
 * @see com.example.proyectospring.uicontrollers.GlobalExceptionHandler#handleInvalidToken(InvalidTokenException)
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje que describe el problema del token.
     *
     * @param mensaje detalle del motivo por el que el token es inválido
     */
    public InvalidTokenException(String mensaje) {
        super(mensaje);
    }
}
