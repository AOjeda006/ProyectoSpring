package com.example.proyectospring.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.proyectospring.domain.entities.Matricula;

/**
 * Repositorio de acceso a datos para la entidad {@link Matricula}.
 *
 * <p>Combina CRUD y paginación ({@link JpaRepository}) con consultas dinámicas
 * ({@link JpaSpecificationExecutor}), usadas junto a
 * {@link com.example.proyectospring.data.specifications.MatriculaSpecifications}.
 *
 * @see Matricula
 * @see com.example.proyectospring.service.MatriculaService
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long>, JpaSpecificationExecutor<Matricula> {

    /**
     * Indica si el alumno ya está matriculado en el curso indicado.
     *
     * <p>Refleja en consulta la restricción de unicidad
     * {@code (alumno_id, curso_id)} de la tabla, permitiendo al servicio rechazar
     * matrículas duplicadas con un error de negocio antes de tocar la base de datos.
     *
     * @param alumnoId identificador del alumno
     * @param cursoId  identificador del curso
     * @return {@code true} si ya existe esa matrícula, {@code false} en caso contrario
     */
    boolean existsByAlumnoIdAndCursoId(Long alumnoId, Long cursoId);
}
