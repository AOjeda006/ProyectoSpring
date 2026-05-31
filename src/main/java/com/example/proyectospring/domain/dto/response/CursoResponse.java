package com.example.proyectospring.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representación completa e inmutable de un curso devuelta por la API.
 *
 * <p>Incluye un {@link ProfesorResumen} con el profesor que lo imparte (o
 * {@code null} si no tiene profesor asignado), en lugar del objeto profesor
 * completo. Lo produce
 * {@link com.example.proyectospring.domain.mappers.CursoMapper#toResponse}.
 *
 * @param id          identificador del curso
 * @param nombre      nombre del curso
 * @param descripcion descripción del contenido; puede ser {@code null}
 * @param creditos    número de créditos; puede ser {@code null}
 * @param profesor    resumen del profesor que imparte el curso, o {@code null} si no tiene
 * @see CursoResumen
 */
@Schema(description = "Datos de salida de un curso, incluye un resumen de su profesor")
public record CursoResponse(
        @Schema(description = "ID del curso", example = "1") Long id,
        @Schema(description = "Nombre del curso", example = "Acceso a Datos") String nombre,
        @Schema(description = "Descripción del curso", example = "Persistencia con JPA") String descripcion,
        @Schema(description = "Número de créditos", example = "6") Integer creditos,
        @Schema(description = "Profesor que imparte el curso (resumen)") ProfesorResumen profesor
) {}
