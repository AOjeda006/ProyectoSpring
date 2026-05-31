package com.example.proyectospring.data.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.proyectospring.domain.dto.filter.MatriculaFiltro;
import com.example.proyectospring.domain.entities.Matricula;

/**
 * Fábrica de {@link Specification} para la búsqueda dinámica de {@link Matricula}.
 *
 * <p>Traduce un {@link MatriculaFiltro} a un predicado JPA con las condiciones de
 * los campos presentes, filtrando por alumno y curso a través de sus
 * identificadores. Clase de utilidad no instanciable.
 *
 * @see MatriculaFiltro
 * @see SpecificationUtils
 */
public final class MatriculaSpecifications {

    /** Constructor privado: clase de utilidad, no instanciable. */
    private MatriculaSpecifications() {}

    /**
     * Construye la specification correspondiente al filtro indicado.
     *
     * <p>Alumno, curso y estado se comparan por igualdad; la nota, como rango
     * cerrado. Los campos {@code null} se omiten.
     *
     * @param filtro criterios de búsqueda; no debe ser {@code null}
     * @return la specification combinada, o {@code null} si el filtro no aporta condiciones (devuelve todas)
     */
    public static Specification<Matricula> conFiltro(MatriculaFiltro filtro) {
        List<Specification<Matricula>> specs = new ArrayList<>();

        if (filtro.alumnoId() != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("alumno").get("id"), filtro.alumnoId()));
        }
        if (filtro.cursoId() != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("curso").get("id"), filtro.cursoId()));
        }
        if (filtro.estado() != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("estado"), filtro.estado()));
        }
        if (filtro.notaMin() != null) {
            specs.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("nota"), filtro.notaMin()));
        }
        if (filtro.notaMax() != null) {
            specs.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("nota"), filtro.notaMax()));
        }

        return SpecificationUtils.combinar(specs);
    }
}
