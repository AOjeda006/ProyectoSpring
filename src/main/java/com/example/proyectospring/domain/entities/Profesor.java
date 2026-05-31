package com.example.proyectospring.domain.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa a un profesor, persistida en la tabla {@code profesores}.
 *
 * <p>Un profesor puede impartir varios {@link Curso}. A diferencia de otras
 * relaciones del modelo, aquí <strong>no</strong> se aplica borrado de huérfanos
 * ({@code orphanRemoval = false}): un curso puede quedar sin profesor sin ser
 * eliminado, lo que evita borrados en cascada accidentales de cursos al
 * desvincular al profesor.
 *
 * <p>Se proyecta a
 * {@link com.example.proyectospring.domain.dto.response.ProfesorResponse} o
 * {@link com.example.proyectospring.domain.dto.response.ProfesorResumen}
 * mediante {@link com.example.proyectospring.domain.mappers.ProfesorMapper}. Los
 * accesores los genera Lombok ({@link Getter}/{@link Setter}).
 *
 * @see Curso
 * @see com.example.proyectospring.domain.dto.ProfesorDTO
 */
@Schema(description = "Entidad que representa un profesor que imparte cursos")
@Entity
@Table(name = "profesores")
@Getter
@Setter
@NoArgsConstructor
public class Profesor {

    @Schema(description = "Identificador único del profesor", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre completo del profesor", example = "María López")
    @Column(nullable = false)
    private String nombre;

    @Schema(description = "Correo electrónico del profesor", example = "maria.lopez@example.com")
    @Column(unique = true)
    private String email;

    @Schema(description = "Especialidad o área de conocimiento", example = "Bases de Datos")
    private String especialidad;

    @Schema(description = "Fecha de contratación del profesor", example = "2020-09-01")
    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    /**
     * Cursos que imparte el profesor (lado inverso de la relación).
     *
     * <p>No se eliminan huérfanos: desvincular un curso del profesor no lo borra.
     * Se marca con {@link JsonIgnore} para evitar ciclos de serialización.
     */
    @Schema(description = "Cursos impartidos por el profesor", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Curso> cursos = new ArrayList<>();
}
