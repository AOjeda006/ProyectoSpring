package com.example.proyectospring.domain.mappers;

import org.mapstruct.Mapper;

import com.example.proyectospring.domain.dto.response.AlumnoResponse;
import com.example.proyectospring.domain.dto.response.AlumnoResumen;
import com.example.proyectospring.domain.entities.Alumno;

/**
 * Mapeador MapStruct entre la entidad {@link Alumno} y sus DTO de salida.
 *
 * <p>La implementación la genera MapStruct en tiempo de compilación y se registra
 * como bean de Spring ({@code componentModel = "spring"}), por lo que puede
 * inyectarse directamente. El mapeo es por nombre de campo coincidente.
 *
 * @see Alumno
 * @see AlumnoResponse
 * @see AlumnoResumen
 */
@Mapper(componentModel = "spring")
public interface AlumnoMapper {

    /**
     * Convierte una entidad en su representación completa de salida.
     *
     * @param alumno entidad a convertir; si es {@code null}, el resultado es {@code null}
     * @return el {@link AlumnoResponse} con todos los datos del alumno
     */
    AlumnoResponse toResponse(Alumno alumno);

    /**
     * Convierte una entidad en su vista resumida para embeber en otras respuestas.
     *
     * @param alumno entidad a convertir; si es {@code null}, el resultado es {@code null}
     * @return el {@link AlumnoResumen} con los datos identificativos del alumno
     */
    AlumnoResumen toResumen(Alumno alumno);
}
