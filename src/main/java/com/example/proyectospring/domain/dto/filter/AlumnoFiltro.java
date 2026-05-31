package com.example.proyectospring.domain.dto.filter;

import java.time.LocalDate;

/**
 * Criterios opcionales para la búsqueda dinámica y paginada de alumnos.
 *
 * <p>Todos los campos son opcionales: los que sean {@code null} (o en blanco,
 * para las cadenas) se ignoran al construir la consulta. La combinación de
 * filtros se traduce a una {@link org.springframework.data.jpa.domain.Specification}
 * en {@link com.example.proyectospring.data.specifications.AlumnoSpecifications}.
 *
 * @param nombre          fragmento del nombre a buscar (coincidencia parcial, sin distinguir mayúsculas); {@code null} lo ignora
 * @param email           fragmento del email a buscar (coincidencia parcial, sin distinguir mayúsculas); {@code null} lo ignora
 * @param registradoDesde fecha de registro mínima, inclusive; {@code null} lo ignora
 * @param registradoHasta fecha de registro máxima, inclusive; {@code null} lo ignora
 * @see com.example.proyectospring.data.specifications.AlumnoSpecifications
 */
public record AlumnoFiltro(
        String nombre,
        String email,
        LocalDate registradoDesde,
        LocalDate registradoHasta
) {}
