package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceModel, UUID> {
    Optional<AttendanceModel> findByPeopleIdAndEventId(UUID peopleId, UUID eventId);

    Page<AttendanceModel> findByEventId(UUID eventId, Pageable pageable);

    long countByEventIdAndStatus(UUID eventId, AttendanceStatus status);

    long countByEventId(UUID id);

    @Query("""
    SELECT new com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto(
        p,
        COALESCE(a.status, com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus.ABSENT)
    )
    FROM PeopleModel p
    LEFT JOIN AttendanceModel a
        ON a.people = p
       AND a.eventId = :eventId
       AND a.eventType = :eventType
    """)
    Page<AttendanceProjectionDto> findAttendanceByEventWithDefaults(
            @Param("eventId") UUID eventId,
            @Param("eventType") AttendanceEventType eventType,
            Pageable pageable
    );

    @Query("""
    SELECT COUNT(p)
    FROM PeopleModel p
    LEFT JOIN AttendanceModel a
        ON a.people = p
       AND a.eventId = :eventId
       AND a.eventType = :eventType
    WHERE COALESCE(
        a.status,
        com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus.ABSENT
    ) = :status
    """)
    long countByEventIdWithDefaults(
            @Param("eventId") UUID eventId,
            @Param("eventType") AttendanceEventType eventType,
            @Param("status") AttendanceStatus status
    );

    long countTotalByEventId(UUID eventId);
}
