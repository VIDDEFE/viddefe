package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;

import java.util.UUID;

public interface PeopleIdAndQualifyProjection {
    UUID getPeopleId();
    AttendanceQualityEnum getAttendanceQuality();
}
