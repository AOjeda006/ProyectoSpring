package com.example.proyectospring.domain.entities;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estado del ciclo de vida de una {@link Matricula}.
 *
 * <p>Toda matrícula nace en {@link #ACTIVA} y evoluciona hacia
 * {@link #FINALIZADA} o {@link #CANCELADA}. Se persiste como cadena
 * ({@link jakarta.persistence.EnumType#STRING}), por lo que renombrar una
 * constante rompería la compatibilidad con los registros existentes.
 *
 * @see Matricula#getEstado()
 */
@Schema(description = "Estado en el que se encuentra una matrícula")
public enum EstadoMatricula {

    /** El alumno está cursando actualmente la asignatura. Estado inicial al matricularse. */
    @Schema(description = "El alumno está cursando actualmente la asignatura")
    ACTIVA,

    /** El alumno completó el curso; suele llevar asociada una {@link Matricula#getNota() nota}. */
    @Schema(description = "El alumno completó el curso")
    FINALIZADA,

    /** La matrícula fue anulada antes de completar el curso. */
    @Schema(description = "La matrícula fue anulada")
    CANCELADA
}
