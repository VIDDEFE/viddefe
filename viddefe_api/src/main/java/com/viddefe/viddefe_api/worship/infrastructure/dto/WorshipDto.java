package com.viddefe.viddefe_api.worship.infrastructure.dto;

import com.viddefe.viddefe_api.worship.domain.models.WorshipMeetingTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter @Setter
public class WorshipDto {
    private UUID id;
    private String name;
    private String description;
    private Date creationDate;
    private LocalDateTime scheduledDate;

    // si vas a exponer relaciones, hacelo por ID
    private WorshipMeetingTypes worshipType;
}
