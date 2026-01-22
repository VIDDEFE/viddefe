package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MinistryFunctionRepository extends JpaRepository<MinistryFunction,UUID> {
    List<MinistryFunction> findByMeetingId(UUID eventId);


    @EntityGraph(attributePaths = {
            "people",
            "ministryFunctionType",
            "meeting",
            "meeting.group",
            "meeting.church"
    })
    @Query("""
    SELECT mf
    FROM MinistryFunction mf
    JOIN mf.meeting m
    WHERE m.scheduledDate > :now
""")
    Page<MinistryFunction> findUpcomingMinistryFunctions(
            @Param("now") OffsetDateTime now,
            Pageable pageable
    );



    Optional<MinistryFunction> findByMeetingIdAndPeopleId(UUID meetingId, UUID peopleId);
}
