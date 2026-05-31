package com.example.proyectospring.domain.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para crear o actualizar un {@link com.example.proyectospring.domain.entities.Profesor}.
 *
 * <p>Modela el cuerpo de las peticiones {@code POST} y {@code PUT} de
 * profesores. El nombre y el email son obligatorios; el email debe tener formato
 * válido. La fecha de contratación es opcional y, si se omite, el servicio
 * asigna la fecha actual al crear.
 *
 * @see com.example.proyectospring.uicontrollers.ProfesorController
 * @see com.example.proyectospring.service.ProfesorService
 */
@Schema(description = "Datos de entrada para crear o actualizar un profesor")
public class ProfesorDTO {

    @Schema(description = "Nombre completo del profesor (obligatorio)", example = "María López")
    @NotBlank(message = "El nombre es obligatorio y no puede estar vacío")
    private String nombre;

    @Schema(description = "Email del profesor (obligatorio y con formato válido)", example = "maria.lopez@example.com")
    @NotBlank(message = "El email es obligatorio y no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String email;

    @Schema(description = "Especialidad o área de conocimiento", example = "Bases de Datos")
    private String especialidad;

    @Schema(description = "Fecha de contratación (opcional, se asigna automáticamente si se omite)", example = "2020-09-01")
    private LocalDate fechaContratacion;

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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }
}
