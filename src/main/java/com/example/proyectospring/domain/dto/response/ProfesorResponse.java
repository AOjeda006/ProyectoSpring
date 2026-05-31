package com.example.proyectospring.domain.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representación completa e inmutable de un profesor devuelta por la API.
 *
 * <p>Es el cuerpo de respuesta de los endpoints de profesores. Lo produce
 * {@link com.example.proyectospring.domain.mappers.ProfesorMapper#toResponse},
 * omitiendo la colección de cursos para no exponer el grafo completo.
 *
 * @param id                identificador del profesor
 * @param nombre            nombre completo del profesor
 * @param email             correo electrónico del profesor
 * @param especialidad      área de conocimiento; puede ser {@code null}
 * @param fechaContratacion fecha de contratación del profesor
 * @see ProfesorResumen
 */
@Schema(description = "Datos de salida de un profesor")
public record ProfesorResponse(
        @Schema(description = "ID del profesor", example = "1") Long id,
        @Schema(description = "Nombre completo del profesor", example = "María López") String nombre,
        @Schema(description = "Email del profesor", example = "maria.lopez@example.com") String email,
        @Schema(description = "Especialidad", example = "Bases de Datos") String especialidad,
        @Schema(description = "Fecha de contratación", example = "2020-09-01") LocalDate fechaContratacion
) {}
