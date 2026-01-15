package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de respuesta para cultos/servicios de adoración.
 * Extiende de MeetingDto para heredar campos comunes (id, name, description, scheduledDate, creationDate).
 */
@Getter @Setter
public class WorshipDto extends MeetingDto {
    protected MeetingTypeDto worshipType;
}
