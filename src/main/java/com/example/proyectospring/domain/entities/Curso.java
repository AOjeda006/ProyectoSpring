package com.example.proyectospring.domain.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un curso o asignatura, persistida en la tabla {@code cursos}.
 *
 * <p>Cada curso puede tener, opcionalmente, un {@link Profesor} asignado
 * (relación {@code ManyToOne} cargada de forma perezosa) y agrupa las
 * {@link Matricula} de los alumnos inscritos, que se cascadean por completo con
 * borrado de huérfanos.
 *
 * <p>Se proyecta a
 * {@link com.example.proyectospring.domain.dto.response.CursoResponse} o
 * {@link com.example.proyectospring.domain.dto.response.CursoResumen} mediante
 * {@link com.example.proyectospring.domain.mappers.CursoMapper}. Los accesores
 * los genera Lombok ({@link Getter}/{@link Setter}).
 *
 * @see Profesor
 * @see Matricula
 * @see com.example.proyectospring.domain.dto.CursoDTO
 */
@Schema(description = "Entidad que representa un curso o asignatura")
@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
public class Curso {

    @Schema(description = "Identificador único del curso", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre del curso", example = "Acceso a Datos")
    @Column(nullable = false)
    private String nombre;

    @Schema(description = "Descripción del contenido del curso", example = "Persistencia con JPA y Spring Data")
    private String descripcion;

    @Schema(description = "Número de créditos del curso", example = "6")
    private Integer creditos;

    /**
     * Profesor que imparte el curso. Opcional: puede ser {@code null} si el curso
     * no tiene profesor asignado. Se carga de forma perezosa.
     */
    @Schema(description = "Profesor que imparte el curso")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    /**
     * Matrículas asociadas al curso (lado inverso de la relación).
     *
     * <p>Se cascadean por completo con borrado de huérfanos. Se marca con
     * {@link JsonIgnore} para evitar ciclos de serialización.
     */
    @Schema(description = "Matrículas asociadas al curso", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matricula> matriculas = new ArrayList<>();
}
