package com.example.proyectospring.data.specifications;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

/**
 * Utilidades estáticas para componer {@link Specification} de Spring Data JPA.
 *
 * <p>Centraliza la lógica de combinar varios predicados, evitando duplicarla en
 * cada clase de specifications del paquete. Es una clase de utilidad no
 * instanciable.
 *
 * @see Specification
 */
public final class SpecificationUtils {

    /** Constructor privado: clase de utilidad, no instanciable. */
    private SpecificationUtils() {}

    /**
     * Combina con {@code AND} todas las specifications de la lista.
     *
     * <p>Una lista vacía produce {@code null}, que en Spring Data equivale a "sin
     * restricciones" y, por tanto, devuelve todos los registros. Los elementos
     * {@code null} de la lista no se contemplan y provocarían un fallo.
     *
     * @param <T>   tipo de entidad sobre el que operan las specifications
     * @param specs predicados a combinar; puede estar vacía pero no ser {@code null}
     * @return la conjunción de todas las specifications, o {@code null} si la lista está vacía
     */
    public static <T> Specification<T> combinar(List<Specification<T>> specs) {
        Specification<T> resultado = null;
        for (Specification<T> spec : specs) {
            resultado = (resultado == null) ? spec : resultado.and(spec);
        }
        return resultado;
    }
}
