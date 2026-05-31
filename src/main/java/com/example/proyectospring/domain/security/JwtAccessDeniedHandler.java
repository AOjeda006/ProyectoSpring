package com.example.proyectospring.domain.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Manejador de acceso denegado que responde {@code 403} en formato {@code ProblemDetail} (RFC 7807).
 *
 * <p>Spring Security lo invoca cuando un usuario <i>autenticado</i> intenta una
 * operación para la que no tiene el rol necesario. Escribe un cuerpo JSON de
 * error coherente con el resto de la API, en lugar de la respuesta por defecto.
 *
 * @see JwtAuthEntryPoint
 * @see SecurityConfig
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Crea el manejador con el serializador JSON.
     *
     * @param objectMapper serializador usado para escribir el cuerpo de error
     */
    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Escribe en la respuesta un {@code ProblemDetail} con estado {@code 403} y
     * el tipo de contenido {@code application/problem+json}.
     *
     * @param request               petición que fue rechazada
     * @param response              respuesta sobre la que se escribe el error
     * @param accessDeniedException causa del rechazo por falta de permisos
     * @throws IOException si falla la escritura en la respuesta
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "No tienes permisos suficientes para esta operación");
        problema.setTitle("Acceso denegado");
        problema.setInstance(java.net.URI.create(request.getRequestURI()));

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(problema));
    }
}
