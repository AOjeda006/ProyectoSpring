package com.example.proyectospring.domain.dto.auth;

import com.example.proyectospring.domain.entities.Rol;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos para que un administrador cree un usuario con un rol concreto.
 *
 * <p>Es el cuerpo de {@code POST /api/v1/auth/usuarios}, endpoint restringido al
 * rol {@link com.example.proyectospring.domain.entities.Rol#ADMIN}. A diferencia
 * del registro público ({@link RegisterRequest}), aquí el rol es explícito y
 * puede ser cualquiera de los disponibles.
 *
 * @param username nombre de usuario único; obligatorio
 * @param password contraseña en claro; obligatoria y de al menos 6 caracteres (se cifra antes de persistir)
 * @param rol      rol que se asigna al nuevo usuario; obligatorio
 * @see com.example.proyectospring.service.AuthService#crearUsuario(CrearUsuarioRequest)
 */
@Schema(description = "Datos para que un ADMIN cree un usuario con un rol concreto")
public record CrearUsuarioRequest(
        @Schema(description = "Nombre de usuario único", example = "profe_maria")
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "secreto123")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @Schema(description = "Rol asignado al usuario", example = "PROFESOR")
        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {}
