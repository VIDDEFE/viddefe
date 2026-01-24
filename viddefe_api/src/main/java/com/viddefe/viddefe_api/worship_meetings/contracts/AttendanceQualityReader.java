package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;

import java.util.List;

public interface AttendanceQualityReader {
    List<AttendanceQuality> getAllAttendanceQualities();
    AttendanceQuality findByAttendanceQualityEnum(AttendanceQualityEnum attendanceQualityEnum);
}
