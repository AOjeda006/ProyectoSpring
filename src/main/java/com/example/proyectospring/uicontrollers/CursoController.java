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

import com.example.proyectospring.domain.dto.CursoDTO;
import com.example.proyectospring.domain.dto.filter.CursoFiltro;
import com.example.proyectospring.domain.dto.response.CursoResponse;
import com.example.proyectospring.domain.entities.Curso;
import com.example.proyectospring.domain.mappers.CursoMapper;
import com.example.proyectospring.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de cursos, bajo {@code /api/v1/cursos}.
 *
 * <p>Expone el CRUD completo con listado filtrado y paginado. Requiere
 * autenticación JWT con un esquema de permisos algo más permisivo que otros
 * recursos: las consultas están abiertas a cualquier usuario autenticado; crear
 * y actualizar requieren rol {@code ADMIN} o {@code PROFESOR}; eliminar queda
 * reservado a {@code ADMIN}. Delega la lógica en {@link CursoService} y traduce a
 * DTO con {@link CursoMapper}.
 *
 * @see CursoService
 */
@Tag(name = "Cursos", description = "Operaciones CRUD para la gestión de cursos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private CursoMapper cursoMapper;

    @Operation(summary = "Listar y filtrar cursos",
            description = "Retorna cursos con paginación. Admite filtros opcionales combinables: "
                    + "nombre, profesorId y rango de créditos (creditosMin / creditosMax).")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    /**
     * Lista cursos con filtros opcionales y paginación. Atiende {@code GET /api/v1/cursos}.
     *
     * @param nombre      filtro por nombre (coincidencia parcial); opcional
     * @param profesorId  filtro por identificador del profesor; opcional
     * @param creditosMin número mínimo de créditos, inclusive; opcional
     * @param creditosMax número máximo de créditos, inclusive; opcional
     * @param pageable    paginación y orden (por defecto, 10 por página ordenados por nombre)
     * @return una página de cursos que cumplen los filtros
     */
    @GetMapping
    public Page<CursoResponse> listar(
            @Parameter(description = "Filtra por nombre (búsqueda parcial)", example = "Datos")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtra por ID de profesor", example = "1")
            @RequestParam(required = false) Long profesorId,
            @Parameter(description = "Créditos mínimos (inclusive)", example = "3")
            @RequestParam(required = false) Integer creditosMin,
            @Parameter(description = "Créditos máximos (inclusive)", example = "9")
            @RequestParam(required = false) Integer creditosMax,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        CursoFiltro filtro = new CursoFiltro(nombre, profesorId, creditosMin, creditosMax);
        return cursoService.buscar(filtro, pageable).map(cursoMapper::toResponse);
    }

    @Operation(summary = "Obtener curso por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    /**
     * Obtiene un curso por su identificador. Atiende {@code GET /api/v1/cursos/{id}}.
     *
     * @param id identificador del curso
     * @return el curso encontrado, con el resumen de su profesor
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el curso ({@code 404})
     */
    @GetMapping("/{id}")
    public CursoResponse obtenerPorId(
            @Parameter(description = "ID del curso", example = "1", required = true)
            @PathVariable Long id) {
        return cursoMapper.toResponse(cursoService.findById(id));
    }

    @Operation(summary = "Crear nuevo curso")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Curso creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Profesor indicado no encontrado")
    })
    /**
     * Crea un nuevo curso. Atiende {@code POST /api/v1/cursos}. Requiere rol {@code ADMIN} o {@code PROFESOR}.
     *
     * <p>Incluye la cabecera {@code Location} con la URI del recurso creado.
     *
     * @param dto datos del curso, validados; puede incluir el id de su profesor
     * @return respuesta {@code 201 Created} con el curso creado y su {@code Location}
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si se indica un profesor que no existe ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ni {@code PROFESOR} ({@code 403})
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<CursoResponse> crear(@Valid @RequestBody CursoDTO dto) {
        Curso creado = cursoService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(cursoMapper.toResponse(creado));
    }

    @Operation(summary = "Actualizar curso existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Curso o profesor no encontrado")
    })
    /**
     * Actualiza un curso existente. Atiende {@code PUT /api/v1/cursos/{id}}. Requiere rol {@code ADMIN} o {@code PROFESOR}.
     *
     * @param id  identificador del curso a actualizar
     * @param dto nuevos datos del curso, validados; puede incluir el id de su profesor
     * @return el curso actualizado
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el curso o el profesor indicado ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ni {@code PROFESOR} ({@code 403})
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public CursoResponse actualizar(
            @Parameter(description = "ID del curso a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CursoDTO dto) {
        return cursoMapper.toResponse(cursoService.update(id, dto));
    }

    @Operation(summary = "Eliminar curso")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Curso eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    /**
     * Elimina un curso. Atiende {@code DELETE /api/v1/cursos/{id}}. Requiere rol {@code ADMIN}.
     *
     * <p>El borrado arrastra en cascada las matrículas del curso (ver
     * {@link com.example.proyectospring.domain.entities.Curso}).
     *
     * @param id identificador del curso a eliminar
     * @return respuesta {@code 204 No Content} si se elimina correctamente
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el curso ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del curso a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
