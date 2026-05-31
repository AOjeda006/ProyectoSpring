package com.example.proyectospring.domain.dto;

import com.example.proyectospring.domain.entities.EstadoMatricula;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * Datos de entrada para actualizar el estado y la nota de una matrícula existente.
 *
 * <p>Modela el cuerpo de la petición {@code PUT} de matrículas. A diferencia de
 * {@link MatriculaDTO}, no permite reasignar el alumno ni el curso: solo cambia
 * el {@link #getEstado() estado} (obligatorio) y la {@link #getNota() nota}
 * (opcional, entre 0 y 10).
 *
 * @see MatriculaDTO
 * @see com.example.proyectospring.service.MatriculaService#update(Long, ActualizarMatriculaDTO)
 */
@Schema(description = "Datos para actualizar el estado y la nota de una matrícula")
public class ActualizarMatriculaDTO {

    @Schema(description = "Nuevo estado de la matrícula (obligatorio)", example = "FINALIZADA")
    @NotNull(message = "El estado de la matrícula es obligatorio")
    private EstadoMatricula estado;

    @Schema(description = "Nota final obtenida (entre 0 y 10, opcional)", example = "8.5")
    @DecimalMin(value = "0.0", message = "La nota no puede ser inferior a 0")
    @DecimalMax(value = "10.0", message = "La nota no puede ser superior a 10")
    private Double nota;

    public EstadoMatricula getEstado() {
        return estado;
    }

    public void setEstado(EstadoMatricula estado) {
        this.estado = estado;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }
}
