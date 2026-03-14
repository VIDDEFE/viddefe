package com.viddefe.viddefe_api.notifications.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viddefe.viddefe_api.notifications.domain.models.Notification;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.common.Channels;

/**
 * Repository for Notification entity
 * Provides methods to query notifications with various filters
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Find all notifications of a specific type
     */
    List<Notification> findByType(NotificationTypeEnum type);

    /**
     * Find all notifications sent via a specific channel
     */
    List<Notification> findByChannel(Channels channel);

    /**
     * Find all notifications by type and channel
     */
    List<Notification> findByTypeAndChannel(NotificationTypeEnum type, Channels channel);

    /**
     * Find notifications by context ID and context entity type
     * Useful for EVENT type notifications that reference a specific entity
     */
    List<Notification> findByContextIdAndContextEntityType(UUID contextId, String contextEntityType);

    /**
     * Find all notifications created within a date range
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :startDate AND n.createdAt <= :endDate")
    List<Notification> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

}
