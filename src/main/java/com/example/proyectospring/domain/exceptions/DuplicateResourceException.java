package com.example.proyectospring.domain.exceptions;

/**
 * Excepción de negocio que indica un conflicto por violación de unicidad.
 *
 * <p>La lanza la capa de servicio al intentar crear un recurso que ya existe:
 * por ejemplo, un email de alumno o profesor ya registrado, un nombre de usuario
 * en uso o una matrícula duplicada del mismo alumno en el mismo curso.
 * {@link com.example.proyectospring.uicontrollers.GlobalExceptionHandler} la
 * traduce a una respuesta {@code 409 Conflict}. Es una excepción no comprobada.
 *
 * @see com.example.proyectospring.uicontrollers.GlobalExceptionHandler#handleDuplicate(DuplicateResourceException)
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje que describe el conflicto.
     *
     * @param mensaje detalle del recurso duplicado, que se expone al cliente
     */
    public DuplicateResourceException(String mensaje) {
        super(mensaje);
    }
}
