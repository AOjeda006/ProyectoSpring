package com.example.proyectospring.uicontrollers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.proyectospring.domain.dto.ActualizarMatriculaDTO;
import com.example.proyectospring.domain.dto.MatriculaDTO;
import com.example.proyectospring.domain.dto.filter.MatriculaFiltro;
import com.example.proyectospring.domain.dto.response.MatriculaResponse;
import com.example.proyectospring.domain.entities.EstadoMatricula;
import com.example.proyectospring.domain.entities.Matricula;
import com.example.proyectospring.domain.mappers.MatriculaMapper;
import com.example.proyectospring.service.MatriculaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de matrículas, bajo {@code /api/v1/matriculas}.
 *
 * <p>Expone la inscripción de alumnos en cursos y la gestión de su estado y
 * nota, con listado filtrado y paginado. Requiere autenticación JWT: las
 * consultas están abiertas a cualquier usuario autenticado; crear y actualizar
 * requieren rol {@code ADMIN} o {@code PROFESOR}; eliminar queda reservado a
 * {@code ADMIN}. Delega la lógica en {@link MatriculaService} y traduce a DTO con
 * {@link MatriculaMapper}.
 *
 * @see MatriculaService
 */
@Tag(name = "Matrículas", description = "Gestión de matrículas de alumnos en cursos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/matriculas")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private MatriculaMapper matriculaMapper;

    @Operation(summary = "Listar y filtrar matrículas",
            description = "Retorna matrículas con paginación. Admite filtros opcionales combinables: "
                    + "alumnoId, cursoId, estado y rango de nota (notaMin / notaMax).")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    /**
     * Lista matrículas con filtros opcionales y paginación. Atiende {@code GET /api/v1/matriculas}.
     *
     * @param alumnoId filtro por identificador del alumno; opcional
     * @param cursoId  filtro por identificador del curso; opcional
     * @param estado   filtro por estado de la matrícula; opcional
     * @param notaMin  nota mínima, inclusive; opcional
     * @param notaMax  nota máxima, inclusive; opcional
     * @param pageable paginación y orden (por defecto, 10 por página ordenados por fecha de matrícula)
     * @return una página de matrículas que cumplen los filtros
     */
    @GetMapping
    public Page<MatriculaResponse> listar(
            @Parameter(description = "Filtra por ID de alumno", example = "1")
            @RequestParam(required = false) Long alumnoId,
            @Parameter(description = "Filtra por ID de curso", example = "1")
            @RequestParam(required = false) Long cursoId,
            @Parameter(description = "Filtra por estado", example = "ACTIVA")
            @RequestParam(required = false) EstadoMatricula estado,
            @Parameter(description = "Nota mínima (inclusive)", example = "5.0")
            @RequestParam(required = false) Double notaMin,
            @Parameter(description = "Nota máxima (inclusive)", example = "10.0")
            @RequestParam(required = false) Double notaMax,
            @PageableDefault(size = 10, sort = "fechaMatricula") Pageable pageable) {
        MatriculaFiltro filtro = new MatriculaFiltro(alumnoId, cursoId, estado, notaMin, notaMax);
        return matriculaService.buscar(filtro, pageable).map(matriculaMapper::toResponse);
    }

    @Operation(summary = "Obtener matrícula por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matrícula encontrada"),
        @ApiResponse(responseCode = "404", description = "Matrícula no encontrada")
    })
    /**
     * Obtiene una matrícula por su identificador. Atiende {@code GET /api/v1/matriculas/{id}}.
     *
     * @param id identificador de la matrícula
     * @return la matrícula encontrada, con los resúmenes de alumno y curso
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe la matrícula ({@code 404})
     */
    @GetMapping("/{id}")
    public MatriculaResponse obtenerPorId(
            @Parameter(description = "ID de la matrícula", example = "1", required = true)
            @PathVariable Long id) {
        return matriculaMapper.toResponse(matriculaService.findById(id));
    }

    @Operation(summary = "Matricular un alumno en un curso",
            description = "Crea una matrícula en estado ACTIVA. El alumno y el curso deben existir "
                    + "y el alumno no puede estar ya matriculado en ese curso.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Matrícula creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Alumno o curso no encontrado"),
        @ApiResponse(responseCode = "409", description = "El alumno ya está matriculado en ese curso")
    })
    /**
     * Matricula un alumno en un curso. Atiende {@code POST /api/v1/matriculas}. Requiere rol {@code ADMIN} o {@code PROFESOR}.
     *
     * <p>La matrícula se crea en estado
     * {@link com.example.proyectospring.domain.entities.EstadoMatricula#ACTIVA} e
     * incluye la cabecera {@code Location} con la URI del recurso creado.
     *
     * @param dto datos de la matrícula (ids de alumno y curso, fecha opcional), validados
     * @return respuesta {@code 201 Created} con la matrícula creada y su {@code Location}
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el alumno o el curso ({@code 404})
     * @throws com.example.proyectospring.domain.exceptions.DuplicateResourceException
     *         si el alumno ya está matriculado en ese curso ({@code 409})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ni {@code PROFESOR} ({@code 403})
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<MatriculaResponse> crear(@Valid @RequestBody MatriculaDTO dto) {
        Matricula creada = matriculaService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getId())
                .toUri();
        return ResponseEntity.created(location).body(matriculaMapper.toResponse(creada));
    }

    @Operation(summary = "Actualizar estado y nota de una matrícula",
            description = "Permite cambiar el estado (ACTIVA, FINALIZADA, CANCELADA) y asignar la nota final.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matrícula actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Matrícula no encontrada")
    })
    /**
     * Actualiza el estado y la nota de una matrícula. Atiende {@code PUT /api/v1/matriculas/{id}}. Requiere rol {@code ADMIN} o {@code PROFESOR}.
     *
     * @param id  identificador de la matrícula a actualizar
     * @param dto nuevo estado y nota, validados
     * @return la matrícula actualizada
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe la matrícula ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ni {@code PROFESOR} ({@code 403})
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public MatriculaResponse actualizar(
            @Parameter(description = "ID de la matrícula a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ActualizarMatriculaDTO dto) {
        return matriculaMapper.toResponse(matriculaService.update(id, dto));
    }

    @Operation(summary = "Eliminar matrícula")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Matrícula eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Matrícula no encontrada")
    })
    /**
     * Elimina una matrícula. Atiende {@code DELETE /api/v1/matriculas/{id}}. Requiere rol {@code ADMIN}.
     *
     * @param id identificador de la matrícula a eliminar
     * @return respuesta {@code 204 No Content} si se elimina correctamente
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe la matrícula ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la matrícula a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        matriculaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
