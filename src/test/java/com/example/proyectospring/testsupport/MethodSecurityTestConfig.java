package com.example.proyectospring.testsupport;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Activa la seguridad a nivel de método (@PreAuthorize) en los slices @WebMvcTest,
 * para poder verificar las reglas de roles con @WithMockUser sin levantar el contexto completo.
 */
@TestConfiguration
@EnableMethodSecurity
public class MethodSecurityTestConfig {
}
