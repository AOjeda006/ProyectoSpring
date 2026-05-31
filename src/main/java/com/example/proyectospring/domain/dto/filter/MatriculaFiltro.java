package com.example.proyectospring.domain.dto.filter;

import com.example.proyectospring.domain.entities.EstadoMatricula;

/**
 * Criterios opcionales para la búsqueda dinámica y paginada de matrículas.
 *
 * <p>Los campos {@code null} se ignoran al construir la consulta. La combinación
 * se traduce a una {@link org.springframework.data.jpa.domain.Specification} en
 * {@link com.example.proyectospring.data.specifications.MatriculaSpecifications}.
 *
 * @param alumnoId identificador del alumno matriculado; {@code null} lo ignora
 * @param cursoId  identificador del curso; {@code null} lo ignora
 * @param estado   estado de la matrícula por el que filtrar; {@code null} lo ignora
 * @param notaMin  nota mínima, inclusive; {@code null} lo ignora
 * @param notaMax  nota máxima, inclusive; {@code null} lo ignora
 * @see com.example.proyectospring.data.specifications.MatriculaSpecifications
 */
public record MatriculaFiltro(
        Long alumnoId,
        Long cursoId,
        EstadoMatricula estado,
        Double notaMin,
        Double notaMax
) {}
