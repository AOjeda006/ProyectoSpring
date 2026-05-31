package com.example.proyectospring.domain.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta con los tokens JWT emitidos tras autenticarse correctamente.
 *
 * <p>La devuelven los endpoints de registro, login y refresh. El cliente debe
 * enviar el {@code accessToken} en la cabecera {@code Authorization: Bearer ...}
 * de las peticiones protegidas y usar el {@code refreshToken} para renovarlo
 * cuando caduque.
 *
 * @param accessToken  token de acceso JWT, de vida corta
 * @param refreshToken token de refresco JWT, de vida larga
 * @param tokenType    esquema de autenticación, siempre {@code "Bearer"}
 * @param expiresIn    segundos hasta la expiración del {@code accessToken}
 * @param username     nombre del usuario autenticado
 * @param rol          rol del usuario autenticado
 */
@Schema(description = "Respuesta con los tokens emitidos tras autenticarse")
public record AuthResponse(
        @Schema(description = "Token de acceso JWT (vida corta)") String accessToken,
        @Schema(description = "Token de refresco JWT (vida larga)") String refreshToken,
        @Schema(description = "Tipo de token", example = "Bearer") String tokenType,
        @Schema(description = "Segundos hasta la expiración del access token", example = "900") long expiresIn,
        @Schema(description = "Nombre de usuario autenticado", example = "admin") String username,
        @Schema(description = "Rol del usuario", example = "ADMIN") String rol
) {}
