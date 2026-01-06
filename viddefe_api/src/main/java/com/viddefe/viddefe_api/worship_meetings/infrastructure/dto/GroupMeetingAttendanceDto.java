package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter @Setter
public class GroupMeetingAttendanceDto {
    private Page<AttendanceDto> attendance;
    private Long totalAttendance;
    private Long presentCount;
    private Long absentCount;
}
