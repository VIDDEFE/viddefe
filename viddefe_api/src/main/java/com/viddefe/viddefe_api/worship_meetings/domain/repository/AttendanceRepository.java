package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceModel, UUID> {
    Optional<AttendanceModel> findByPeopleIdAndEventId(UUID peopleId, UUID eventId);

    @Query("""
    SELECT new com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto(
        p,
        COALESCE(a.status, com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus.ABSENT)
    )
    FROM PeopleModel p
    JOIN Meeting m ON m.id = :eventId AND (m.church.id = :contextId OR m.group.id = :contextId)
    LEFT JOIN AttendanceModel a
        ON a.people = p
       AND a.eventId = :eventId
       AND a.eventType = :eventType
    LEFT JOIN AttendanceQualityPeople aqp ON aqp.people.id = p.id
    LEFT JOIN AttendanceQuality at ON at.id = aqp.attendanceQuality.id
    WHERE p.church.id = m.church.id AND 
        (
            :attendanceQuality IS NULL OR at.attendanceQuality = :attendanceQuality
        )
    ORDER BY p.lastName, p.firstName
    """)
    Page<AttendanceProjectionDto> findAttendanceByEventAndContexIdWithDefaults(
            @Param("eventId") UUID eventId,
            @Param("eventType") TopologyEventType eventType,
            @Param("contextId") UUID contextId,
            @Param("attendanceQuality") AttendanceQualityEnum attendanceQuality,
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
            @Param("eventType") TopologyEventType eventType,
            @Param("status") AttendanceStatus status
    );

    long countTotalByEventId(UUID eventId);


    @Query("""
    SELECT
        CASE
            WHEN COUNT(m.id) = 0 THEN 0.0
            ELSE ((COUNT(at.id) * 1.0) / COUNT(m.id)) * 100
        END
    FROM Meeting m
    LEFT JOIN AttendanceModel at
        ON at.eventId = m.id
       AND at.people.id = :peopleId
       AND at.eventType = :eventType
    WHERE m.scheduledDate BETWEEN :from AND :to
""")
    Double calculateAttendancePercentage(@Param("peopleId") UUID peopleId,
                                         @Param("eventType") TopologyEventType eventType,
                                         @Param("to") OffsetDateTime to,
                                         @Param("from") OffsetDateTime from
                                         );
}
