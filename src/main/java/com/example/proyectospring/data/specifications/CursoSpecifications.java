package com.example.proyectospring.data.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.proyectospring.domain.dto.filter.CursoFiltro;
import com.example.proyectospring.domain.entities.Curso;

/**
 * Fábrica de {@link Specification} para la búsqueda dinámica de {@link Curso}.
 *
 * <p>Traduce un {@link CursoFiltro} a un predicado JPA con las condiciones de los
 * campos presentes, incluyendo el filtrado por el identificador del profesor
 * asociado mediante navegación de la relación. Clase de utilidad no instanciable.
 *
 * @see CursoFiltro
 * @see SpecificationUtils
 */
public final class CursoSpecifications {

    /** Constructor privado: clase de utilidad, no instanciable. */
    private CursoSpecifications() {}

    /**
     * Construye la specification correspondiente al filtro indicado.
     *
     * <p>El nombre se compara con {@code LIKE} sin distinguir mayúsculas; los
     * créditos como rango cerrado; el profesor por igualdad de su identificador.
     * Los campos {@code null} o en blanco se omiten.
     *
     * @param filtro criterios de búsqueda; no debe ser {@code null}
     * @return la specification combinada, o {@code null} si el filtro no aporta condiciones (devuelve todos)
     */
    public static Specification<Curso> conFiltro(CursoFiltro filtro) {
        List<Specification<Curso>> specs = new ArrayList<>();

        if (filtro.nombre() != null && !filtro.nombre().isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombre")), "%" + filtro.nombre().toLowerCase() + "%"));
        }
        if (filtro.profesorId() != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("profesor").get("id"), filtro.profesorId()));
        }
        if (filtro.creditosMin() != null) {
            specs.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("creditos"), filtro.creditosMin()));
        }
        if (filtro.creditosMax() != null) {
            specs.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("creditos"), filtro.creditosMax()));
        }

        return SpecificationUtils.combinar(specs);
    }
}
