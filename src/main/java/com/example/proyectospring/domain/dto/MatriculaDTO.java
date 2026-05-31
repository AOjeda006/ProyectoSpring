package com.example.proyectospring.domain.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Datos de entrada para matricular un alumno en un curso.
 *
 * <p>Modela el cuerpo de la petición de creación de matrículas. Referencia al
 * alumno y al curso por sus identificadores, ambos obligatorios; el servicio
 * verifica que existan y que el alumno no esté ya matriculado en ese curso. La
 * fecha de matrícula es opcional y, si se omite, se asigna la fecha actual. El
 * estado inicial ({@link com.example.proyectospring.domain.entities.EstadoMatricula#ACTIVA})
 * lo fija el servicio, no el cliente.
 *
 * @see com.example.proyectospring.service.MatriculaService
 * @see ActualizarMatriculaDTO
 */
@Schema(description = "Datos de entrada para matricular un alumno en un curso")
public class MatriculaDTO {

    @Schema(description = "ID del alumno que se matricula (obligatorio)", example = "1")
    @NotNull(message = "El id del alumno es obligatorio")
    private Long alumnoId;

    @Schema(description = "ID del curso en el que se matricula (obligatorio)", example = "1")
    @NotNull(message = "El id del curso es obligatorio")
    private Long cursoId;

    @Schema(description = "Fecha de la matrícula (opcional, se asigna la fecha actual si se omite)", example = "2024-09-15")
    private LocalDate fechaMatricula;

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }
}
