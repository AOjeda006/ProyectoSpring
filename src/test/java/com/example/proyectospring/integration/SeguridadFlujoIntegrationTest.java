package com.example.proyectospring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

/**
 * Test de integración full-stack (controlador + seguridad JWT + servicio + repositorio + H2).
 * Ejercita la cadena de filtros real, los tokens reales y las reglas de roles.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeguridadFlujoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /** Hace login y devuelve el access token. */
    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.accessToken");
    }

    @Test
    void sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/alumnos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginAdmin_yAccedeAlumnos_200() throws Exception {
        String token = login("admin", "admin1234");

        mockMvc.perform(get("/api/v1/alumnos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminCreaAlumno_201() throws Exception {
        String token = login("admin", "admin1234");

        mockMvc.perform(post("/api/v1/alumnos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Integración\",\"email\":\"integracion@example.com\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void alumnoRegistrado_noPuedeCrearProfesor_403() throws Exception {
        // Registro público => rol ALUMNO
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alumno_it\",\"password\":\"secreto123\"}"))
                .andExpect(status().isCreated());

        String token = login("alumno_it", "secreto123");

        mockMvc.perform(post("/api/v1/profesores")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"X\",\"email\":\"x@example.com\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void credencialesIncorrectas_devuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }
}
