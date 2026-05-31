package com.example.proyectospring.domain.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para crear o actualizar un {@link com.example.proyectospring.domain.entities.Alumno}.
 *
 * <p>Modela el cuerpo de las peticiones {@code POST} y {@code PUT} de alumnos.
 * Las restricciones de validación ({@link NotBlank}, {@link Email}) se aplican
 * sobre los argumentos anotados con {@link jakarta.validation.Valid} en el
 * controlador; sus mensajes se devuelven al cliente en caso de error.
 *
 * <p>La fecha de registro es opcional: si se omite, el servicio asigna la fecha
 * actual al crear el alumno.
 *
 * @see com.example.proyectospring.uicontrollers.AlumnoController
 * @see com.example.proyectospring.service.AlumnoService
 */
@Schema(description = "Datos de entrada para crear o actualizar un alumno")
public class AlumnoDTO {

    @Schema(description = "Nombre completo del alumno (obligatorio)", example = "Juan García")
    @NotBlank(message = "El nombre es obligatorio y no puede estar vacío")
    private String nombre;

    @Schema(description = "Email del alumno (obligatorio y con formato válido)", example = "juan@example.com")
    @NotBlank(message = "El email es obligatorio y no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Schema(description = "Fecha de registro del alumno (opcional, se asigna automáticamente si se omite)", example = "2024-01-15")
    private LocalDate fechaRegistro;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
