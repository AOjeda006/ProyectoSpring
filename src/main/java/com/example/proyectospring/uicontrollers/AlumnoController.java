package com.example.proyectospring.uicontrollers;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.example.proyectospring.domain.dto.AlumnoDTO;
import com.example.proyectospring.domain.dto.filter.AlumnoFiltro;
import com.example.proyectospring.domain.dto.response.AlumnoResponse;
import com.example.proyectospring.domain.entities.Alumno;
import com.example.proyectospring.domain.mappers.AlumnoMapper;
import com.example.proyectospring.service.AlumnoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de alumnos, bajo {@code /api/v1/alumnos}.
 *
 * <p>Expone el CRUD completo de alumnos con listado filtrado y paginado.
 * Requiere autenticación JWT; las consultas (listar, obtener) están disponibles
 * para cualquier usuario autenticado, mientras que las operaciones de escritura
 * (crear, actualizar, eliminar) exigen rol {@code ADMIN}. Delega la lógica en
 * {@link AlumnoService} y traduce las entidades a DTO con {@link AlumnoMapper}.
 *
 * @see AlumnoService
 * @see com.example.proyectospring.uicontrollers.GlobalExceptionHandler
 */
@Tag(name = "Alumnos", description = "Operaciones CRUD para la gestión de alumnos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private AlumnoMapper alumnoMapper;

    @Operation(
        summary = "Listar y filtrar alumnos",
        description = "Retorna alumnos con paginación y ordenación. Admite filtros opcionales combinables: "
                + "nombre, email y rango de fecha de registro (registradoDesde / registradoHasta)."
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    /**
     * Lista alumnos con filtros opcionales y paginación. Atiende {@code GET /api/v1/alumnos}.
     *
     * @param nombre          filtro por nombre (coincidencia parcial); opcional
     * @param email           filtro por email (coincidencia parcial); opcional
     * @param registradoDesde fecha de registro mínima, inclusive; opcional
     * @param registradoHasta fecha de registro máxima, inclusive; opcional
     * @param pageable        paginación y orden (por defecto, 10 por página ordenados por nombre)
     * @return una página de alumnos que cumplen los filtros
     */
    @GetMapping
    public Page<AlumnoResponse> listar(
            @Parameter(description = "Filtra por nombre (búsqueda parcial)", example = "Juan")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtra por email (búsqueda parcial)", example = "@example.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Registrados desde esta fecha (inclusive)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registradoDesde,
            @Parameter(description = "Registrados hasta esta fecha (inclusive)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registradoHasta,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        AlumnoFiltro filtro = new AlumnoFiltro(nombre, email, registradoDesde, registradoHasta);
        return alumnoService.buscar(filtro, pageable).map(alumnoMapper::toResponse);
    }

    @Operation(
        summary = "Obtener alumno por ID",
        description = "Busca y retorna un alumno concreto a partir de su identificador numérico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alumno encontrado"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    /**
     * Obtiene un alumno por su identificador. Atiende {@code GET /api/v1/alumnos/{id}}.
     *
     * @param id identificador del alumno
     * @return el alumno encontrado
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el alumno ({@code 404})
     */
    @GetMapping("/{id}")
    public AlumnoResponse obtenerPorId(
            @Parameter(description = "ID del alumno", example = "1", required = true)
            @PathVariable Long id) {
        return alumnoMapper.toResponse(alumnoService.findById(id));
    }

    @Operation(
        summary = "Crear nuevo alumno",
        description = "Registra un nuevo alumno. El nombre y el email son obligatorios. "
                + "El email debe tener formato válido. La fecha de registro se asigna automáticamente si se omite."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Alumno creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (nombre vacío, email nulo, vacío o con formato incorrecto)"),
        @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    /**
     * Crea un nuevo alumno. Atiende {@code POST /api/v1/alumnos}. Requiere rol {@code ADMIN}.
     *
     * <p>Incluye en la respuesta la cabecera {@code Location} con la URI del
     * recurso recién creado.
     *
     * @param alumnoDTO datos del alumno, validados
     * @return respuesta {@code 201 Created} con el alumno creado y su {@code Location}
     * @throws com.example.proyectospring.domain.exceptions.DuplicateResourceException
     *         si el email ya está registrado ({@code 409})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlumnoResponse> crear(@Valid @RequestBody AlumnoDTO alumnoDTO) {
        Alumno creado = alumnoService.create(alumnoDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(creado.getId())
                        .toUri();
        return ResponseEntity.created(location).body(alumnoMapper.toResponse(creado));
    }

    @Operation(
        summary = "Actualizar alumno existente",
        description = "Modifica el nombre, email y fecha de registro de un alumno identificado por su ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alumno actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    /**
     * Actualiza un alumno existente. Atiende {@code PUT /api/v1/alumnos/{id}}. Requiere rol {@code ADMIN}.
     *
     * @param id        identificador del alumno a actualizar
     * @param alumnoDTO nuevos datos del alumno, validados
     * @return el alumno actualizado
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el alumno ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AlumnoResponse actualizar(
            @Parameter(description = "ID del alumno a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody AlumnoDTO alumnoDTO) {
        return alumnoMapper.toResponse(alumnoService.update(id, alumnoDTO));
    }

    @Operation(
        summary = "Eliminar alumno",
        description = "Elimina definitivamente el alumno con el ID indicado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Alumno eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    /**
     * Elimina un alumno. Atiende {@code DELETE /api/v1/alumnos/{id}}. Requiere rol {@code ADMIN}.
     *
     * @param id identificador del alumno a eliminar
     * @return respuesta {@code 204 No Content} si se elimina correctamente
     * @throws com.example.proyectospring.domain.exceptions.ResourceNotFoundException
     *         si no existe el alumno ({@code 404})
     * @throws org.springframework.security.access.AccessDeniedException
     *         si quien invoca no tiene rol {@code ADMIN} ({@code 403})
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del alumno a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        alumnoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
