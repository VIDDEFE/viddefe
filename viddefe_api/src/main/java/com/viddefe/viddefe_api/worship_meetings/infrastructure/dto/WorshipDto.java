package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
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

    private WorshipMeetingTypes worshipType;
}
