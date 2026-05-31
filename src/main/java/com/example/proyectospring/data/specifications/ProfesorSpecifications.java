package com.example.proyectospring.data.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.proyectospring.domain.dto.filter.ProfesorFiltro;
import com.example.proyectospring.domain.entities.Profesor;

/**
 * Fábrica de {@link Specification} para la búsqueda dinámica de {@link Profesor}.
 *
 * <p>Traduce un {@link ProfesorFiltro} a un predicado JPA con las condiciones de
 * los campos presentes. Clase de utilidad no instanciable.
 *
 * @see ProfesorFiltro
 * @see SpecificationUtils
 */
public final class ProfesorSpecifications {

    /** Constructor privado: clase de utilidad, no instanciable. */
    private ProfesorSpecifications() {}

    /**
     * Construye la specification correspondiente al filtro indicado.
     *
     * <p>Nombre y especialidad se comparan con {@code LIKE} sin distinguir
     * mayúsculas. Los campos {@code null} o en blanco se omiten.
     *
     * @param filtro criterios de búsqueda; no debe ser {@code null}
     * @return la specification combinada, o {@code null} si el filtro no aporta condiciones (devuelve todos)
     */
    public static Specification<Profesor> conFiltro(ProfesorFiltro filtro) {
        List<Specification<Profesor>> specs = new ArrayList<>();

        if (filtro.nombre() != null && !filtro.nombre().isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombre")), "%" + filtro.nombre().toLowerCase() + "%"));
        }
        if (filtro.especialidad() != null && !filtro.especialidad().isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("especialidad")), "%" + filtro.especialidad().toLowerCase() + "%"));
        }

        return SpecificationUtils.combinar(specs);
    }
}
