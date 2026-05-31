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

import com.example.proyectospring.domain.dto.ProfesorDTO;
import com.example.proyectospring.domain.dto.filter.ProfesorFiltro;
import com.example.proyectospring.domain.dto.response.ProfesorResponse;
import com.example.proyectospring.domain.entities.Profesor;
import com.example.proyectospring.domain.mappers.ProfesorMapper;
import com.example.proyectospring.service.ProfesorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de profesores, bajo {@code /api/v1/profesores}.
 *
 * <p>Expone el CRUD completo con listado filtrado y paginado. Requiere
 * autenticación JWT; las consultas están disponibles para cualquier usuario
 * autenticado y las operaciones de escritura exigen rol {@code ADMIN}. Delega la
 * lógica en {@link ProfesorService} y traduce a DTO con {@link ProfesorMapper}.
 *
 * @see ProfesorService
 */
@Tag(name = "Profesores", description = "Operaciones CRUD para la gestión de profesores")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/profesores")
public class ProfesorController {

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Operation(summary = "Listar y filtrar profesores",
            description = "Retorna profesores con paginación. Admite filtros opcionales combinables: nombre y especialidad.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    /**
     * Lista profesores con filtros opcionales y paginación. Atiende {@code GET /api/v1/profesores}.
     *
     * @param nombre       filtro por nombre (coincidencia parcial); opcional
     * @param especialidad filtro por especialidad (coincidencia parcial); opcional
     * @param pageable     paginación y orden (por defecto, 10 por página ordenados por nombre)
     * @return una página de profesores que cumplen los filtros
     */
    @GetMapping
    public Page<ProfesorResponse> listar(
            @Parameter(description = "Filtra por nombre (búsqueda parcial)", example = "María")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtra por especialidad (búsqueda parcial)", example = "Bases de Datos")
            @RequestParam(required = false) String especialidad,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        ProfesorFiltro filtro = new ProfesorFiltro(nombre, especialidad);
        return profesorService.buscar(filtro, pageable).map(profesorMapper::toResponse);
    }

    @Operation(summary = "Obtener profesor por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profesor encontrado"),
        @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    /**
     * Obtiene un profesor por su identificador. Atiende {@code GET /api/v1/profesores/{id}}.
     *
     * @param id identificador del profesor
     * @return el profesor encontrado
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el profesor ({@code 404})
     */
    @GetMapping("/{id}")
    public ProfesorResponse obtenerPorId(
            @Parameter(description = "ID del profesor", example = "1", required = true)
            @PathVariable Long id) {
        return profesorMapper.toResponse(profesorService.findById(id));
    }

    @Operation(summary = "Crear nuevo profesor")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Profesor creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    /**
     * Crea un nuevo profesor. Atiende {@code POST /api/v1/profesores}. Requiere rol {@code ADMIN}.
     *
     * <p>Incluye la cabecera {@code Location} con la URI del recurso creado.
     *
     * @param dto datos del profesor, validados
     * @return respuesta {@code 201 Created} con el profesor creado y su {@code Location}
     * @throws com.example.proyectospring.domain.exceptions.DuplicateResourceException
     *         si el email ya está registrado ({@code 409})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfesorResponse> crear(@Valid @RequestBody ProfesorDTO dto) {
        Profesor creado = profesorService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(profesorMapper.toResponse(creado));
    }

    @Operation(summary = "Actualizar profesor existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profesor actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    /**
     * Actualiza un profesor existente. Atiende {@code PUT /api/v1/profesores/{id}}. Requiere rol {@code ADMIN}.
     *
     * @param id  identificador del profesor a actualizar
     * @param dto nuevos datos del profesor, validados
     * @return el profesor actualizado
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el profesor ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProfesorResponse actualizar(
            @Parameter(description = "ID del profesor a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProfesorDTO dto) {
        return profesorMapper.toResponse(profesorService.update(id, dto));
    }

    @Operation(summary = "Eliminar profesor")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Profesor eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    /**
     * Elimina un profesor. Atiende {@code DELETE /api/v1/profesores/{id}}. Requiere rol {@code ADMIN}.
     *
     * @param id identificador del profesor a eliminar
     * @return respuesta {@code 204 No Content} si se elimina correctamente
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el profesor ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del profesor a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        profesorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
