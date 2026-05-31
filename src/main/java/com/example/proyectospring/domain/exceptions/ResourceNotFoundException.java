package com.example.proyectospring.domain.exceptions;

/**
 * Excepción de negocio que indica que un recurso solicitado no existe.
 *
 * <p>La lanza la capa de servicio cuando una operación referencia una entidad
 * inexistente.
 * {@link com.example.proyectospring.uicontrollers.GlobalExceptionHandler} la
 * traduce a una respuesta {@code 404 Not Found} en formato {@code ProblemDetail}.
 * Es una excepción no comprobada, por lo que no forma parte de la firma de los
 * métodos que la lanzan.
 *
 * @see com.example.proyectospring.uicontrollers.GlobalExceptionHandler#handleNotFound(ResourceNotFoundException)
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje libre.
     *
     * @param mensaje descripción del recurso no encontrado, que se expone al cliente
     */
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepción componiendo un mensaje estándar a partir del tipo de recurso y su id.
     *
     * <p>El mensaje resultante tiene la forma {@code "<recurso> con id <id> no encontrado"}.
     *
     * @param recurso nombre del tipo de recurso (p. ej. {@code "Alumno"})
     * @param id      identificador buscado que no se encontró
     */
    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " con id " + id + " no encontrado");
    }
}
