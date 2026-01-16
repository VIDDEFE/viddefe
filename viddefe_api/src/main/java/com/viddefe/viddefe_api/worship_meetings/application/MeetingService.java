package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio unificado para operaciones CRUD sobre la tabla normalizada 'meetings'.
 * Abstrae los detalles del discriminador y proporciona métodos genéricos.
 *
 * Reglas de timezone:
 * - NO realiza conversiones de zona
 * - Asigna directamente OffsetDateTime sin modificaciones
 * - Los DTOs deben validar que la fecha incluya offset
 */
@Service
public class MeetingService {

    private final MeetingRepository repository;

    public MeetingService(MeetingRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea una nueva reunión.
     * @param meeting La entidad a persistir (WorshipMeetingModel o GroupMeetings)
     * @return La entidad creada con ID asignado
     */
    public Meeting create(Meeting meeting) {
        return repository.save(meeting);
    }

    /**
     * Actualiza una reunión existente.
     */
    public Meeting update(Meeting meeting) {
        return repository.save(meeting);
    }

    /**
     * Obtiene una reunión por ID sin relaciones cargadas.
     */
    public Optional<Meeting> findById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Obtiene una reunión por ID con relaciones pre-cargadas.
     * Evita N+1 queries al convertir a DTO.
     */
    public Optional<Meeting> findByIdWithRelations(UUID id) {
        return repository.findWithRelationsById(id);
    }

    /**
     * Obtiene todas las reuniones de un contexto paginadas.
     */
    public Page<Meeting> findByContextId(UUID contextId, Pageable pageable) {
        return repository.findByContextId(contextId, pageable);
    }

    /**
     * Verifica si existe conflicto de reunión (misma iglesia/grupo, tipo y fecha).
     */
    public boolean existsConflict(UUID contextId, Long typeId, OffsetDateTime scheduledDate) {
        return repository.existsByContextIdAndTypeIdAndScheduledDate(contextId, typeId, scheduledDate);
    }

    /**
     * Elimina una reunión.
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}

