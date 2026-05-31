package com.example.proyectospring.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.proyectospring.data.repositories.UsuarioRepository;

/**
 * Test de integración contra un MySQL real levantado con Testcontainers (Docker).
 * Se omite automáticamente si Docker no está disponible (disabledWithoutDocker = true),
 * de modo que el build no falla en máquinas sin Docker pero sí se ejecuta en CI.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class MySqlTestcontainerTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("alumnos_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void elAdminInicialSePersisteEnMySqlReal() {
        // DataInitializer siembra el admin al arrancar el contexto contra el MySQL del contenedor.
        assertThat(usuarioRepository.findByUsername("admin")).isPresent();
    }
}
