package com.example.proyectospring.domain.dto.response;

import java.time.LocalDate;

import com.example.proyectospring.domain.entities.EstadoMatricula;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representación completa e inmutable de una matrícula devuelta por la API.
 *
 * <p>Embebe los resúmenes del {@link AlumnoResumen alumno} y del
 * {@link CursoResumen curso} implicados, evitando exponer el grafo completo y los
 * ciclos entre entidades. Lo produce
 * {@link com.example.proyectospring.domain.mappers.MatriculaMapper#toResponse}.
 *
 * @param id             identificador de la matrícula
 * @param alumno         resumen del alumno matriculado
 * @param curso          resumen del curso de la matrícula
 * @param fechaMatricula fecha en que se realizó la matrícula
 * @param estado         estado actual de la matrícula
 * @param nota           nota final, o {@code null} si aún no se ha asignado
 */
@Schema(description = "Datos de salida de una matrícula, con resúmenes de alumno y curso")
public record MatriculaResponse(
        @Schema(description = "ID de la matrícula", example = "1") Long id,
        @Schema(description = "Alumno matriculado (resumen)") AlumnoResumen alumno,
        @Schema(description = "Curso de la matrícula (resumen)") CursoResumen curso,
        @Schema(description = "Fecha de la matrícula", example = "2024-09-15") LocalDate fechaMatricula,
        @Schema(description = "Estado de la matrícula", example = "ACTIVA") EstadoMatricula estado,
        @Schema(description = "Nota final (null si no asignada)", example = "8.5") Double nota
) {}
