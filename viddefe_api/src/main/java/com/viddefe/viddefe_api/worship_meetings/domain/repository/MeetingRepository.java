package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio unificado para la tabla normalizada 'meetings'.
 * Maneja tanto WORSHIP como GROUP_MEETING a través de herencia y discriminador.
 *
 * o usar queries específicas si se necesita optimización.
 */
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, UUID> {

    @EntityGraph(attributePaths = {
            "meetingType"
    })
    Page<Meeting> findByChurchIdAndGroupIsNull(
            UUID churchId,
            Pageable pageable
    );

    /**
     * Meetings a nivel grupo (group IS NOT NULL)
     */
    @EntityGraph(attributePaths = {
            "meetingType"
    })
    Page<Meeting> findByGroupId(
            UUID groupId,
            Pageable pageable);

    /**
     * Obtiene reunión con sus relaciones cargadas (evita N+1).
     */
    @EntityGraph(attributePaths = {
            "church",
            "group"
    })
    Optional<Meeting> findWithRelationsById(UUID id);

    boolean existsByChurchIdAndMeetingTypeIdAndScheduledDate(
            UUID churchId,
            Long meetingTypeId,
            OffsetDateTime scheduledDate
    );

    boolean existsByChurchIdAndGroupIdAndMeetingTypeIdAndScheduledDate(
            UUID churchId,
            UUID groupId,
            Long meetingTypeId,
            OffsetDateTime scheduledDate
    );

    // =========================
    // UPDATE
    // =========================

    boolean existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
            UUID churchId,
            Long meetingTypeId,
            OffsetDateTime scheduledDate,
            UUID id
    );

    boolean existsByChurchIdAndGroupIdAndMeetingTypeIdAndScheduledDateAndIdNot(
            UUID churchId,
            UUID groupId,
            Long meetingTypeId,
            OffsetDateTime scheduledDate,
            UUID id
    );
}

