package com.example.proyectospring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.proyectospring.data.repositories.CursoRepository;
import com.example.proyectospring.data.specifications.CursoSpecifications;
import com.example.proyectospring.domain.dto.CursoDTO;
import com.example.proyectospring.domain.dto.filter.CursoFiltro;
import com.example.proyectospring.domain.entities.Curso;
import com.example.proyectospring.domain.entities.Profesor;
import com.example.proyectospring.domain.exceptions.ResourceNotFoundException;

/**
 * Lógica de negocio para la gestión de cursos.
 *
 * <p>Media entre el controlador y la capa de datos. A diferencia de los demás
 * servicios, depende de {@link ProfesorService} para resolver y validar el
 * profesor que se asocia a un curso, de modo que asignar un profesor inexistente
 * produce un error de "no encontrado".
 *
 * @see CursoRepository
 * @see ProfesorService
 * @see com.example.proyectospring.uicontrollers.CursoController
 */
@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProfesorService profesorService;

    /**
     * Busca cursos aplicando un filtro dinámico, con paginación y ordenación.
     *
     * @param filtro   criterios de búsqueda; los campos vacíos se ignoran
     * @param pageable información de página, tamaño y orden
     * @return una página de cursos que cumplen el filtro; vacía si no hay coincidencias
     */
    public Page<Curso> buscar(CursoFiltro filtro, Pageable pageable) {
        return cursoRepository.findAll(CursoSpecifications.conFiltro(filtro), pageable);
    }

    /**
     * Recupera un curso por su identificador.
     *
     * @param id identificador del curso
     * @return el curso encontrado; nunca {@code null}
     * @throws ResourceNotFoundException si no existe ningún curso con ese {@code id}
     */
    public Curso findById(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
    }

    /**
     * Crea y persiste un nuevo curso.
     *
     * @param dto datos del curso a crear, opcionalmente con el id de su profesor
     * @return el curso creado, con su {@code id} generado
     * @throws ResourceNotFoundException si se indica un {@code profesorId} que no existe
     */
    public Curso create(CursoDTO dto) {
        Curso curso = new Curso();
        aplicarDatos(curso, dto);
        return cursoRepository.save(curso);
    }

    /**
     * Actualiza los datos de un curso existente.
     *
     * @param id  identificador del curso a actualizar
     * @param dto nuevos datos del curso, opcionalmente con el id de su profesor
     * @return el curso actualizado
     * @throws ResourceNotFoundException si no existe el curso, o si se indica un {@code profesorId} inexistente
     */
    public Curso update(Long id, CursoDTO dto) {
        Curso existente = findById(id);
        aplicarDatos(existente, dto);
        return cursoRepository.save(existente);
    }

    /**
     * Elimina un curso por su identificador.
     *
     * <p>Al eliminarse, sus matrículas se borran en cascada (ver {@link Curso}).
     *
     * @param id identificador del curso a eliminar
     * @throws ResourceNotFoundException si no existe ningún curso con ese {@code id}
     */
    public void delete(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso", id);
        }
        cursoRepository.deleteById(id);
    }

    /**
     * Vuelca los datos del DTO sobre la entidad curso, resolviendo el profesor.
     *
     * <p>Lógica compartida por {@link #create(CursoDTO)} y
     * {@link #update(Long, CursoDTO)}. Si el DTO trae un {@code profesorId}, lo
     * resuelve mediante {@link ProfesorService#findById(Long)} y lo asocia; si es
     * {@code null}, desvincula el curso de cualquier profesor.
     *
     * @param curso entidad destino sobre la que se aplican los cambios
     * @param dto   datos de origen
     * @throws ResourceNotFoundException si {@code dto.profesorId} no corresponde a ningún profesor
     */
    private void aplicarDatos(Curso curso, CursoDTO dto) {
        curso.setNombre(dto.getNombre());
        curso.setDescripcion(dto.getDescripcion());
        curso.setCreditos(dto.getCreditos());
        if (dto.getProfesorId() != null) {
            Profesor profesor = profesorService.findById(dto.getProfesorId());
            curso.setProfesor(profesor);
        } else {
            curso.setProfesor(null);
        }
    }
}
