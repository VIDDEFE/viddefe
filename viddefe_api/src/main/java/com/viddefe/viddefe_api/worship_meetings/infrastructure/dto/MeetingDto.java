package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO base de respuesta para meetings/reuniones.
 * Contiene los campos comunes que comparten todos los tipos de meetings.
 */
@Getter @Setter
public class MeetingDto {
    protected UUID id;
    protected String name;
    protected String description;
    protected OffsetDateTime scheduledDate;
    protected Instant creationDate;
}
