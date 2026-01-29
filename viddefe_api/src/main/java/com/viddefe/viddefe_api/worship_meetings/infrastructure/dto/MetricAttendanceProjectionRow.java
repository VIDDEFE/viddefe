package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import java.util.UUID;

public interface MetricAttendanceProjectionRow {
    UUID getId();
    Long getTotalNewAttendees();
    Long getTotalPeopleAttended();
    Long getTotalMeetings();
}
