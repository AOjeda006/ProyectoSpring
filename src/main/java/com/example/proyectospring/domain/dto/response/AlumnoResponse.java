package com.example.proyectospring.domain.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representación completa e inmutable de un alumno devuelta por la API.
 *
 * <p>Es el cuerpo de respuesta de los endpoints de alumnos. Lo produce
 * {@link com.example.proyectospring.domain.mappers.AlumnoMapper#toResponse} a
 * partir de la entidad, omitiendo deliberadamente la colección de matrículas
 * para no exponer el grafo completo.
 *
 * @param id            identificador del alumno
 * @param nombre        nombre completo del alumno
 * @param email         correo electrónico del alumno
 * @param fechaRegistro fecha en que el alumno se dio de alta
 * @see AlumnoResumen
 */
@Schema(description = "Datos de salida de un alumno")
public record AlumnoResponse(
        @Schema(description = "ID del alumno", example = "1") Long id,
        @Schema(description = "Nombre completo del alumno", example = "Juan García") String nombre,
        @Schema(description = "Email del alumno", example = "juan@example.com") String email,
        @Schema(description = "Fecha de registro", example = "2024-01-15") LocalDate fechaRegistro
) {}
