package com.example.proyectospring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.proyectospring.data.repositories.AlumnoRepository;
import com.example.proyectospring.domain.dto.AlumnoDTO;
import com.example.proyectospring.domain.entities.Alumno;
import com.example.proyectospring.domain.exceptions.DuplicateResourceException;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;

/**
 * Pruebas unitarias de {@link AlumnoService} con el repositorio simulado (Mockito).
 *
 * <p>Verifican las reglas de negocio aisladas de la base de datos: asignación de
 * fecha por defecto al crear, rechazo de emails duplicados, traducción de
 * búsquedas sin resultado a {@link ResourceNotFoundException} y actualización de
 * campos.
 */
@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    @InjectMocks
    private AlumnoService alumnoService;

    /**
     * Crea un DTO de alumno válido con datos de ejemplo, base para los casos de prueba.
     *
     * @return un {@link AlumnoDTO} con nombre y email rellenos
     */
    private AlumnoDTO nuevoDto() {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre("Juan García");
        dto.setEmail("juan@example.com");
        return dto;
    }

    @Test
    void create_conEmailNuevo_guardaYAsignaFechaActual() {
        AlumnoDTO dto = nuevoDto();
        when(alumnoRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(alumnoRepository.save(any(Alumno.class))).thenAnswer(inv -> inv.getArgument(0));

        Alumno creado = alumnoService.create(dto);

        assertThat(creado.getNombre()).isEqualTo("Juan García");
        assertThat(creado.getEmail()).isEqualTo("juan@example.com");
        assertThat(creado.getFechaRegistro()).isEqualTo(LocalDate.now());
        verify(alumnoRepository).save(any(Alumno.class));
    }

    @Test
    void create_conEmailDuplicado_lanzaDuplicateYNoGuarda() {
        AlumnoDTO dto = nuevoDto();
        when(alumnoRepository.existsByEmail("juan@example.com")).thenReturn(true);

        assertThatThrownBy(() -> alumnoService.create(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("juan@example.com");

        verify(alumnoRepository, never()).save(any());
    }

    @Test
    void findById_existente_devuelveAlumno() {
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));

        assertThat(alumnoService.findById(1L).getId()).isEqualTo(1L);
    }

    @Test
    void findById_inexistente_lanzaResourceNotFound() {
        when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alumnoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_existente_actualizaCampos() {
        Alumno existente = new Alumno();
        existente.setId(1L);
        existente.setNombre("Antiguo");
        existente.setEmail("antiguo@example.com");
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(alumnoRepository.save(any(Alumno.class))).thenAnswer(inv -> inv.getArgument(0));

        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre("Nuevo");
        dto.setEmail("nuevo@example.com");

        Alumno actualizado = alumnoService.update(1L, dto);

        assertThat(actualizado.getNombre()).isEqualTo("Nuevo");
        assertThat(actualizado.getEmail()).isEqualTo("nuevo@example.com");
    }

    @Test
    void delete_inexistente_lanzaResourceNotFound() {
        when(alumnoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> alumnoService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(alumnoRepository, never()).deleteById(any());
    }
}
