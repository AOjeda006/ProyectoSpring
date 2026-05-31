package com.example.proyectospring.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.proyectospring.data.repositories.UsuarioRepository;
import com.example.proyectospring.domain.dto.auth.AuthResponse;
import com.example.proyectospring.domain.dto.auth.CrearUsuarioRequest;
import com.example.proyectospring.domain.dto.auth.LoginRequest;
import com.example.proyectospring.domain.dto.auth.RegisterRequest;
import com.example.proyectospring.domain.dto.auth.UsuarioResponse;
import com.example.proyectospring.domain.entities.Rol;
import com.example.proyectospring.domain.entities.Usuario;
import com.example.proyectospring.domain.exceptions.DuplicateResourceException;
import com.example.proyectospring.domain.exceptions.InvalidTokenException;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;
import com.example.proyectospring.domain.security.JwtService;

/**
 * Lógica de negocio de autenticación y gestión de usuarios.
 *
 * <p>Orquesta el registro, el inicio de sesión, la renovación de tokens y el
 * alta de usuarios por parte de un administrador. Cifra las contraseñas con
 * {@link PasswordEncoder}, delega la verificación de credenciales en el
 * {@link AuthenticationManager} de Spring Security y emite los tokens JWT a
 * través de {@link JwtService}.
 *
 * @see JwtService
 * @see com.example.proyectospring.uicontrollers.AuthController
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Crea el servicio con sus dependencias (inyección por constructor).
     *
     * @param usuarioRepository     repositorio de usuarios
     * @param passwordEncoder       codificador BCrypt para las contraseñas
     * @param authenticationManager gestor de autenticación de Spring Security
     * @param jwtService            servicio de emisión y validación de tokens JWT
     */
    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario mediante el alta pública.
     *
     * <p>El usuario se crea siempre con rol
     * {@link com.example.proyectospring.domain.entities.Rol#ALUMNO} y queda
     * autenticado de inmediato: la respuesta ya incluye los tokens JWT.
     *
     * @param request datos de registro (nombre de usuario y contraseña en claro)
     * @return los tokens y datos del usuario recién registrado
     * @throws DuplicateResourceException si el nombre de usuario ya está en uso
     */
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("El nombre de usuario '" + request.username() + "' ya está en uso");
        }
        Usuario usuario = new Usuario(
                request.username(),
                passwordEncoder.encode(request.password()),
                Rol.ALUMNO);
        usuarioRepository.save(usuario);
        return generarRespuesta(usuario);
    }

    /**
     * Autentica a un usuario con sus credenciales y emite los tokens.
     *
     * @param request credenciales de acceso (nombre de usuario y contraseña)
     * @return los tokens y datos del usuario autenticado
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son incorrectas (se traduce a {@code 401})
     * @throws ResourceNotFoundException si, tras autenticarse, el usuario no se encuentra (situación anómala)
     */
    public AuthResponse login(LoginRequest request) {
        // Lanza AuthenticationException (401) si las credenciales son incorrectas.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + request.username()));
        return generarRespuesta(usuario);
    }

    /**
     * Renueva el par de tokens a partir de un refresh token válido.
     *
     * <p>Comprueba que el token sea efectivamente un refresh token y que su
     * usuario siga existiendo. Cualquier fallo de parseo, firma o expiración se
     * normaliza a {@link InvalidTokenException} para no filtrar detalles internos.
     *
     * @param refreshToken refresh token JWT emitido previamente
     * @return un nuevo par de tokens para el mismo usuario
     * @throws InvalidTokenException si el token no es un refresh token, está expirado, es inválido o su usuario ya no existe
     */
    public AuthResponse refresh(String refreshToken) {
        try {
            if (!jwtService.esRefreshToken(refreshToken)) {
                throw new InvalidTokenException("El token proporcionado no es un refresh token");
            }
            String username = jwtService.extraerUsername(refreshToken);
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new InvalidTokenException("El usuario del token ya no existe"));
            return generarRespuesta(usuario);
        } catch (InvalidTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidTokenException("Refresh token inválido o expirado");
        }
    }

    /**
     * Crea un usuario con un rol explícito (alta administrativa).
     *
     * <p>A diferencia de {@link #register(RegisterRequest)}, permite asignar
     * cualquier {@link com.example.proyectospring.domain.entities.Rol} y no emite
     * tokens: solo devuelve los datos públicos del usuario creado. El control de
     * que solo un administrador pueda invocarlo se realiza en el controlador con
     * {@code @PreAuthorize}.
     *
     * @param request datos del usuario a crear, incluido su rol
     * @return los datos públicos del usuario creado (sin contraseña)
     * @throws DuplicateResourceException si el nombre de usuario ya está en uso
     */
    public UsuarioResponse crearUsuario(CrearUsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("El nombre de usuario '" + request.username() + "' ya está en uso");
        }
        Usuario usuario = new Usuario(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.rol());
        usuarioRepository.save(usuario);
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getRol(), usuario.isHabilitado());
    }

    /**
     * Construye la respuesta de autenticación con un par de tokens recién emitidos.
     *
     * @param usuario usuario para el que se generan los tokens
     * @return la respuesta con access token, refresh token y datos del usuario
     */
    private AuthResponse generarRespuesta(Usuario usuario) {
        String accessToken = jwtService.generarAccessToken(usuario);
        String refreshToken = jwtService.generarRefreshToken(usuario);
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessExpirationSegundos(),
                usuario.getUsername(),
                usuario.getRol().name());
    }
}
