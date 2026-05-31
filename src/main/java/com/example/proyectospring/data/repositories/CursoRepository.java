package com.example.proyectospring.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.proyectospring.domain.entities.Curso;

/**
 * Repositorio de acceso a datos para la entidad {@link Curso}.
 *
 * <p>Aporta las operaciones CRUD y de paginación de {@link JpaRepository} y la
 * ejecución de consultas dinámicas de {@link JpaSpecificationExecutor}, usadas
 * junto a {@link com.example.proyectospring.data.specifications.CursoSpecifications}.
 * No añade consultas derivadas propias.
 *
 * @see Curso
 * @see com.example.proyectospring.service.CursoService
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>, JpaSpecificationExecutor<Curso> {
}
