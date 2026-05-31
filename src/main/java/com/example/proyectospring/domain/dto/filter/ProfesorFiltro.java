package com.example.proyectospring.domain.dto.filter;

/**
 * Criterios opcionales para la búsqueda dinámica y paginada de profesores.
 *
 * <p>Los campos {@code null} o en blanco se ignoran al construir la consulta. La
 * combinación se traduce a una {@link org.springframework.data.jpa.domain.Specification}
 * en {@link com.example.proyectospring.data.specifications.ProfesorSpecifications}.
 *
 * @param nombre       fragmento del nombre a buscar (coincidencia parcial, sin distinguir mayúsculas); {@code null} lo ignora
 * @param especialidad fragmento de la especialidad a buscar (coincidencia parcial, sin distinguir mayúsculas); {@code null} lo ignora
 * @see com.example.proyectospring.data.specifications.ProfesorSpecifications
 */
public record ProfesorFiltro(
        String nombre,
        String especialidad
) {}
