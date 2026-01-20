package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter
public class WorshipDetailedDto extends MeetingDto {
    private Long totalAttendance;
    private Long presentCount;
    private Long absentCount;
    private OffsetDateTime date;

    public WorshipDetailedDto fromWorshipDto(MeetingDto worshipDto) {
        this.setId(worshipDto.getId());
        this.setName(worshipDto.getName());
        this.setDescription(worshipDto.getDescription());
        this.setCreationDate(worshipDto.getCreationDate());
        this.setScheduledDate(worshipDto.getScheduledDate());
        this.setType(worshipDto.getType());
        return this;
    }
    //private List<>
}
