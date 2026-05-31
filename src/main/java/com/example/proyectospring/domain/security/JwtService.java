package com.example.proyectospring.domain.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.proyectospring.domain.entities.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Genera y valida JSON Web Tokens firmados con HMAC-SHA256 (HS256).
 *
 * <p>Maneja dos tipos de token, distinguidos por el <i>claim</i> {@code tipo}:
 * <ul>
 *   <li><b>access</b>: vida corta; autoriza las peticiones a la API.</li>
 *   <li><b>refresh</b>: vida larga; sirve para emitir nuevos access token sin
 *       reintroducir las credenciales.</li>
 * </ul>
 *
 * <p>La clave de firma y los tiempos de expiración se inyectan desde la
 * configuración ({@code jwt.secret}, {@code jwt.access-expiration-ms},
 * {@code jwt.refresh-expiration-ms}). El secreto debe tener al menos 256 bits
 * (32 bytes) para HS256.
 *
 * @see JwtAuthenticationFilter
 * @see com.example.proyectospring.service.AuthService
 */
@Service
public class JwtService {

    private static final String CLAIM_TIPO = "tipo";
    private static final String CLAIM_ROL = "rol";
    private static final String TIPO_ACCESS = "access";
    private static final String TIPO_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    /**
     * Crea el servicio derivando la clave de firma a partir del secreto configurado.
     *
     * @param secret             secreto compartido para HS256; debe tener al menos 32 bytes
     * @param accessExpirationMs validez del access token, en milisegundos
     * @param refreshExpirationMs validez del refresh token, en milisegundos
     * @throws io.jsonwebtoken.security.WeakKeyException si el secreto es demasiado corto para HS256
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /**
     * Genera un access token para el usuario indicado.
     *
     * <p>Incluye como <i>subject</i> el nombre de usuario y, como claims, el tipo
     * {@code access} y el rol.
     *
     * @param usuario usuario para el que se emite el token
     * @return el access token JWT firmado y compactado
     */
    public String generarAccessToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim(CLAIM_TIPO, TIPO_ACCESS)
                .claim(CLAIM_ROL, usuario.getRol().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Genera un refresh token para el usuario indicado.
     *
     * <p>Lleva como <i>subject</i> el nombre de usuario y el tipo {@code refresh};
     * no incluye el rol, pues no autoriza peticiones por sí mismo.
     *
     * @param usuario usuario para el que se emite el token
     * @return el refresh token JWT firmado y compactado
     */
    public String generarRefreshToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim(CLAIM_TIPO, TIPO_REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) de un token.
     *
     * @param token token JWT a inspeccionar
     * @return el nombre de usuario contenido en el token
     * @throws io.jsonwebtoken.JwtException si el token es inválido, está expirado o su firma no coincide
     */
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Indica si el token es de tipo <i>access</i>.
     *
     * @param token token JWT a inspeccionar
     * @return {@code true} si su claim {@code tipo} es {@code access}
     * @throws io.jsonwebtoken.JwtException si el token es inválido, está expirado o su firma no coincide
     */
    public boolean esAccessToken(String token) {
        return TIPO_ACCESS.equals(extraerClaim(token, c -> c.get(CLAIM_TIPO, String.class)));
    }

    /**
     * Indica si el token es de tipo <i>refresh</i>.
     *
     * @param token token JWT a inspeccionar
     * @return {@code true} si su claim {@code tipo} es {@code refresh}
     * @throws io.jsonwebtoken.JwtException si el token es inválido, está expirado o su firma no coincide
     */
    public boolean esRefreshToken(String token) {
        return TIPO_REFRESH.equals(extraerClaim(token, c -> c.get(CLAIM_TIPO, String.class)));
    }

    /**
     * Comprueba que un token sea válido para un usuario concreto.
     *
     * <p>Verifica la firma, la no expiración y que el <i>subject</i> coincida con
     * el nombre del {@link UserDetails}. Cualquier excepción durante el proceso
     * se interpreta como token no válido y devuelve {@code false}.
     *
     * @param token       token JWT a validar
     * @param userDetails usuario contra el que se contrasta el token
     * @return {@code true} si el token es válido para ese usuario; {@code false} en caso contrario
     */
    public boolean esValido(String token, UserDetails userDetails) {
        try {
            String username = extraerUsername(token);
            return username.equals(userDetails.getUsername()) && !estaExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Devuelve la validez del access token expresada en segundos.
     *
     * <p>Se incluye en {@link com.example.proyectospring.domain.dto.auth.AuthResponse}
     * para que el cliente sepa cuándo renovar el token.
     *
     * @return los segundos de validez del access token
     */
    public long getAccessExpirationSegundos() {
        return accessExpirationMs / 1000;
    }

    /**
     * Indica si el token ya ha expirado.
     *
     * @param token token JWT a inspeccionar
     * @return {@code true} si la fecha de expiración es anterior al instante actual
     */
    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Verifica la firma del token, lo parsea y aplica un extractor sobre sus claims.
     *
     * <p>Método de apoyo que centraliza el parseo y la verificación de firma para
     * el resto de extractores.
     *
     * @param <T>      tipo del valor extraído
     * @param token    token JWT a parsear
     * @param resolver función que obtiene el valor deseado a partir de los {@link Claims}
     * @return el valor extraído de los claims del token
     * @throws io.jsonwebtoken.JwtException si el token es inválido, está expirado o su firma no coincide
     */
    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}
