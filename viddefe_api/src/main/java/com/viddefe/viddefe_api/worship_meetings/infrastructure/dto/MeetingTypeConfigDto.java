package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO para la tabla de configuración MeetingTypeConfig.
 * Mapea tipos de reuniones generales a subtypes específicos.
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MeetingTypeConfigDto {
    private UUID id;
    private MeetingTypeEnum meetingTypeEnum;
    private Long subtypeId;
    private String name;
    private String description;
}

