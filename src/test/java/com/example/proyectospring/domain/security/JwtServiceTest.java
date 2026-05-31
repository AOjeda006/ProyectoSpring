package com.example.proyectospring.domain.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.proyectospring.domain.entities.Rol;
import com.example.proyectospring.domain.entities.Usuario;

/**
 * Pruebas unitarias de {@link JwtService}.
 *
 * <p>Verifican la emisión y validación de tokens sin levantar el contexto de
 * Spring: distinción entre access y refresh token, validez frente al usuario
 * correcto e incorrecto, y rechazo de tokens expirados.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Secret de al menos 32 bytes para HS256
        String secret = "clave-de-test-suficientemente-larga-para-hmac-sha256-1234567890";
        jwtService = new JwtService(secret, 900_000, 604_800_000);
        usuario = new Usuario("maria", "{bcrypt}xxx", Rol.PROFESOR);
    }

    @Test
    void accessToken_contieneUsernameYEsDeTipoAccess() {
        String token = jwtService.generarAccessToken(usuario);

        assertThat(jwtService.extraerUsername(token)).isEqualTo("maria");
        assertThat(jwtService.esAccessToken(token)).isTrue();
        assertThat(jwtService.esRefreshToken(token)).isFalse();
    }

    @Test
    void refreshToken_esDeTipoRefreshNoAccess() {
        String token = jwtService.generarRefreshToken(usuario);

        assertThat(jwtService.esRefreshToken(token)).isTrue();
        assertThat(jwtService.esAccessToken(token)).isFalse();
    }

    @Test
    void esValido_conUsuarioCoincidente_devuelveTrue() {
        String token = jwtService.generarAccessToken(usuario);
        UserDetails userDetails = new UsuarioDetails(usuario);

        assertThat(jwtService.esValido(token, userDetails)).isTrue();
    }

    @Test
    void esValido_conOtroUsuario_devuelveFalse() {
        String token = jwtService.generarAccessToken(usuario);
        UserDetails otro = new UsuarioDetails(new Usuario("pedro", "{bcrypt}yyy", Rol.ALUMNO));

        assertThat(jwtService.esValido(token, otro)).isFalse();
    }

    @Test
    void accessTokenExpirado_noEsValido() {
        // Expiración negativa => token ya caducado al emitirse
        JwtService caducador = new JwtService(
                "clave-de-test-suficientemente-larga-para-hmac-sha256-1234567890", -1000, 1000);
        String token = caducador.generarAccessToken(usuario);
        UserDetails userDetails = new UsuarioDetails(usuario);

        assertThat(caducador.esValido(token, userDetails)).isFalse();
    }
}
