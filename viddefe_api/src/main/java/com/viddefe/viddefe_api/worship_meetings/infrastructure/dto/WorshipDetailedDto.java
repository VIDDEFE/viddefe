package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class WorshipDetailedDto extends WorshipDto{
    private List<AttendanceDto> attendance;
    private Long totalAttendance;
    private Long presentCount;
    private Long absentCount;
    //private List<>
}
