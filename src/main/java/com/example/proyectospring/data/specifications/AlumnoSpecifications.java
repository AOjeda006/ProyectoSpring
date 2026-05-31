package com.example.proyectospring.data.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.proyectospring.domain.dto.filter.AlumnoFiltro;
import com.example.proyectospring.domain.entities.Alumno;

/**
 * Fábrica de {@link Specification} para la búsqueda dinámica de {@link Alumno}.
 *
 * <p>Traduce un {@link AlumnoFiltro} a un predicado JPA, incluyendo únicamente
 * las condiciones de los campos presentes. Clase de utilidad no instanciable.
 *
 * @see AlumnoFiltro
 * @see SpecificationUtils
 */
public final class AlumnoSpecifications {

    /** Constructor privado: clase de utilidad, no instanciable. */
    private AlumnoSpecifications() {}

    /**
     * Construye la specification correspondiente al filtro indicado.
     *
     * <p>Las cadenas se comparan con {@code LIKE} sin distinguir mayúsculas y las
     * fechas como rango cerrado. Los campos {@code null} o en blanco se omiten.
     *
     * @param filtro criterios de búsqueda; no debe ser {@code null}
     * @return la specification combinada, o {@code null} si el filtro no aporta condiciones (devuelve todos)
     */
    public static Specification<Alumno> conFiltro(AlumnoFiltro filtro) {
        List<Specification<Alumno>> specs = new ArrayList<>();

        if (filtro.nombre() != null && !filtro.nombre().isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombre")), "%" + filtro.nombre().toLowerCase() + "%"));
        }
        if (filtro.email() != null && !filtro.email().isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + filtro.email().toLowerCase() + "%"));
        }
        if (filtro.registradoDesde() != null) {
            specs.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("fechaRegistro"), filtro.registradoDesde()));
        }
        if (filtro.registradoHasta() != null) {
            specs.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("fechaRegistro"), filtro.registradoHasta()));
        }

        return SpecificationUtils.combinar(specs);
    }
}
