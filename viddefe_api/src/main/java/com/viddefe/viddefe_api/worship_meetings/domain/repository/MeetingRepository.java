package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio unificado para la tabla normalizada 'meetings'.
 * Maneja tanto WORSHIP como GROUP_MEETING a través de herencia y discriminador.
 *
 * Metodos genéricos por tipo se pueden llamar con findByContextIdAndMeetingType(),
 * o usar queries específicas si se necesita optimización.
 */
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, UUID> {

    /**
     * Busca todas las reuniones de un contexto (sin filtrar por tipo).
     */
    Page<Meeting> findByContextId(@NotNull UUID contextId, Pageable pageable);

    /**
     * Verifica si existe conflicto de reunión (misma iglesia/grupo, tipo y fecha).
     */
    boolean existsByContextIdAndTypeIdAndScheduledDate(
            @NotNull UUID contextId,
            @NotNull Long typeId,
            @NotNull OffsetDateTime scheduledDate
    );

    /**
     * Obtiene reunión con sus relaciones cargadas (evita N+1).
     */
    @EntityGraph(attributePaths = {
            "worshipType",
            "church",
            "groupMeetingType",
            "group"
    })
    Optional<Meeting> findWithRelationsById(UUID id);

}

