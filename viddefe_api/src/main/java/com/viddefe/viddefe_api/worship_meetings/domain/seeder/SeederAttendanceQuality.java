package com.viddefe.viddefe_api.worship_meetings.domain.seeder;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederAttendanceQuality {
    private final AttendanceQualityRepository attendanceQualityRepository;

    @PostConstruct
    public void seed() {
        List<String> existingTypes = attendanceQualityRepository.findAll()
                .stream()
                .map(AttendanceQuality::getName)
                .toList();

        List<AttendanceQuality> typesToSeed = Arrays.stream(AttendanceQualityEnum.values())
                .filter(attendedEnum -> !existingTypes.contains(attendedEnum.getDescription()))
                .map(gt -> new AttendanceQuality(null, gt.getDescription(), gt))
                .toList();
        attendanceQualityRepository.saveAll(typesToSeed);
    }
}
