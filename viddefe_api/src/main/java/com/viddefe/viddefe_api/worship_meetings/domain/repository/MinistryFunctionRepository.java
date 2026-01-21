package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MinistryFunctionRepository extends JpaRepository<MinistryFunction,UUID> {
    List<MinistryFunction> findByEventId(UUID eventId);

    @Query("""
        SELECT mf
        FROM MinistryFunction mf
        JOIN FETCH Meeting m ON mf.event.id = m.id
        WHERE mf.reminderSentAt IS NULL
          AND m.scheduledDate BETWEEN :now AND :limit
    """)
    Page<MinistryFunction> findPendingReminders(
            @Param("now") OffsetDateTime now,
            @Param("limit") OffsetDateTime limit,
            Pageable pageable
    );
}
