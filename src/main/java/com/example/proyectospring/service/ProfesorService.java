package com.example.proyectospring.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.proyectospring.data.repositories.ProfesorRepository;
import com.example.proyectospring.data.specifications.ProfesorSpecifications;
import com.example.proyectospring.domain.dto.ProfesorDTO;
import com.example.proyectospring.domain.dto.filter.ProfesorFiltro;
import com.example.proyectospring.domain.entities.Profesor;
import com.example.proyectospring.domain.exceptions.DuplicateResourceException;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;

/**
 * Lógica de negocio para la gestión de profesores.
 *
 * <p>Media entre el controlador y la capa de datos: combina el
 * {@link ProfesorRepository} con {@link ProfesorSpecifications} para la búsqueda
 * dinámica y aplica reglas como la unicidad del email. Además de su CRUD propio,
 * {@link com.example.proyectospring.service.CursoService} lo usa para resolver el
 * profesor de un curso.
 *
 * @see ProfesorRepository
 * @see com.example.proyectospring.uicontrollers.ProfesorController
 */
@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository profesorRepository;

    /**
     * Busca profesores aplicando un filtro dinámico, con paginación y ordenación.
     *
     * @param filtro   criterios de búsqueda; los campos vacíos se ignoran
     * @param pageable información de página, tamaño y orden
     * @return una página de profesores que cumplen el filtro; vacía si no hay coincidencias
     */
    public Page<Profesor> buscar(ProfesorFiltro filtro, Pageable pageable) {
        return profesorRepository.findAll(ProfesorSpecifications.conFiltro(filtro), pageable);
    }

    /**
     * Recupera un profesor por su identificador.
     *
     * @param id identificador del profesor
     * @return el profesor encontrado; nunca {@code null}
     * @throws ResourceNotFoundException si no existe ningún profesor con ese {@code id}
     */
    public Profesor findById(Long id) {
        return profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor", id));
    }

    /**
     * Crea y persiste un nuevo profesor.
     *
     * <p>Rechaza la operación si el email ya está registrado. Si el DTO no incluye
     * fecha de contratación, se asigna la fecha actual.
     *
     * @param dto datos del profesor a crear
     * @return el profesor creado, con su {@code id} generado
     * @throws DuplicateResourceException si ya existe un profesor con el mismo email
     */
    public Profesor create(ProfesorDTO dto) {
        if (profesorRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Ya existe un profesor con el email " + dto.getEmail());
        }
        Profesor profesor = new Profesor();
        profesor.setNombre(dto.getNombre());
        profesor.setEmail(dto.getEmail());
        profesor.setEspecialidad(dto.getEspecialidad());
        profesor.setFechaContratacion(
                dto.getFechaContratacion() != null ? dto.getFechaContratacion() : LocalDate.now());
        return profesorRepository.save(profesor);
    }

    /**
     * Actualiza los datos de un profesor existente.
     *
     * <p>Reescribe nombre, email y especialidad; la fecha de contratación solo se
     * modifica si el DTO la trae informada.
     *
     * @param id  identificador del profesor a actualizar
     * @param dto nuevos datos del profesor
     * @return el profesor actualizado
     * @throws ResourceNotFoundException si no existe ningún profesor con ese {@code id}
     */
    public Profesor update(Long id, ProfesorDTO dto) {
        Profesor existente = findById(id);
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setEspecialidad(dto.getEspecialidad());
        if (dto.getFechaContratacion() != null) {
            existente.setFechaContratacion(dto.getFechaContratacion());
        }
        return profesorRepository.save(existente);
    }

    /**
     * Elimina un profesor por su identificador.
     *
     * @param id identificador del profesor a eliminar
     * @throws ResourceNotFoundException si no existe ningún profesor con ese {@code id}
     */
    public void delete(Long id) {
        if (!profesorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profesor", id);
        }
        profesorRepository.deleteById(id);
    }
}
