package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GroupMeetingDetailedDto extends GroupMeetingDto{
    private Long totalAttendance;
    private Long presentCount;
    private Long absentCount;
    public GroupMeetingDetailedDto fromDto(GroupMeetingDto dto){
        this.setId(dto.getId());
        this.setType(dto.getType());
        this.setName(dto.getName());
        this.setDescription(dto.getDescription());
        this.setDate(dto.getDate());
        return this;
    }
}
