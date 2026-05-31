package com.example.proyectospring.domain.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Credenciales para iniciar sesión.
 *
 * <p>Es el cuerpo de {@code POST /api/v1/auth/login}. Si las credenciales son
 * válidas, la API responde con un {@link AuthResponse} que incluye el par de
 * tokens JWT.
 *
 * @param username nombre de usuario; obligatorio
 * @param password contraseña en claro; obligatoria
 * @see com.example.proyectospring.service.AuthService#login(LoginRequest)
 */
@Schema(description = "Credenciales para iniciar sesión")
public record LoginRequest(
        @Schema(description = "Nombre de usuario", example = "admin")
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @Schema(description = "Contraseña", example = "admin1234")
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
