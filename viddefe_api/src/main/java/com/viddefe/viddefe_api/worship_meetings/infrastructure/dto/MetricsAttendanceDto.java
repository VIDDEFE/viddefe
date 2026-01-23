package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for Metrics Attendance
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MetricsAttendanceDto {
    private Long newAttendees;
    private Double retentionRate,totalAbsenteesRate, totalAbsentees;
}
