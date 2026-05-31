package com.example.proyectospring.domain.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos del registro público de un nuevo usuario.
 *
 * <p>Es el cuerpo de {@code POST /api/v1/auth/register}. El usuario creado a
 * partir de esta petición siempre recibe el rol
 * {@link com.example.proyectospring.domain.entities.Rol#ALUMNO}; para crear
 * usuarios con otros roles existe {@link CrearUsuarioRequest}, reservado a
 * administradores.
 *
 * @param username nombre de usuario único; obligatorio
 * @param password contraseña en claro; obligatoria y de al menos 6 caracteres (se cifra antes de persistir)
 * @see com.example.proyectospring.service.AuthService#register(RegisterRequest)
 */
@Schema(description = "Datos para registrar un nuevo usuario (se crea con rol ALUMNO)")
public record RegisterRequest(
        @Schema(description = "Nombre de usuario único", example = "nuevo_usuario")
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "secreto123")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password
) {}
