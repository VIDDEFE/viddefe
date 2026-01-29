package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceModel;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.PeopleAttendanceEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PeopleReader peopleReader;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MeetingReader meetingReader;

    @Override
    public AttendanceDto updateAttendance(CreateAttendanceDto dto, TopologyEventType type) {
        PeopleModel person = peopleReader.getPeopleById(dto.getPeopleId());
        boolean isNewAttendee;
        Meeting meeting = meetingReader.getById(dto.getEventId());
        UUID contextId = resolveContextId(meeting, type);
        isNewAttendee = countAttendanceByPeopleId(person.getId(),contextId , type) == 0;
        AttendanceModel attendanceModel = attendanceRepository.findByPeopleIdAndEventId
                (dto.getPeopleId(), dto.getEventId())
                .orElseGet(()-> new AttendanceModel(
                        null,
                        person,
                        meeting,
                        type,
                        null,
                        isNewAttendee
                ));
        AttendanceStatus status = attendanceModel.getId() != null ?
                AttendanceStatus.ABSENT :
                AttendanceStatus.PRESENT;

        attendanceModel.setStatus(status);
        PeopleAttendanceEventDto peopleAttendanceEventDto = PeopleAttendanceEventDto.builder()
                .contextId(contextId)
                .meetingId(meeting.getId())
                .eventType(type)
                .build();
        if(attendanceModel.getId() != null){
            attendanceRepository.deleteById(attendanceModel.getId());
            applicationEventPublisher.publishEvent(peopleAttendanceEventDto);
            return attendanceModel.toDto();
        }
         AttendanceModel saved = attendanceRepository.save(attendanceModel);
        applicationEventPublisher.publishEvent(peopleAttendanceEventDto);
        return saved.toDto();
    }

    @Override
    public Page<AttendanceDto> getAttendanceByEventIdAndContextId(UUID eventId, Pageable pageable, TopologyEventType type, UUID contextId, AttendanceQualityEnum levelOfAttendance) {
        return resolveAttendancePage(
                eventId,
                type,
                contextId,
                levelOfAttendance,
                pageable
        );

    }

    @Override
    public long countTotalByEventId(UUID eventId, TopologyEventType eventType) {
        return attendanceRepository.countTotalByEventId(eventId);
    }

    @Override
    public long countByEventIdWithDefaults(UUID eventId, TopologyEventType eventType, AttendanceStatus status) {
        return attendanceRepository.countByEventIdWithDefaults(eventId, eventType, status);
    }

    /**
     * Counts the total attendance records for a given person in a specific context and event type.
     *
     * @param peopleId  The UUID of the person whose attendance is to be counted.
     * @param eventType The type of event (e.g., TEMPLE_WORHSIP, GROUP_MEETING).
     * @return The total number of attendance records for the specified person.
     */
    private Long countAttendanceByPeopleId(UUID peopleId,UUID contextId, TopologyEventType eventType) {

        return switch (eventType) {
            case TEMPLE_WORHSIP -> attendanceRepository.countTotalWorshipAttendancesByPeopleIdAndContextIdAndEventType(
                    peopleId,
                    contextId,
                    eventType
            );
            case GROUP_MEETING -> attendanceRepository.countTotalGroupsAttendancesByPeopleIdAndContextIdAndEventType(
                    peopleId,
                    contextId,
                    eventType
            );
            default -> throw new IllegalArgumentException("Unsupported TopologyEventType: " + eventType);
        };
    }

    private UUID resolveContextId(Meeting meeting, TopologyEventType type) {
        return switch (type) {
            case TEMPLE_WORHSIP -> meeting.getChurch().getId();
            case GROUP_MEETING -> meeting.getGroup().getId();
            default -> throw new IllegalArgumentException("Unsupported TopologyEventType: " + type);
        };
    }

    private Page<AttendanceDto> resolveAttendancePage(
            UUID eventId,
            TopologyEventType eventType,
            UUID contextId,
            AttendanceQualityEnum attendanceQuality,
            Pageable pageable
    ) {
        if (eventType == TopologyEventType.TEMPLE_WORHSIP) {
            return attendanceRepository.findAttendanceByEventIdAndChurchId(
                    eventId,
                    eventType,
                    contextId,
                    attendanceQuality,
                    pageable
            ).map(AttendanceProjectionDto::toDto);
        } else if (eventType == TopologyEventType.GROUP_MEETING) {
            return attendanceRepository.findAttendanceByEventIdAndGroupId(
                    eventId,
                    eventType,
                    contextId,
                    attendanceQuality,
                    pageable
            ).map(AttendanceProjectionDto::toDto);
        } else {
            throw new IllegalArgumentException("Unsupported TopologyEventType: " + eventType);
        }
    }

}
