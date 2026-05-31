package com.example.proyectospring.domain.mappers;

import org.mapstruct.Mapper;

import com.example.proyectospring.domain.dto.response.MatriculaResponse;
import com.example.proyectospring.domain.entities.Matricula;

/**
 * Mapeador MapStruct de la entidad {@link Matricula} a su DTO de salida.
 *
 * <p>Reutiliza {@link AlumnoMapper} y {@link CursoMapper} ({@code uses}) para
 * convertir el alumno y el curso de la matrícula en sus formas resumidas. La
 * implementación la genera MapStruct y se registra como bean de Spring.
 *
 * @see Matricula
 * @see MatriculaResponse
 */
@Mapper(componentModel = "spring", uses = {AlumnoMapper.class, CursoMapper.class})
public interface MatriculaMapper {

    /**
     * Convierte una matrícula en su representación de salida.
     *
     * <p>El campo {@code alumno} se proyecta a {@code AlumnoResumen} mediante
     * {@link AlumnoMapper#toResumen} y {@code curso} a {@code CursoResumen}
     * mediante {@link CursoMapper#toResumen}.
     *
     * @param matricula entidad a convertir; si es {@code null}, el resultado es {@code null}
     * @return el {@link MatriculaResponse} con los datos de la matrícula y los resúmenes de alumno y curso
     */
    MatriculaResponse toResponse(Matricula matricula);
}
