package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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
    public Meeting findById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Meeting not found with id: " + id)
        );
    }

    /**
     * Obtiene una reunión por ID con relaciones pre-cargadas.
     * Evita N+1 queries al convertir a DTO.
     */
    public Optional<Meeting> findByIdWithRelations(UUID id) {
        return repository.findWithRelationsById(id);
    }

    /**
     * Obtiene reuniones de adoración (worship) por churchId con paginación.
     */
    public Page<Meeting> findWorshipMeetingByChurchId(UUID churchId, Pageable pageable) {
        return repository.findByChurchIdAndGroupIsNull(churchId, pageable);
    }

    /**
     * Obtiene reuniones de grupo por churchId y groupId con paginación.
     */
    public Page<Meeting> findGroupMeetingByGroupId(UUID groupId, Pageable pageable) {
        return repository.findByGroupId(groupId, pageable);
    }


    /**
     * Elimina una reunión.
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}

