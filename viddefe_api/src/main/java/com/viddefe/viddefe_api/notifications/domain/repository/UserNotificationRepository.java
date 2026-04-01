package com.viddefe.viddefe_api.notifications.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;

public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {

    /**
     * Find all unread notifications for a specific user
     */
    @Query("SELECT un FROM UserNotification un WHERE un.userId = :peopleId AND un.readAt IS NULL")
    List<UserNotification> findUnreadByPeopleId(@Param("peopleId") UUID peopleId);

    /**
     * Find notification by its ID and people ID
     */
    Optional<UserNotification> findByNotificationIdAndUserId(UUID notificationId, UUID peopleId);

    /**
     * Count unread notifications for a specific user
     */
    @Query("SELECT COUNT(un) FROM UserNotification un WHERE un.userId = :peopleId AND un.readAt IS NULL")
    long countUnreadByPeopleId(@Param("peopleId") UUID peopleId);

    /**
     * Mark all notifications as read for a specific user
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserNotification un SET un.readAt = CURRENT_TIMESTAMP, un.status = 'READ' WHERE un.userId = :peopleId AND un.readAt IS NULL")
    int markAllAsRead(@Param("peopleId") UUID peopleId);

    /**
     * Find all user notifications by status
     */
    Page<UserNotification> findByStatus(UserNotificationStatus status, Pageable pageable);

    /**
     * Find all user notifications by people ID
     */
    Page<UserNotification> findByUserId(UUID peopleId, Pageable pageable);

    /**
     * Find all user notifications by notification ID
     */
    List<UserNotification> findByNotificationId(UUID notificationId);

    Optional<UserNotification> findByUserId(UUID userId);
}
