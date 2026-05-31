package com.example.proyectospring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.proyectospring.data.repositories.UsuarioRepository;
import com.example.proyectospring.domain.entities.Rol;
import com.example.proyectospring.domain.entities.Usuario;

/**
 * Inicializador que crea un usuario administrador al arrancar la aplicación.
 *
 * <p>Implementa {@link CommandLineRunner}, por lo que Spring Boot lo ejecuta una
 * vez levantado el contexto. Garantiza que siempre exista una cuenta de
 * administrador con la que gestionar el sistema desde el primer arranque. Las
 * credenciales se toman de la configuración ({@code app.admin.username} y
 * {@code app.admin.password}) y la operación es idempotente: si el usuario ya
 * existe, no hace nada.
 *
 * @see com.example.proyectospring.domain.entities.Usuario
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    /**
     * Crea el inicializador con sus dependencias y las credenciales del administrador.
     *
     * @param usuarioRepository repositorio donde se persiste el administrador
     * @param passwordEncoder   codificador con el que se cifra la contraseña
     * @param adminUsername     nombre de usuario del administrador, de {@code app.admin.username}
     * @param adminPassword     contraseña en claro del administrador, de {@code app.admin.password}
     */
    public DataInitializer(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin.username}") String adminUsername,
                           @Value("${app.admin.password}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Crea el usuario administrador con rol
     * {@link com.example.proyectospring.domain.entities.Rol#ADMIN} y la contraseña
     * cifrada, salvo que ya exista uno con ese nombre de usuario. No recibe ni usa
     * argumentos de línea de comandos.
     *
     * @param args argumentos de línea de comandos (ignorados)
     */
    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByUsername(adminUsername)) {
            return;
        }
        Usuario admin = new Usuario(
                adminUsername,
                passwordEncoder.encode(adminPassword),
                Rol.ADMIN);
        usuarioRepository.save(admin);
        log.info("Usuario administrador inicial '{}' creado.", adminUsername);
    }
}
