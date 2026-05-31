package com.example.proyectospring.uicontrollers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.proyectospring.domain.entities.Alumno;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;
import com.example.proyectospring.domain.mappers.AlumnoMapper;
import com.example.proyectospring.domain.security.JwtAuthenticationFilter;
import com.example.proyectospring.domain.security.SecurityConfig;
import com.example.proyectospring.service.AlumnoService;
import com.example.proyectospring.testsupport.MethodSecurityTestConfig;

/**
 * Pruebas de slice web ({@code @WebMvcTest}) de {@link AlumnoController}.
 *
 * <p>Cargan solo la capa web con el servicio y el mapeador simulados. Excluyen
 * la cadena de seguridad real y desactivan los filtros, pero importan
 * {@link MethodSecurityTestConfig} para poder verificar las reglas de
 * {@code @PreAuthorize} con {@code @WithMockUser}. Comprueban códigos de estado,
 * validación de entrada y control de acceso por rol.
 */
@WebMvcTest(controllers = AlumnoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@Import(MethodSecurityTestConfig.class)
class AlumnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlumnoService alumnoService;

    @MockitoBean
    private AlumnoMapper alumnoMapper;

    @Test
    @WithMockUser(roles = "ALUMNO")
    void listar_devuelve200() throws Exception {
        when(alumnoService.buscar(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/alumnos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_comoAdmin_devuelve201() throws Exception {
        Alumno creado = new Alumno();
        creado.setId(5L);
        when(alumnoService.create(any())).thenReturn(creado);

        mockMvc.perform(post("/api/v1/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"email\":\"juan@example.com\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_datosInvalidos_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"email\":\"no-es-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.nombre").exists());
    }

    @Test
    @WithMockUser(roles = "ALUMNO")
    void crear_comoAlumno_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\",\"email\":\"juan@example.com\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerPorId_inexistente_devuelve404() throws Exception {
        when(alumnoService.findById(eq(99L)))
                .thenThrow(new ResourceNotFoundException("Alumno", 99L));

        mockMvc.perform(get("/api/v1/alumnos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
