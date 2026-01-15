package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * DTO de respuesta para reuniones de grupo.
 * Extiende de MeetingDto para heredar campos comunes.
 */
@Getter @Setter
public class GroupMeetingDto extends MeetingDto {
    protected MeetingTypeDto type;

    /**
     * Getter de compatibilidad para 'date'.
     * Mapea al campo scheduledDate heredado de MeetingDto.
     */
    public OffsetDateTime getDate() {
        return getScheduledDate();
    }

    /**
     * Setter de compatibilidad para 'date'.
     * Mapea al campo scheduledDate heredado de MeetingDto.
     */
    public void setDate(OffsetDateTime date) {
        setScheduledDate(date);
    }
}