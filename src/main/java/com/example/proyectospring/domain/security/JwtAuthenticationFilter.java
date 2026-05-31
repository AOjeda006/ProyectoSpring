package com.example.proyectospring.domain.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que autentica cada petición a partir del access token JWT, si lo hay.
 *
 * <p>Se ejecuta una vez por petición ({@link OncePerRequestFilter}). Si la
 * cabecera {@code Authorization} contiene un token {@code Bearer} de tipo
 * <i>access</i> válido, carga el usuario y lo establece en el
 * {@link SecurityContextHolder}. <strong>No bloquea</strong> peticiones sin
 * token ni con token inválido: simplemente las deja sin autenticar y delega en
 * la cadena de seguridad la decisión de rechazarlas (devolviendo {@code 401} vía
 * {@link JwtAuthEntryPoint}).
 *
 * @see JwtService
 * @see JwtAuthEntryPoint
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Crea el filtro con sus dependencias.
     *
     * @param jwtService         servicio de validación de tokens
     * @param userDetailsService servicio para cargar el usuario asociado al token
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Si la petición trae un access token válido, autentica al usuario en el
     * contexto de seguridad; en cualquier caso, continúa la cadena de filtros.
     * Solo los tokens de tipo <i>access</i> autorizan: los refresh token se
     * ignoran aquí. Ante un token inválido o expirado, limpia el contexto y deja
     * pasar la petición sin autenticar.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HEADER);
        if (header == null || !header.startsWith(PREFIJO)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIJO.length());

        try {
            // Solo los access token autorizan peticiones; los refresh token se rechazan aquí.
            if (jwtService.esAccessToken(token)) {
                String username = jwtService.extraerUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.esValido(token, userDetails)) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (Exception e) {
            // Token inválido/expirado: se deja sin autenticar y la cadena devolverá 401 si hace falta.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
