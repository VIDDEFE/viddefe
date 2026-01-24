package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final PeopleReader peopleReader;

    @Override
    public AttendanceDto updateAttendance(CreateAttendanceDto dto, TopologyEventType type) {
        PeopleModel person = peopleReader.getPeopleById(dto.getPeopleId());
        boolean isNewAttendee;
        isNewAttendee = countAttendanceByPeopleId(person.getId(), type) == 0;
        AttendanceModel attendanceModel = attendanceRepository.findByPeopleIdAndEventId
                (dto.getPeopleId(), dto.getEventId())
                .orElseGet(()-> new AttendanceModel(
                        null,
                        person,
                        dto.getEventId(),
                        type,
                        null,
                        isNewAttendee
                ));
        AttendanceStatus status = attendanceModel.getId() != null ?
                AttendanceStatus.ABSENT :
                AttendanceStatus.PRESENT;

        attendanceModel.setStatus(status);
        if(attendanceModel.getId() != null){
            attendanceRepository.deleteById(attendanceModel.getId());
            return attendanceModel.toDto();
        }


        return attendanceRepository.save(attendanceModel).toDto();
    }

    @Override
    public Page<AttendanceDto> getAttendanceByEventIdAndContextId(UUID eventId, Pageable pageable, TopologyEventType type, UUID contextId, AttendanceQualityEnum levelOfAttendance) {

        return attendanceRepository
                .findAttendanceByEventAndContexIdWithDefaults(eventId, type ,contextId,levelOfAttendance,pageable)
                .map(AttendanceProjectionDto::toDto);
    }

    @Override
    public long countTotalByEventId(UUID eventId, TopologyEventType eventType) {
        return attendanceRepository.countTotalByEventId(eventId);
    }

    @Override
    public long countByEventIdWithDefaults(UUID eventId, TopologyEventType eventType, AttendanceStatus status) {
        return attendanceRepository.countByEventIdWithDefaults(eventId, eventType, status);
    }

    private Long countAttendanceByPeopleId(UUID peopleId, TopologyEventType eventType) {
        return attendanceRepository.countByEventIdWithDefaults(
                peopleId,
                eventType,
                AttendanceStatus.PRESENT
        );
    }

}
