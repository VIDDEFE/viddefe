package com.viddefe.viddefe_api.notifications.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotifications;

public interface UserNotificationRepository extends JpaRepository<UserNotifications, UUID> {
    Page<UserNotifications> findByStatus(NotificationStatus status, Pageable pageable);
}
