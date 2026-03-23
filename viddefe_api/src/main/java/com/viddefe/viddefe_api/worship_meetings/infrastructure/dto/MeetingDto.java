package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO base de respuesta para meetings/reuniones.
 * Contiene los campos comunes que comparten todos los tipos de meetings.
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class MeetingDto {
    protected UUID id;
    protected String name;
    protected String description;
    protected OffsetDateTime scheduledDate;
    protected Instant creationDate;
    protected MeetingTypeDto type;
    protected UUID contextId; // Puede ser null para reuniones de templo, o contener el ID del grupo para reuniones de grupo
    protected TopologyEventType eventType; // Indica el tipo de evento para determinar la lógica de notificaciones
}
