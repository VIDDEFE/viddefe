package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter
public class WorshipDetailedDto extends WorshipDto{
    private Long totalAttendance;
    private Long presentCount;
    private Long absentCount;
    private OffsetDateTime date;

    public WorshipDetailedDto fromWorshipDto(WorshipDto worshipDto) {
        this.setId(worshipDto.getId());
        this.setName(worshipDto.getName());
        this.setDescription(worshipDto.getDescription());
        this.setCreationDate(worshipDto.getCreationDate());
        this.setScheduledDate(worshipDto.getScheduledDate());
        this.setWorshipType(worshipDto.getWorshipType());
        return this;
    }
    //private List<>
}
