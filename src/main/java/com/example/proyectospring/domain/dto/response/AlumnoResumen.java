package com.example.proyectospring.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Vista resumida e inmutable de un alumno, embebida dentro de otras respuestas.
 *
 * <p>Se usa cuando solo hace falta identificar al alumno (por ejemplo, dentro de
 * {@link MatriculaResponse}) sin arrastrar todos sus datos ni sus relaciones. Lo
 * produce {@link com.example.proyectospring.domain.mappers.AlumnoMapper#toResumen}.
 *
 * @param id     identificador del alumno
 * @param nombre nombre completo del alumno
 * @param email  correo electrónico del alumno
 * @see AlumnoResponse
 */
@Schema(description = "Vista resumida de un alumno, usada dentro de otras respuestas")
public record AlumnoResumen(
        @Schema(description = "ID del alumno", example = "1") Long id,
        @Schema(description = "Nombre del alumno", example = "Juan García") String nombre,
        @Schema(description = "Email del alumno", example = "juan@example.com") String email
) {}
