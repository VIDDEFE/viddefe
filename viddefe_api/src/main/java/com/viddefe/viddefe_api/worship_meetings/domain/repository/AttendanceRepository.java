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
    @Query("""
    SELECT 
        a
    FROM PeopleModel p
    JOIN AttendanceModel a 
        ON a.people = p 
       AND a.eventId.id = :eventId 
    WHERE p.id = :peopleId
    """
    )
    Optional<AttendanceModel> findByPeopleIdAndEventId(UUID peopleId, UUID eventId);

    @Query("""
    SELECT new com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto(
        p,
        COALESCE(a.status, com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus.ABSENT)
    )
    FROM PeopleModel p
    JOIN Meeting m ON m.id = :eventId AND m.church.id = :churchId
    LEFT JOIN AttendanceModel a
        ON a.people = p
       AND a.eventId.id = :eventId
       AND a.eventType = :eventType
    LEFT JOIN AttendanceQualityPeople aqp ON aqp.people.id = p.id
    LEFT JOIN AttendanceQuality at ON at.id = aqp.attendanceQuality.id
    WHERE p.church.id = m.church.id AND 
        (
            :attendanceQuality IS NULL OR at.attendanceQuality = :attendanceQuality
        )
    ORDER BY p.lastName, p.firstName
    """)
    Page<AttendanceProjectionDto> findAttendanceByEventIdAndChurchId(
            @Param("eventId") UUID eventId,
            @Param("eventType") TopologyEventType eventType,
            @Param("churchId") UUID churchId,
            @Param("attendanceQuality") AttendanceQualityEnum attendanceQuality,
            Pageable pageable
    );

    @Query("""
    SELECT new com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto(
        p,
        COALESCE(a.status, com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus.ABSENT)
    )
    FROM PeopleModel p
    JOIN Meeting m ON m.id = :eventId AND m.group.id = :groupId
    JOIN HomeGroupsPeopleMembers hp ON hp.people.id = p.id AND hp.homeGroup.id = m.group.id
    LEFT JOIN AttendanceModel a
        ON a.people = p
       AND a.eventId.id = :eventId
       AND a.eventType = :eventType
    LEFT JOIN AttendanceQualityPeople aqp ON aqp.people.id = p.id
    LEFT JOIN AttendanceQuality at ON at.id = aqp.attendanceQuality.id
    WHERE p.church.id = m.church.id AND (:attendanceQuality IS NULL OR at.attendanceQuality = :attendanceQuality)
    ORDER BY p.lastName, p.firstName
    """)
    Page<AttendanceProjectionDto> findAttendanceByEventIdAndGroupId(
            @Param("eventId") UUID eventId,
            @Param("eventType") TopologyEventType eventType,
            @Param("groupId") UUID groupId,
            @Param("attendanceQuality") AttendanceQualityEnum attendanceQuality,
            Pageable pageable
    );

    @Query("""
    SELECT COUNT(p)
    FROM PeopleModel p
    LEFT JOIN AttendanceModel a
        ON a.people = p
       AND a.eventId.id = :eventId
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

    @Query("""
    SELECT
        COUNT(a)
    FROM AttendanceModel a
    JOIN Meeting m ON m.id = a.eventId.id
    JOIN ChurchModel cm ON cm.id = :contextId
    WHERE a.people.id = :peopleId
      AND a.eventType = :eventType
    """)
    long countTotalWorshipAttendancesByPeopleIdAndContextIdAndEventType(
            @Param("peopleId") UUID peopleId,
            @Param("contextId") UUID contextId,
            @Param("eventType") TopologyEventType eventType
    );

    @Query("""
    SELECT
        COUNT(a)
    FROM AttendanceModel a
    JOIN Meeting m ON m.id = a.eventId.id
    JOIN HomeGroupsModel hg ON hg.id = m.group.id
    WHERE a.people.id = :peopleId
      AND a.eventType = :eventType AND hg.id = :contextId
    """)
    long countTotalGroupsAttendancesByPeopleIdAndContextIdAndEventType(
            @Param("peopleId") UUID peopleId,
            @Param("contextId") UUID contextId,
            @Param("eventType") TopologyEventType eventType
    );

    @Query("""
    SELECT 
        COUNT(a)
    FROM AttendanceModel a
    WHERE a.eventId.id = :eventId
""")
    long countTotalByEventId(UUID eventId);


    @Query("""
    SELECT
        CASE
            WHEN COUNT(m.id) = 0 THEN 0.0
            ELSE ((COUNT(at.id) * 1.0) / COUNT(m.id)) * 100
        END
    FROM Meeting m
    LEFT JOIN AttendanceModel at
        ON at.eventId.id = m.id
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
