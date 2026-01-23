package com.viddefe.viddefe_api.worship_meetings.configuration;

import lombok.Getter;

/**
 * Enumerate the quality of attendance.
 */
@Getter
public enum AttendanceQualityEnum {
    HIGH("Alta Asistencia",70),
    MEDIUM("Media Asistencia",40),
    LOW("Baja Asistencia",10),
    NO_YET("Aun No Asiste",0);

    private final String description;
    private  final int value;

    AttendanceQualityEnum(String description, int value) {
        this.value = value;
        this.description = description;
    }
}
