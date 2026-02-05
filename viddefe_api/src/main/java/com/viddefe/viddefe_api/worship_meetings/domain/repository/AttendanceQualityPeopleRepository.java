package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQualityPeople;
import com.viddefe.viddefe_api.worship_meetings.domain.models.serializable.AttendanceQualityPeopleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AttendanceQualityPeopleRepository extends JpaRepository<AttendanceQualityPeople, AttendanceQualityPeopleId> {
    /**
     * Find AttendanceQualityPeople by peopleId, contextId, and eventType.
     *
     * @param peopleId  the ID of the person
     * @param contextId the context ID (church or group)
     * @return an Optional containing the AttendanceQualityPeople if found, otherwise empty
     */
    @Query("""
    SELECT aqp
    FROM AttendanceQualityPeople aqp
    JOIN FETCH aqp.people
    JOIN FETCH aqp.attendanceQuality
    WHERE aqp.id.peopleId = :peopleId
      AND aqp.id.contextId = :contextId
      AND aqp.eventType = :eventType
""")
    Optional<AttendanceQualityPeople> findByPeopleIdAndContextIdAndEventType(
            UUID peopleId, UUID contextId, TopologyEventType eventType);
}
