package com.example.proyectospring.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Vista resumida e inmutable de un profesor, embebida dentro de otras respuestas.
 *
 * <p>Se usa para mostrar el profesor de un curso ({@link CursoResponse}) sin
 * exponer todos sus datos. Lo produce
 * {@link com.example.proyectospring.domain.mappers.ProfesorMapper#toResumen}.
 *
 * @param id     identificador del profesor
 * @param nombre nombre completo del profesor
 * @see ProfesorResponse
 */
@Schema(description = "Vista resumida de un profesor, usada dentro de otras respuestas")
public record ProfesorResumen(
        @Schema(description = "ID del profesor", example = "1") Long id,
        @Schema(description = "Nombre del profesor", example = "María López") String nombre
) {}
