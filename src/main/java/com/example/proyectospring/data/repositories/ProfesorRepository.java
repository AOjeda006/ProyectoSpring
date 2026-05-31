package com.example.proyectospring.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.proyectospring.domain.entities.Profesor;

/**
 * Repositorio de acceso a datos para la entidad {@link Profesor}.
 *
 * <p>Combina CRUD y paginación ({@link JpaRepository}) con consultas dinámicas
 * ({@link JpaSpecificationExecutor}), usadas junto a
 * {@link com.example.proyectospring.data.specifications.ProfesorSpecifications}.
 *
 * @see Profesor
 * @see com.example.proyectospring.service.ProfesorService
 */
@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long>, JpaSpecificationExecutor<Profesor> {

    /**
     * Indica si ya existe un profesor con el email dado.
     *
     * <p>Se usa para garantizar la unicidad del email antes de crear un profesor.
     *
     * @param email email a comprobar
     * @return {@code true} si existe un profesor con ese email, {@code false} en caso contrario
     */
    boolean existsByEmail(String email);
}
