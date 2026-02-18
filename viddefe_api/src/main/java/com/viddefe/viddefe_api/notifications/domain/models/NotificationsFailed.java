package com.viddefe.viddefe_api.notifications.domain.models;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> variables;
    @Column(nullable = false)

    private Instant createdAt;

    @Column(nullable = false)
    private String to;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Channels channel;

    private String template;

    @Column(nullable = false)
    private UUID peopleId;
}
