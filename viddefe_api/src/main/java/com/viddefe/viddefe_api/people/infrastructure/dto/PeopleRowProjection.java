package com.viddefe.viddefe_api.people.infrastructure.dto;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;

import java.time.LocalDate;
import java.util.UUID;

public interface PeopleRowProjection {
    UUID getId();
    String getCc();
    String getFirstName();
    String getLastName();
    String getPhone();
    String getAvatar();
    LocalDate getBirthDate();

    Long getTypePersonId();
    String getTypePersonName();

    Long getStateId();
    String getStateName();

    AttendanceQuality getAttendanceQuality();
}
