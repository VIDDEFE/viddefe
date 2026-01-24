package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceQualityReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceQualityReaderImpl implements AttendanceQualityReader {
    private final AttendanceQualityRepository attendanceQualityRepository;

    @Override
    public List<AttendanceQuality> getAllAttendanceQualities() {
        return attendanceQualityRepository.findAll();
    }

    @Override
    public AttendanceQuality findByAttendanceQualityEnum(AttendanceQualityEnum attendanceQualityEnum) {
        return attendanceQualityRepository.findByAttendanceQuality(attendanceQualityEnum);
    }
}
