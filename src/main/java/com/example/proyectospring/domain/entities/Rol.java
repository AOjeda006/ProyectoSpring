package com.example.proyectospring.domain.entities;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Rol de un {@link Usuario}, que determina sus permisos en la API.
 *
 * <p>Cada rol se traduce a una autoridad de Spring Security con el prefijo
 * {@code ROLE_} (p. ej. {@code ROLE_ADMIN}) en
 * {@link com.example.proyectospring.domain.security.UsuarioDetails}, lo que
 * habilita las comprobaciones {@code hasRole(...)} de los controladores. Se
 * persiste como cadena ({@link jakarta.persistence.EnumType#STRING}).
 *
 * @see Usuario#getRol()
 * @see com.example.proyectospring.domain.security.UsuarioDetails#getAuthorities()
 */
@Schema(description = "Rol de un usuario en el sistema, determina sus permisos")
public enum Rol {

    /** Acceso total: gestión de usuarios y operaciones de escritura sobre todo el dominio. */
    @Schema(description = "Acceso total: gestión de usuarios y de todo el dominio")
    ADMIN,

    /** Puede crear y actualizar cursos y matrículas, pero no gestionar usuarios ni borrar recursos. */
    @Schema(description = "Puede gestionar cursos y matrículas")
    PROFESOR,

    /** Acceso de solo lectura al catálogo académico. */
    @Schema(description = "Acceso de solo lectura")
    ALUMNO
}
