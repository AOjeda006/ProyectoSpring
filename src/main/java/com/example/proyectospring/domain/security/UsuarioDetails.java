package com.example.proyectospring.domain.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.proyectospring.domain.entities.Usuario;

/**
 * Adapta la entidad {@link Usuario} a la interfaz {@link UserDetails} de Spring Security.
 *
 * <p>Envuelve un usuario del dominio y expone sus credenciales y autoridades en
 * el formato que espera el framework. El {@link Rol} se traduce a una única
 * autoridad con el prefijo {@code ROLE_} (p. ej. {@code ROLE_ADMIN}), de modo
 * que las comprobaciones {@code hasRole(...)} funcionan correctamente. El estado
 * de la cuenta (no expirada, no bloqueada, credenciales vigentes) se considera
 * siempre válido; la habilitación se toma de {@link Usuario#isHabilitado()}.
 *
 * @see Usuario
 * @see CustomUserDetailsService
 */
public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    /**
     * Envuelve la entidad de usuario indicada.
     *
     * @param usuario usuario del dominio a adaptar; no debe ser {@code null}
     */
    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Devuelve la entidad de usuario subyacente.
     *
     * <p>Útil para que el resto de la aplicación acceda a los datos de dominio
     * (id, rol, etc.) a partir del principal autenticado.
     *
     * @return el {@link Usuario} envuelto; nunca {@code null}
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Devuelve una única autoridad derivada del rol del usuario, con el
     * prefijo {@code ROLE_}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Refleja el estado de habilitación del usuario en la base de datos.
     */
    @Override
    public boolean isEnabled() {
        return usuario.isHabilitado();
    }
}
