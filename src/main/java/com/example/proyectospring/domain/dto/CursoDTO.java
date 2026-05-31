package com.example.proyectospring.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Datos de entrada para crear o actualizar un {@link com.example.proyectospring.domain.entities.Curso}.
 *
 * <p>Modela el cuerpo de las peticiones {@code POST} y {@code PUT} de cursos. El
 * profesor se referencia por su identificador ({@link #getProfesorId()}) en
 * lugar de anidar el objeto completo; el servicio lo resuelve contra la base de
 * datos. Si {@code profesorId} es {@code null}, el curso queda sin profesor
 * asignado.
 *
 * @see com.example.proyectospring.uicontrollers.CursoController
 * @see com.example.proyectospring.service.CursoService
 */
@Schema(description = "Datos de entrada para crear o actualizar un curso")
public class CursoDTO {

    @Schema(description = "Nombre del curso (obligatorio)", example = "Acceso a Datos")
    @NotBlank(message = "El nombre del curso es obligatorio y no puede estar vacío")
    private String nombre;

    @Schema(description = "Descripción del contenido del curso", example = "Persistencia con JPA y Spring Data")
    private String descripcion;

    @Schema(description = "Número de créditos del curso", example = "6")
    @Min(value = 1, message = "El número de créditos debe ser al menos 1")
    private Integer creditos;

    @Schema(description = "ID del profesor que imparte el curso (opcional)", example = "1")
    @Positive(message = "El id del profesor debe ser un número positivo")
    private Long profesorId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }
}
