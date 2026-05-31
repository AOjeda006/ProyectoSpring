package com.example.proyectospring.domain.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.proyectospring.data.repositories.UsuarioRepository;

/**
 * Implementación de {@link UserDetailsService} respaldada por la base de datos.
 *
 * <p>Es el punto de integración de Spring Security con el modelo de usuarios de
 * la aplicación: carga el usuario por su nombre desde {@link UsuarioRepository}
 * y lo envuelve en un {@link UsuarioDetails}. La usan tanto el
 * {@link AuthenticationManager} durante el login como
 * {@link JwtAuthenticationFilter} al validar un token.
 *
 * @see UsuarioDetails
 * @see JwtAuthenticationFilter
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Crea el servicio con el repositorio de usuarios.
     *
     * @param usuarioRepository repositorio del que se cargan los usuarios
     */
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Busca el usuario por su nombre y lo adapta a {@link UsuarioDetails}.
     *
     * @param username nombre de usuario a cargar
     * @return los detalles del usuario para Spring Security
     * @throws UsernameNotFoundException si no existe ningún usuario con ese nombre
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .map(UsuarioDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
