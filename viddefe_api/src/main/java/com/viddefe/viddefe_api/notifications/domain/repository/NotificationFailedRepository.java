package com.viddefe.viddefe_api.notifications.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.viddefe.viddefe_api.notifications.domain.models.NotificationFailed;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationFailedStatus;

/**
 * Repository for NotificationFailed entity
 * Provides methods for managing failed notifications and retry logic
 */
public interface NotificationFailedRepository extends JpaRepository<NotificationFailed, UUID> {

    /**
     * Find all failed notifications ready for retry
     * A notification is ready for retry if status=PENDING_RETRY and next_retry_at <= now
     */
    @Query("SELECT nf FROM NotificationFailed nf WHERE nf.status = 'PENDING_RETRY' AND nf.nextRetryAt <= CURRENT_TIMESTAMP")
    List<NotificationFailed> findReadyForRetry();

    /**
     * Find failed notifications by status
     */
    List<NotificationFailed> findByStatus(NotificationFailedStatus status);

    /**
     * Find failed notifications for a specific user
     */
    List<NotificationFailed> findByPeopleId(UUID peopleId);

    /**
     * Find exhausted notifications created before a specific date
     * Useful for cleanup of old records
     */
    @Query("SELECT nf FROM NotificationFailed nf WHERE nf.status = 'EXHAUSTED' AND nf.createdAt < :cutoffDate")
    List<NotificationFailed> findExhaustedBefore(@Param("cutoffDate") Instant cutoffDate);

    /**
     * Delete exhausted notifications older than a specific date
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationFailed nf WHERE nf.status = 'EXHAUSTED' AND nf.createdAt < :cutoffDate")
    int deleteExhaustedBefore(@Param("cutoffDate") Instant cutoffDate);

    /**
     * Count pending retry notifications
     */
    @Query("SELECT COUNT(nf) FROM NotificationFailed nf WHERE nf.status = 'PENDING_RETRY'")
    long countReadyForRetry();

}
