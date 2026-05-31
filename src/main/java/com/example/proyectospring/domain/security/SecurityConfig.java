package com.example.proyectospring.domain.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de Spring Security para la API.
 *
 * <p>Define una seguridad <i>stateless</i> basada en JWT: deshabilita CSRF y la
 * gestión de sesiones, declara qué rutas son públicas (login, registro,
 * refresh, documentación Swagger, salud de Actuator y consola H2) y exige
 * autenticación para el resto. Inserta el {@link JwtAuthenticationFilter} antes
 * del filtro estándar de usuario/contraseña y enchufa los manejadores de error
 * {@link JwtAuthEntryPoint} ({@code 401}) y {@link JwtAccessDeniedHandler}
 * ({@code 403}). La anotación {@link EnableMethodSecurity} habilita las
 * comprobaciones {@code @PreAuthorize} de los controladores.
 *
 * @see JwtAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * Crea la configuración con el filtro JWT y los manejadores de error.
     *
     * @param jwtAuthenticationFilter filtro que autentica las peticiones por token
     * @param jwtAuthEntryPoint       manejador de respuestas {@code 401}
     * @param jwtAccessDeniedHandler  manejador de respuestas {@code 403}
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthEntryPoint jwtAuthEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    /**
     * Codificador de contraseñas basado en BCrypt.
     *
     * <p>Lo usan el registro y la verificación de credenciales para no almacenar
     * nunca contraseñas en claro.
     *
     * @return el {@link PasswordEncoder} de la aplicación
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el {@link AuthenticationManager} gestionado por Spring Security.
     *
     * <p>Lo necesita {@link com.example.proyectospring.service.AuthService} para
     * verificar las credenciales durante el login.
     *
     * @param config configuración de autenticación de Spring
     * @return el gestor de autenticación de la aplicación
     * @throws Exception si no puede obtenerse de la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define la cadena de filtros de seguridad de la API.
     *
     * @param http constructor de la configuración de seguridad HTTP
     * @return la {@link SecurityFilterChain} resultante
     * @throws Exception si la configuración no puede construirse
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/refresh").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
