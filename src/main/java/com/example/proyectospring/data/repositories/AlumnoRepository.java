package com.example.proyectospring.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.proyectospring.domain.entities.Alumno;

/**
 * Repositorio de acceso a datos para la entidad {@link Alumno}.
 *
 * <p>Combina las operaciones CRUD y de paginación de {@link JpaRepository} con la
 * ejecución de consultas dinámicas de {@link JpaSpecificationExecutor}, que el
 * servicio usa junto a
 * {@link com.example.proyectospring.data.specifications.AlumnoSpecifications}
 * para filtrar de forma flexible. Spring Data genera la implementación en tiempo
 * de ejecución.
 *
 * @see Alumno
 * @see com.example.proyectospring.service.AlumnoService
 */
@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {

    /**
     * Indica si ya existe un alumno con el email dado.
     *
     * <p>Se usa para garantizar la unicidad del email antes de crear un alumno.
     *
     * @param email email a comprobar
     * @return {@code true} si existe un alumno con ese email, {@code false} en caso contrario
     */
    boolean existsByEmail(String email);
}
