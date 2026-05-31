package com.example.proyectospring.domain.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Punto de entrada de autenticación que responde {@code 401} en formato {@code ProblemDetail} (RFC 7807).
 *
 * <p>Spring Security lo invoca cuando una petición a un recurso protegido llega
 * sin autenticación válida. En lugar de la página de login por defecto, escribe
 * un cuerpo JSON de error coherente con el resto de la API.
 *
 * @see JwtAccessDeniedHandler
 * @see SecurityConfig
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Crea el punto de entrada con el serializador JSON.
     *
     * @param objectMapper serializador usado para escribir el cuerpo de error
     */
    public JwtAuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Escribe en la respuesta un {@code ProblemDetail} con estado {@code 401} y
     * el tipo de contenido {@code application/problem+json}.
     *
     * @param request       petición que provocó el fallo de autenticación
     * @param response      respuesta sobre la que se escribe el error
     * @param authException causa del fallo de autenticación
     * @throws IOException si falla la escritura en la respuesta
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Se requiere autenticación válida (token JWT)");
        problema.setTitle("No autenticado");
        problema.setInstance(java.net.URI.create(request.getRequestURI()));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(problema));
    }
}
