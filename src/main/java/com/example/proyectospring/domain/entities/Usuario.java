package com.example.proyectospring.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa a un usuario autenticable del sistema, persistida en la tabla {@code usuarios}.
 *
 * <p>Almacena las credenciales de acceso y el {@link Rol} que determina los
 * permisos. La contraseña se guarda cifrada con BCrypt y nunca se serializa a
 * JSON ({@link JsonIgnore}). Esta entidad es la fuente de datos de
 * {@link com.example.proyectospring.domain.security.UsuarioDetails} para la
 * integración con Spring Security.
 *
 * <p>Es independiente de las entidades de dominio (alumno, profesor): un usuario
 * representa una identidad de acceso, no a una persona del catálogo académico.
 * Los accesores los genera Lombok ({@link Getter}/{@link Setter}).
 *
 * @see Rol
 * @see com.example.proyectospring.domain.security.UsuarioDetails
 */
@Schema(description = "Usuario del sistema con credenciales y rol de acceso")
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Schema(description = "Identificador único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nombre de usuario único para iniciar sesión", example = "admin")
    @Column(nullable = false, unique = true)
    private String username;

    @Schema(description = "Contraseña cifrada con BCrypt", accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Schema(description = "Rol del usuario", example = "ADMIN")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Schema(description = "Indica si la cuenta está habilitada", example = "true")
    @Column(nullable = false)
    private boolean habilitado = true;

    /**
     * Crea un usuario habilitado con las credenciales y el rol indicados.
     *
     * <p>La cuenta se marca como habilitada por defecto. La contraseña debe venir
     * ya cifrada por quien invoca este constructor (los servicios la codifican con
     * BCrypt antes de instanciar); este constructor no aplica ningún cifrado.
     *
     * @param username nombre de usuario único para iniciar sesión
     * @param password contraseña <strong>ya cifrada</strong>
     * @param rol      rol que determina los permisos del usuario
     */
    public Usuario(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.habilitado = true;
    }
}
