package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final PeopleReader peopleReader;

    @Override
    public AttendanceDto updateAttendance(CreateAttendanceDto dto, AttendanceEventType type) {
        PeopleModel person = peopleReader.getPeopleById(dto.getPeopleId());
        AttendanceModel attendanceModel = attendanceRepository.findByPeopleIdAndEventId
                (dto.getPeopleId(), dto.getEventId())
                .orElseGet(()-> new AttendanceModel(
                        null,
                        person,
                        dto.getEventId(),
                        type,
                        null
                ));
        AttendanceStatus status = attendanceModel.getId() == null ?
                determineInverseStatus(attendanceModel.getStatus()) :
                AttendanceStatus.PRESENT;

        attendanceModel.setStatus(status);

        return attendanceRepository.save(attendanceModel).toDto();
    }

    private AttendanceStatus determineInverseStatus(AttendanceStatus type) {
        return switch (type) {
            case PRESENT -> AttendanceStatus.ABSENT;
            case ABSENT -> AttendanceStatus.PRESENT;
        };
    }
}
