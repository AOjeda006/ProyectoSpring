package com.example.proyectospring.domain.dto.auth;

import com.example.proyectospring.domain.entities.Rol;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Datos públicos de un usuario, devueltos tras crearlo.
 *
 * <p>Expone deliberadamente solo información no sensible: <strong>nunca</strong>
 * incluye la contraseña. Es la respuesta de
 * {@code POST /api/v1/auth/usuarios}.
 *
 * @param id         identificador del usuario
 * @param username   nombre de usuario
 * @param rol        rol asignado al usuario
 * @param habilitado {@code true} si la cuenta está habilitada para iniciar sesión
 */
@Schema(description = "Datos públicos de un usuario (nunca incluye la contraseña)")
public record UsuarioResponse(
        @Schema(description = "ID del usuario", example = "1") Long id,
        @Schema(description = "Nombre de usuario", example = "admin") String username,
        @Schema(description = "Rol del usuario", example = "ADMIN") Rol rol,
        @Schema(description = "Si la cuenta está habilitada", example = "true") boolean habilitado
) {}
