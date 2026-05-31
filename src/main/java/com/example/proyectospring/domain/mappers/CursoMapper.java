package com.example.proyectospring.domain.mappers;

import org.mapstruct.Mapper;

import com.example.proyectospring.domain.dto.response.CursoResponse;
import com.example.proyectospring.domain.dto.response.CursoResumen;
import com.example.proyectospring.domain.entities.Curso;

/**
 * Mapeador MapStruct entre la entidad {@link Curso} y sus DTO de salida.
 *
 * <p>Reutiliza {@link ProfesorMapper} ({@code uses}) para convertir el profesor
 * del curso en un {@link com.example.proyectospring.domain.dto.response.ProfesorResumen}.
 * La implementación la genera MapStruct y se registra como bean de Spring.
 *
 * @see Curso
 * @see CursoResponse
 * @see CursoResumen
 * @see ProfesorMapper
 */
@Mapper(componentModel = "spring", uses = ProfesorMapper.class)
public interface CursoMapper {

    /**
     * Convierte una entidad en su representación completa de salida.
     *
     * <p>El campo {@code profesor} se proyecta a {@code ProfesorResumen} mediante
     * {@link ProfesorMapper#toResumen}; si el curso no tiene profesor, queda
     * {@code null}.
     *
     * @param curso entidad a convertir; si es {@code null}, el resultado es {@code null}
     * @return el {@link CursoResponse} con los datos del curso y el resumen de su profesor
     */
    CursoResponse toResponse(Curso curso);

    /**
     * Convierte una entidad en su vista resumida para embeber en otras respuestas.
     *
     * @param curso entidad a convertir; si es {@code null}, el resultado es {@code null}
     * @return el {@link CursoResumen} con los datos identificativos del curso
     */
    CursoResumen toResumen(Curso curso);
}
