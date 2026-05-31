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
 * Entidad JPA que representa a un alumno del sistema, persistida en la tabla {@code alumnos}.
 *
 * <p>Es uno de los agregados de dominio principales. Un alumno puede tener
 * varias {@link Matricula}, relación que se gestiona en cascada: al eliminar el
 * alumno se eliminan sus matrículas ({@link CascadeType#ALL} con
 * {@code orphanRemoval = true}).
 *
 * <p>No se expone directamente como cuerpo de respuesta de la API; se proyecta a
 * {@link com.example.proyectospring.domain.dto.response.AlumnoResponse} o
 * {@link com.example.proyectospring.domain.dto.response.AlumnoResumen} mediante
 * {@link com.example.proyectospring.domain.mappers.AlumnoMapper}. Los accesores
 * son generados por Lombok ({@link Getter}/{@link Setter}).
 *
 * @see Matricula
 * @see com.example.proyectospring.domain.dto.AlumnoDTO
 * @see com.example.proyectospring.domain.mappers.AlumnoMapper
 */
@Schema(description = "Entidad que representa un alumno en el sistema")
@Entity
@Table(name = "alumnos")
@Getter
@Setter
@NoArgsConstructor
public class Alumno {

    @Schema(description = "Identificador único del alumno", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre completo del alumno", example = "Juan García")
    @Column(nullable = false)
    private String nombre;

    @Schema(description = "Correo electrónico del alumno", example = "juan@example.com")
    @Column(unique = true)
    private String email;

    @Schema(description = "Fecha de registro del alumno en el sistema", example = "2024-01-15")
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    /**
     * Matrículas del alumno en distintos cursos (lado inverso de la relación).
     *
     * <p>Se cascadea por completo y se aplica borrado de huérfanos, de modo que
     * las matrículas viven y mueren con el alumno. Se marca con {@link JsonIgnore}
     * para evitar ciclos de serialización y exposición no deseada.
     */
    @Schema(description = "Matrículas del alumno en cursos", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matricula> matriculas = new ArrayList<>();
}
