package com.example.proyectospring.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectospring.domain.entities.Usuario;

/**
 * Repositorio de acceso a datos para la entidad {@link Usuario}.
 *
 * <p>Da soporte a la autenticación: la búsqueda por nombre de usuario alimenta a
 * {@link com.example.proyectospring.domain.security.CustomUserDetailsService} y
 * la comprobación de existencia evita nombres de usuario duplicados al
 * registrar o crear cuentas.
 *
 * @see Usuario
 * @see com.example.proyectospring.domain.security.CustomUserDetailsService
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario único.
     *
     * @param username nombre de usuario a buscar
     * @return un {@link Optional} con el usuario si existe, o vacío en caso contrario
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Indica si ya existe un usuario con el nombre de usuario dado.
     *
     * @param username nombre de usuario a comprobar
     * @return {@code true} si el nombre de usuario ya está en uso, {@code false} en caso contrario
     */
    boolean existsByUsername(String username);
}
