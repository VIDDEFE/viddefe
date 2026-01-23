package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceQualityRepository extends JpaRepository<AttendanceQuality, Long> {
    AttendanceQuality findByAttendanceQuality(AttendanceQualityEnum quality);
}
