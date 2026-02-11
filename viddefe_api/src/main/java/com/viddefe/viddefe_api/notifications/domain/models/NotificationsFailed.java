package com.viddefe.viddefe_api.notifications.domain.models;

import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications_failed")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class NotificationsFailed {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String message;
    @Column(nullable = false)

    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private String to;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
}
