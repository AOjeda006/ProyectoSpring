package com.example.proyectospring.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Vista resumida e inmutable de un curso, embebida dentro de otras respuestas.
 *
 * <p>Se usa para identificar el curso de una matrícula ({@link MatriculaResponse})
 * sin arrastrar su descripción, créditos ni relaciones. Lo produce
 * {@link com.example.proyectospring.domain.mappers.CursoMapper#toResumen}.
 *
 * @param id     identificador del curso
 * @param nombre nombre del curso
 * @see CursoResponse
 */
@Schema(description = "Vista resumida de un curso, usada dentro de otras respuestas")
public record CursoResumen(
        @Schema(description = "ID del curso", example = "1") Long id,
        @Schema(description = "Nombre del curso", example = "Acceso a Datos") String nombre
) {}
