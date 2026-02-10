package com.viddefe.viddefe_api.infrastructure.rabbit.model;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.MenssagingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "messaging_errors"
)
@Getter @Setter
public class MessagingError {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private MenssagingStatus status;

    private String message;
    private String to;
    private String from;
    private OffsetDateTime timestamp;
}
