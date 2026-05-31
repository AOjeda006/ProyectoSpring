package com.example.proyectospring.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Declara el esquema de seguridad "bearerAuth" para que Swagger UI muestre el botón
 * "Authorize" y permita probar los endpoints protegidos con un token JWT.
 */
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Introduce el access token JWT obtenido en /api/v1/auth/login"
)
public class OpenApiConfig {
}
