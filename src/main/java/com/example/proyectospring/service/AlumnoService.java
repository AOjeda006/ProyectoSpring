package com.example.proyectospring.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.proyectospring.data.repositories.AlumnoRepository;
import com.example.proyectospring.data.specifications.AlumnoSpecifications;
import com.example.proyectospring.domain.dto.AlumnoDTO;
import com.example.proyectospring.domain.dto.filter.AlumnoFiltro;
import com.example.proyectospring.domain.entities.Alumno;
import com.example.proyectospring.domain.exceptions.DuplicateResourceException;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;

/**
 * Lógica de negocio para la gestión de alumnos.
 *
 * <p>Media entre el controlador y la capa de datos: combina el
 * {@link AlumnoRepository} con {@link AlumnoSpecifications} para la búsqueda
 * dinámica y aplica las reglas de negocio, como la unicidad del email. Trabaja
 * con entidades {@link Alumno}; la conversión a DTO de salida es responsabilidad
 * del controlador.
 *
 * @see AlumnoRepository
 * @see com.example.proyectospring.uicontrollers.AlumnoController
 */
@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    /**
     * Busca alumnos aplicando un filtro dinámico, con paginación y ordenación.
     *
     * @param filtro   criterios de búsqueda; los campos vacíos se ignoran
     * @param pageable información de página, tamaño y orden
     * @return una página de alumnos que cumplen el filtro; vacía si no hay coincidencias
     */
    public Page<Alumno> buscar(AlumnoFiltro filtro, Pageable pageable) {
        return alumnoRepository.findAll(AlumnoSpecifications.conFiltro(filtro), pageable);
    }

    /**
     * Recupera un alumno por su identificador.
     *
     * @param id identificador del alumno
     * @return el alumno encontrado; nunca {@code null}
     * @throws ResourceNotFoundException si no existe ningún alumno con ese {@code id}
     */
    public Alumno findById(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno", id));
    }

    /**
     * Crea y persiste un nuevo alumno.
     *
     * <p>Rechaza la operación si el email ya está registrado. Si el DTO no incluye
     * fecha de registro, se asigna la fecha actual.
     *
     * @param dto datos del alumno a crear
     * @return el alumno creado, con su {@code id} generado
     * @throws DuplicateResourceException si ya existe un alumno con el mismo email
     */
    public Alumno create(AlumnoDTO dto) {
        if (alumnoRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Ya existe un alumno con el email " + dto.getEmail());
        }
        Alumno alumno = new Alumno();
        alumno.setNombre(dto.getNombre());
        alumno.setEmail(dto.getEmail());
        alumno.setFechaRegistro(dto.getFechaRegistro() != null ? dto.getFechaRegistro() : LocalDate.now());
        return alumnoRepository.save(alumno);
    }

    /**
     * Actualiza los datos de un alumno existente.
     *
     * <p>Reescribe nombre y email; la fecha de registro solo se modifica si el
     * DTO la trae informada, conservando en caso contrario la original.
     *
     * @param id  identificador del alumno a actualizar
     * @param dto nuevos datos del alumno
     * @return el alumno actualizado
     * @throws ResourceNotFoundException si no existe ningún alumno con ese {@code id}
     */
    public Alumno update(Long id, AlumnoDTO dto) {
        Alumno existente = findById(id);
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        if (dto.getFechaRegistro() != null) {
            existente.setFechaRegistro(dto.getFechaRegistro());
        }
        return alumnoRepository.save(existente);
    }

    /**
     * Elimina un alumno por su identificador.
     *
     * <p>Al eliminarse, sus matrículas se borran en cascada (ver {@link Alumno}).
     *
     * @param id identificador del alumno a eliminar
     * @throws ResourceNotFoundException si no existe ningún alumno con ese {@code id}
     */
    public void delete(Long id) {
        if (!alumnoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alumno", id);
        }
        alumnoRepository.deleteById(id);
    }
}
