package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO for Metrics Attendance
 */
@SuperBuilder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MetricsAttendanceDto {
    protected Long newAttendees; //done

    // Baseline
    protected Long totalPeopleAttended; //done
    protected Long totalPeople; //done


    // Rates (derived, not queried)
    protected Double attendanceRate; //done
    protected Double absenceRate; //done

    // Optional but useful
    protected Long totalMeetings; //done
    protected Double averageAttendancePerMeeting; //done
}
