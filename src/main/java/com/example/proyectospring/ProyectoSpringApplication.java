package com.example.proyectospring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Punto de entrada de la API REST de Gestión de Alumnos.
 *
 * <p>Aplicación Spring Boot que gestiona alumnos, profesores, cursos y sus
 * matrículas, con seguridad basada en JWT y documentación OpenAPI/Swagger. La
 * anotación {@link SpringBootApplication} activa la autoconfiguración y el
 * escaneo de componentes del paquete {@code com.example.proyectospring};
 * {@link OpenAPIDefinition} define los metadatos globales que aparecen en la
 * interfaz de Swagger.
 *
 * @see com.example.proyectospring.domain.security.SecurityConfig
 * @see com.example.proyectospring.config.OpenApiConfig
 */
@OpenAPIDefinition(
    info = @Info(
        title = "API de Gestión de Alumnos",
        version = "1.0",
        description = "API REST para crear, consultar, actualizar y eliminar alumnos. "
                + "Autenticación HTTP Basic requerida (usuario: admin, contraseña: 1234).",
        contact = @Contact(name = "Administrador", email = "admin@example.com")
    )
)
@SpringBootApplication
public class ProyectoSpringApplication {

    /**
     * Arranca la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos pasados al contexto de Spring
     */
    public static void main(String[] args) {
        SpringApplication.run(ProyectoSpringApplication.class, args);
    }
}
