package com.example.proyectospring.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.proyectospring.data.repositories.AlumnoRepository;
import com.example.proyectospring.data.repositories.CursoRepository;
import com.example.proyectospring.data.repositories.MatriculaRepository;
import com.example.proyectospring.data.specifications.MatriculaSpecifications;
import com.example.proyectospring.domain.dto.ActualizarMatriculaDTO;
import com.example.proyectospring.domain.dto.MatriculaDTO;
import com.example.proyectospring.domain.dto.filter.MatriculaFiltro;
import com.example.proyectospring.domain.entities.Alumno;
import com.example.proyectospring.domain.entities.Curso;
import com.example.proyectospring.domain.entities.EstadoMatricula;
import com.example.proyectospring.domain.entities.Matricula;
import com.example.proyectospring.domain.exceptions.DuplicateResourceException;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;

/**
 * Lógica de negocio para la gestión de matrículas.
 *
 * <p>Coordina las entidades {@link Alumno}, {@link Curso} y {@link Matricula}:
 * al crear una matrícula valida que el alumno y el curso existan y que no haya
 * una inscripción previa del mismo alumno en el mismo curso. Accede
 * directamente a los repositorios de alumno y curso (en lugar de a sus
 * servicios) por tratarse de simples comprobaciones de existencia.
 *
 * @see MatriculaRepository
 * @see com.example.proyectospring.uicontrollers.MatriculaController
 */
@Service
public class MatriculaService {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Busca matrículas aplicando un filtro dinámico, con paginación y ordenación.
     *
     * @param filtro   criterios de búsqueda; los campos vacíos se ignoran
     * @param pageable información de página, tamaño y orden
     * @return una página de matrículas que cumplen el filtro; vacía si no hay coincidencias
     */
    public Page<Matricula> buscar(MatriculaFiltro filtro, Pageable pageable) {
        return matriculaRepository.findAll(MatriculaSpecifications.conFiltro(filtro), pageable);
    }

    /**
     * Recupera una matrícula por su identificador.
     *
     * @param id identificador de la matrícula
     * @return la matrícula encontrada; nunca {@code null}
     * @throws ResourceNotFoundException si no existe ninguna matrícula con ese {@code id}
     */
    public Matricula findById(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula", id));
    }

    /**
     * Matricula a un alumno en un curso.
     *
     * <p>Verifica que existan tanto el alumno como el curso y que el alumno no
     * esté ya matriculado en ese curso. La matrícula se crea en estado
     * {@link EstadoMatricula#ACTIVA}; si el DTO no indica fecha, se usa la actual.
     *
     * @param dto datos de la matrícula (ids de alumno y curso, fecha opcional)
     * @return la matrícula creada, con su {@code id} generado
     * @throws ResourceNotFoundException  si no existe el alumno o el curso indicados
     * @throws DuplicateResourceException si el alumno ya está matriculado en ese curso
     */
    public Matricula create(MatriculaDTO dto) {
        Alumno alumno = alumnoRepository.findById(dto.getAlumnoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno", dto.getAlumnoId()));
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", dto.getCursoId()));

        if (matriculaRepository.existsByAlumnoIdAndCursoId(alumno.getId(), curso.getId())) {
            throw new DuplicateResourceException(
                    "El alumno " + alumno.getId() + " ya está matriculado en el curso " + curso.getId());
        }

        Matricula matricula = new Matricula();
        matricula.setAlumno(alumno);
        matricula.setCurso(curso);
        matricula.setFechaMatricula(
                dto.getFechaMatricula() != null ? dto.getFechaMatricula() : LocalDate.now());
        matricula.setEstado(EstadoMatricula.ACTIVA);
        return matriculaRepository.save(matricula);
    }

    /**
     * Actualiza el estado y la nota de una matrícula existente.
     *
     * <p>No permite reasignar el alumno ni el curso. La nota se establece tal cual
     * venga en el DTO, incluyendo {@code null} para borrarla.
     *
     * @param id  identificador de la matrícula a actualizar
     * @param dto nuevo estado y nota
     * @return la matrícula actualizada
     * @throws ResourceNotFoundException si no existe ninguna matrícula con ese {@code id}
     */
    public Matricula update(Long id, ActualizarMatriculaDTO dto) {
        Matricula existente = findById(id);
        existente.setEstado(dto.getEstado());
        existente.setNota(dto.getNota());
        return matriculaRepository.save(existente);
    }

    /**
     * Elimina una matrícula por su identificador.
     *
     * @param id identificador de la matrícula a eliminar
     * @throws ResourceNotFoundException si no existe ninguna matrícula con ese {@code id}
     */
    public void delete(Long id) {
        if (!matriculaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Matrícula", id);
        }
        matriculaRepository.deleteById(id);
    }
}
