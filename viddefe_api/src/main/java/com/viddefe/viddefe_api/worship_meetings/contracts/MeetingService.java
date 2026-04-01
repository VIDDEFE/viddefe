package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de servicio para operaciones CRUD sobre la entidad Meeting.
 *
 * Reglas de timezone:
 * - NO realiza conversiones de zona horaria
 * - Asigna directamente OffsetDateTime sin modificaciones
 * - Los DTOs deben validar que la fecha incluya offset
 */
public interface MeetingService {

    /**
     * Crea una nueva reunión.
     *
     * @param meeting La entidad a persistir (WorshipMeetingModel o GroupMeetings)
     * @return La entidad creada con ID asignado
     */
    Meeting create(Meeting meeting);

    /**
     * Actualiza una reunión existente.
     *
     * @param meeting La entidad con los datos actualizados
     * @return La entidad actualizada
     */
    Meeting update(Meeting meeting);

    /**
     * Obtiene una reunión por ID sin relaciones cargadas.
     *
     * @param id Identificador único de la reunión
     * @return La entidad encontrada
     * @throws jakarta.persistence.EntityNotFoundException si no existe una reunión con el ID dado
     */
    Meeting findById(UUID id);

    /**
     * Obtiene una reunión por ID con relaciones pre-cargadas.
     * Evita N+1 queries al convertir a DTO.
     *
     * @param id Identificador único de la reunión
     * @return Optional con la entidad y sus relaciones, o vacío si no existe
     */
    Optional<Meeting> findByIdWithRelations(UUID id);

    /**
     * Obtiene reuniones de adoración (worship) por churchId con paginación.
     * Solo retorna reuniones que no pertenecen a ningún grupo.
     *
     * @param churchId Identificador de la iglesia
     * @param pageable Configuración de paginación y ordenamiento
     * @return Página de reuniones de adoración
     */
    Page<Meeting> findWorshipMeetingByChurchId(UUID churchId, Pageable pageable);

    /**
     * Obtiene reuniones de grupo por groupId con paginación.
     *
     * @param groupId  Identificador del grupo
     * @param pageable Configuración de paginación y ordenamiento
     * @return Página de reuniones del grupo
     */
    Page<Meeting> findGroupMeetingByGroupId(UUID groupId, Pageable pageable);

    /**
     * Elimina una reunión por su ID.
     *
     * @param id Identificador único de la reunión a eliminar
     */
    void delete(UUID id);
}