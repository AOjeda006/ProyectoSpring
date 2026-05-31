package com.example.proyectospring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.proyectospring.data.repositories.AlumnoRepository;
import com.example.proyectospring.data.repositories.CursoRepository;
import com.example.proyectospring.data.repositories.MatriculaRepository;
import com.example.proyectospring.domain.dto.MatriculaDTO;
import com.example.proyectospring.domain.entities.Alumno;
import com.example.proyectospring.domain.entities.Curso;
import com.example.proyectospring.domain.entities.EstadoMatricula;
import com.example.proyectospring.domain.entities.Matricula;
import com.example.proyectospring.domain.exceptions.DuplicateResourceException;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;

/**
 * Pruebas unitarias de {@link MatriculaService} con los repositorios simulados (Mockito).
 *
 * <p>Verifican la creación de matrículas en estado
 * {@link com.example.proyectospring.domain.entities.EstadoMatricula#ACTIVA}, el
 * rechazo de matrículas duplicadas y el manejo de alumno o curso inexistentes.
 */
@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private AlumnoRepository alumnoRepository;
    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    /**
     * Crea un DTO de matrícula válido (alumno 1, curso 2), base para los casos de prueba.
     *
     * @return un {@link MatriculaDTO} con los ids de alumno y curso rellenos
     */
    private MatriculaDTO dto() {
        MatriculaDTO dto = new MatriculaDTO();
        dto.setAlumnoId(1L);
        dto.setCursoId(2L);
        return dto;
    }

    @Test
    void create_alumnoYCursoExisten_creaActivaConFechaActual() {
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        Curso curso = new Curso();
        curso.setId(2L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
        when(matriculaRepository.existsByAlumnoIdAndCursoId(1L, 2L)).thenReturn(false);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        Matricula creada = matriculaService.create(dto());

        assertThat(creada.getEstado()).isEqualTo(EstadoMatricula.ACTIVA);
        assertThat(creada.getFechaMatricula()).isNotNull();
        assertThat(creada.getAlumno()).isSameAs(alumno);
        assertThat(creada.getCurso()).isSameAs(curso);
    }

    @Test
    void create_matriculaDuplicada_lanzaDuplicate() {
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        Curso curso = new Curso();
        curso.setId(2L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
        when(matriculaRepository.existsByAlumnoIdAndCursoId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> matriculaService.create(dto()))
                .isInstanceOf(DuplicateResourceException.class);

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void create_alumnoInexistente_lanzaResourceNotFound() {
        when(alumnoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matriculaService.create(dto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alumno");

        verify(matriculaRepository, never()).save(any());
    }
}
