package com.example.proyectospring.uicontrollers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyectospring.domain.dto.auth.AuthResponse;
import com.example.proyectospring.domain.dto.auth.CrearUsuarioRequest;
import com.example.proyectospring.domain.dto.auth.LoginRequest;
import com.example.proyectospring.domain.dto.auth.RefreshRequest;
import com.example.proyectospring.domain.dto.auth.RegisterRequest;
import com.example.proyectospring.domain.dto.auth.UsuarioResponse;
import com.example.proyectospring.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST de autenticación, bajo {@code /api/v1/auth}.
 *
 * <p>Expone el registro público, el inicio de sesión, la renovación de tokens y
 * el alta administrativa de usuarios. Es uno de los pocos controladores con
 * endpoints públicos (registro, login y refresh están permitidos sin token en
 * {@link com.example.proyectospring.domain.security.SecurityConfig}); el alta de
 * usuarios, en cambio, requiere rol {@code ADMIN}. Delega toda la lógica en
 * {@link AuthService}.
 *
 * @see AuthService
 */
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y gestión de tokens JWT")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Crea el controlador con su servicio de autenticación.
     *
     * @param authService servicio que implementa la lógica de autenticación
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea una cuenta con rol ALUMNO y devuelve los tokens JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado y autenticado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "El nombre de usuario ya existe")
    })
    /**
     * Registra un nuevo usuario (rol {@code ALUMNO}) y lo autentica. Atiende {@code POST /api/v1/auth/register}.
     *
     * @param request datos de registro, validados
     * @return los tokens y datos del usuario, con estado {@code 201 Created}
     * @throws com.example.proyectospring.domain.exceptions.DuplicateResourceException
     *         si el nombre de usuario ya existe ({@code 409})
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Iniciar sesión",
            description = "Valida las credenciales y devuelve un access token y un refresh token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticación correcta"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    /**
     * Inicia sesión y emite el par de tokens. Atiende {@code POST /api/v1/auth/login}.
     *
     * @param request credenciales, validadas
     * @return los tokens y datos del usuario, con estado {@code 200 OK}
     * @throws org.springframework.security.core.AuthenticationException
     *         si las credenciales son incorrectas ({@code 401})
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Renovar el access token",
            description = "A partir de un refresh token válido, emite un nuevo access token (y refresh token).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens renovados"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    /**
     * Renueva los tokens a partir de un refresh token. Atiende {@code POST /api/v1/auth/refresh}.
     *
     * @param request petición con el refresh token, validada
     * @return un nuevo par de tokens, con estado {@code 200 OK}
     * @throws com.example.proyectospring.domain.exceptions.InvalidTokenException
     *         si el refresh token es inválido o ha expirado ({@code 401})
     */
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @Operation(summary = "Crear un usuario con rol concreto (solo ADMIN)",
            description = "Permite a un administrador dar de alta usuarios con cualquier rol (ADMIN, PROFESOR, ALUMNO).",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Requiere rol ADMIN"),
        @ApiResponse(responseCode = "409", description = "El nombre de usuario ya existe")
    })
    /**
     * Crea un usuario con un rol concreto. Atiende {@code POST /api/v1/auth/usuarios}.
     *
     * <p>Restringido al rol {@code ADMIN} mediante {@code @PreAuthorize}.
     *
     * @param request datos del usuario y su rol, validados
     * @return los datos públicos del usuario creado, con estado {@code 201 Created}
     * @throws com.example.proyectospring.domain.exceptions.DuplicateResourceException
     *         si el nombre de usuario ya existe ({@code 409})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        return authService.crearUsuario(request);
    }
}
