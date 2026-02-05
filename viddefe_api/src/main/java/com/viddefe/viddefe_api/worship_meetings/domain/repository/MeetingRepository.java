package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricAttendanceProjectionRow;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
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

    /**
     * Attendance Metrics for Worship Meetings
     * @param churchId
     * @param eventType
     * @param startOfTime
     * @param endOfTime
     * @return MetricsAttendanceDto(
     *       Long newAttendees,
     *       Double retentionRate,
     *       Double totalAbsenteesRate,
     *  )
     */

    @Query("""
        SELECT
            m.church.id AS id,
            CAST(
                COALESCE(SUM(
                    CASE WHEN at.isNewAttendee = true THEN 1 ELSE 0 END
                ), 0)
            AS Long) AS totalNewAttendees,
            COUNT(DISTINCT at.people) AS totalPeopleAttended,
            COUNT(DISTINCT m.id) AS totalMeetings
        FROM Meeting m
        JOIN m.church c ON c.id In :churchId
        JOIN AttendanceModel at
            ON at.eventId.id = m.id
        WHERE m.scheduledDate BETWEEN :startOfTime AND :endOfTime
        GROUP BY m.church.id
    """)
    List<MetricAttendanceProjectionRow> getMetricsWorshipAttendanceByInId(
            @Param("churchId") List<UUID> churchId,
            @Param("eventType") @NotNull TopologyEventType eventType,
            @Param("startOfTime") @NotNull OffsetDateTime startOfTime,
            @Param("endOfTime") @NotNull OffsetDateTime endOfTime
    );

    @Query("""
    SELECT
            m.group.id AS id,
            CAST(
                COALESCE(SUM(
                    CASE WHEN at.isNewAttendee = true THEN 1 ELSE 0 END
                ), 0)
            AS Long) AS totalNewAttendees,
            COUNT(DISTINCT at.people) AS totalPeopleAttended,
            COUNT(DISTINCT m.id) AS totalMeetings
        FROM Meeting m
        JOIN m.group g ON g.id In :groupIds
        JOIN AttendanceModel at
            ON at.eventId.id = m.id
        WHERE m.scheduledDate BETWEEN :startOfTime AND :endOfTime
        GROUP BY m.group.id
""")
    List<MetricAttendanceProjectionRow> getMetricsGroupAttendanceByInId(
            @Param("groupIds") List<UUID> groupIds,
            @Param("eventType") TopologyEventType eventType,
            @Param("startOfTime") OffsetDateTime startOfTime,
            @Param("endOfTime") OffsetDateTime endOfTime
    );

}

