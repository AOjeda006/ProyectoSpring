package com.example.proyectospring.domain.dto.filter;

/**
 * Criterios opcionales para la búsqueda dinámica y paginada de cursos.
 *
 * <p>Los campos {@code null} (o en blanco, para el nombre) se ignoran al
 * construir la consulta. La combinación se traduce a una
 * {@link org.springframework.data.jpa.domain.Specification} en
 * {@link com.example.proyectospring.data.specifications.CursoSpecifications}.
 *
 * @param nombre      fragmento del nombre a buscar (coincidencia parcial, sin distinguir mayúsculas); {@code null} lo ignora
 * @param profesorId  identificador del profesor que imparte el curso; {@code null} lo ignora
 * @param creditosMin número mínimo de créditos, inclusive; {@code null} lo ignora
 * @param creditosMax número máximo de créditos, inclusive; {@code null} lo ignora
 * @see com.example.proyectospring.data.specifications.CursoSpecifications
 */
public record CursoFiltro(
        String nombre,
        Long profesorId,
        Integer creditosMin,
        Integer creditosMax
) {}
