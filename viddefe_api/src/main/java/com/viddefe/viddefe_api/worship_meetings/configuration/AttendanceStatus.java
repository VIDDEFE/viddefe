package com.viddefe.viddefe_api.worship_meetings.configuration;

import lombok.Getter;

@Getter
public enum AttendanceStatus {

    PRESENT("Asistió"),
    ABSENT("No asistió");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }
}
