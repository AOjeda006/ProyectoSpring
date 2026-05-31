package com.example.proyectospring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de humo del arranque de la aplicación.
 *
 * <p>{@link SpringBootTest} levanta el contexto completo de Spring bajo el perfil
 * {@code test} (que usa la base de datos H2 en memoria), verificando que la
 * configuración y el cableado de los beans son válidos.
 */
@SpringBootTest
@ActiveProfiles("test")
class ProyectoSpringApplicationTests {

    /**
     * Comprueba que el contexto de Spring se inicializa sin errores.
     *
     * <p>Si algún bean está mal configurado, la carga del contexto falla y, con
     * ella, este test.
     */
    @Test
    void contextLoads() {
    }
}
