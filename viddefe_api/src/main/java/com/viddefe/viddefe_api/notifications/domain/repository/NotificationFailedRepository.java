package com.viddefe.viddefe_api.notifications.domain.repository;

import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationsFailed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationFailedRepository extends JpaRepository<NotificationsFailed, UUID> {
    Page<NotificationsFailed> findByStatus(NotificationStatus status, Pageable pageable);
}
