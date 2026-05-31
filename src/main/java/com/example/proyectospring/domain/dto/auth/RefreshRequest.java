package com.example.proyectospring.domain.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Petición para renovar los tokens a partir de un refresh token válido.
 *
 * <p>Es el cuerpo de {@code POST /api/v1/auth/refresh}. Permite obtener un nuevo
 * par de tokens sin volver a enviar las credenciales, siempre que el refresh
 * token sea del tipo correcto y no haya expirado.
 *
 * @param refreshToken refresh token JWT emitido en el login; obligatorio
 * @see com.example.proyectospring.service.AuthService#refresh(String)
 */
@Schema(description = "Petición para renovar el access token a partir de un refresh token")
public record RefreshRequest(
        @Schema(description = "Refresh token válido emitido en el login")
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}
