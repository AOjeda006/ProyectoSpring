package com.example.proyectospring.uicontrollers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.proyectospring.domain.dto.auth.AuthResponse;
import com.example.proyectospring.domain.security.JwtAuthenticationFilter;
import com.example.proyectospring.domain.security.SecurityConfig;
import com.example.proyectospring.service.AuthService;
import com.example.proyectospring.testsupport.MethodSecurityTestConfig;

/**
 * Pruebas de slice web ({@code @WebMvcTest}) de {@link AuthController}.
 *
 * <p>Cargan solo la capa web con {@link AuthService} simulado. Verifican el
 * registro y su validación, así como el control de acceso por rol del alta
 * administrativa de usuarios mediante {@code @WithMockUser} y
 * {@link MethodSecurityTestConfig}.
 */
@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
@Import(MethodSecurityTestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_datosValidos_devuelve201() throws Exception {
        when(authService.register(any()))
                .thenReturn(new AuthResponse("at", "rt", "Bearer", 900, "nuevo", "ALUMNO"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nuevo\",\"password\":\"secreto123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("at"))
                .andExpect(jsonPath("$.rol").value("ALUMNO"));
    }

    @Test
    void register_passwordCorta_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nuevo\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearUsuario_comoAdmin_devuelve201() throws Exception {
        when(authService.crearUsuario(any()))
                .thenReturn(new com.example.proyectospring.domain.dto.auth.UsuarioResponse(
                        2L, "profe1", com.example.proyectospring.domain.entities.Rol.PROFESOR, true));

        mockMvc.perform(post("/api/v1/auth/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"profe1\",\"password\":\"secreto123\",\"rol\":\"PROFESOR\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("PROFESOR"));
    }

    @Test
    @WithMockUser(roles = "ALUMNO")
    void crearUsuario_comoAlumno_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/auth/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"hacker\",\"password\":\"secreto123\",\"rol\":\"ADMIN\"}"))
                .andExpect(status().isForbidden());
    }
}
