package com.example.proyectospring.domain.mappers;

import org.mapstruct.Mapper;

import com.example.proyectospring.domain.dto.response.ProfesorResponse;
import com.example.proyectospring.domain.dto.response.ProfesorResumen;
import com.example.proyectospring.domain.entities.Profesor;

/**
 * Mapeador MapStruct entre la entidad {@link Profesor} y sus DTO de salida.
 *
 * <p>La implementación la genera MapStruct y se registra como bean de Spring.
 * {@link CursoMapper} lo reutiliza ({@code uses}) para resolver el profesor de un
 * curso a su forma resumida.
 *
 * @see Profesor
 * @see ProfesorResponse
 * @see ProfesorResumen
 */
@Mapper(componentModel = "spring")
public interface ProfesorMapper {

    /**
     * Convierte una entidad en su representación completa de salida.
     *
     * @param profesor entidad a convertir; si es {@code null}, el resultado es {@code null}
     * @return el {@link ProfesorResponse} con todos los datos del profesor
     */
    ProfesorResponse toResponse(Profesor profesor);

    /**
     * Convierte una entidad en su vista resumida para embeber en otras respuestas.
     *
     * @param profesor entidad a convertir; si es {@code null}, el resultado es {@code null}
     * @return el {@link ProfesorResumen} con los datos identificativos del profesor
     */
    ProfesorResumen toResumen(Profesor profesor);
}
