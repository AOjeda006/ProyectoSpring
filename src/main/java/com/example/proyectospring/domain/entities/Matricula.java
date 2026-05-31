package com.example.proyectospring.domain.entities;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad intermedia que modela la inscripción ({@code matrícula}) de un {@link Alumno} en un {@link Curso}.
 *
 * <p>Resuelve la relación muchos-a-muchos entre alumnos y cursos añadiendo
 * atributos propios de la inscripción ({@link #fechaMatricula}, {@link #estado}
 * y {@link #nota}). Una restricción de unicidad sobre {@code (alumno_id, curso_id)}
 * impide que un mismo alumno se matricule dos veces en el mismo curso; esa misma
 * regla se comprueba de antemano en
 * {@link com.example.proyectospring.service.MatriculaService} para devolver un
 * error de negocio claro.
 *
 * <p>Ambas relaciones son obligatorias ({@code optional = false}) y se cargan de
 * forma perezosa. Se proyecta a
 * {@link com.example.proyectospring.domain.dto.response.MatriculaResponse}
 * mediante {@link com.example.proyectospring.domain.mappers.MatriculaMapper}.
 *
 * @see EstadoMatricula
 * @see com.example.proyectospring.domain.dto.MatriculaDTO
 */
@Schema(description = "Entidad intermedia que representa la matrícula de un alumno en un curso")
@Entity
@Table(
    name = "matriculas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_matricula_alumno_curso",
        columnNames = {"alumno_id", "curso_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class Matricula {

    @Schema(description = "Identificador único de la matrícula", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Alumno matriculado")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @Schema(description = "Curso en el que se matricula el alumno")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Schema(description = "Fecha en la que se realizó la matrícula", example = "2024-09-15")
    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Schema(description = "Estado actual de la matrícula", example = "ACTIVA")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatricula estado;

    /**
     * Nota final obtenida en el curso, en una escala de 0 a 10.
     *
     * <p>Es {@code null} mientras la matrícula no haya finalizado y no se le haya
     * asignado calificación.
     */
    @Schema(description = "Nota final obtenida (null mientras el curso no haya finalizado)", example = "8.5")
    private Double nota;
}
