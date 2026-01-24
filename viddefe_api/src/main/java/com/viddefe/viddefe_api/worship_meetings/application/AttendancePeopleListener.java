package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQualityPeople;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.serializable.AttendanceQualityPeopleId;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityPeopleRepository;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityRepository;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendancePeopleEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttendancePeopleListener {
    private final PeopleReader peopleReader;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceQualityRepository attendanceQualityRepository;
    private final AttendanceQualityPeopleRepository attendanceQualityPeopleRepository;
    private static final Integer MONTHS_AGO = 3;
    private final MeetingReader meetingReader;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleAttendancePeopleEvent(AttendancePeopleEvent event) {
        PeopleModel people = peopleReader.getPeopleById(event.getPeopleId());
        OffsetDateTime from = event.getToday().minusMonths(MONTHS_AGO);
        Double percentage = attendanceRepository.calculateAttendancePercentage(
                people.getId(),
                event.getEventType(),
                event.getToday().plusDays(1L), // Inclusive today to the attendance calculation
                from
        );
        TopologyEventType eventType = event.getEventType();
        UUID contextId = resolveContextId(event.getMeetingId(), eventType);
        AttendanceQualityEnum quality = determineQuality(percentage);
        AttendanceQuality attendanceQuality = attendanceQualityRepository.findByAttendanceQuality(quality);
        AttendanceQualityPeople attendanceQualityPeople = attendanceQualityPeopleRepository.findByPeopleIdAndContextIdAndEventType(people.getId(), contextId, eventType)
                .orElseGet(()-> new AttendanceQualityPeople(
                        null,
                        attendanceQuality,
                        people,
                        event.getEventType()
                ));
        if(attendanceQualityPeople.getId() != null){
            // Ya existe, solo actualizar si la calidad ha cambiado
            if(attendanceQualityPeople.getAttendanceQuality().getAttendanceQuality() == quality){
                return; // No hay cambio en la calidad, salir del método
            }else {
                attendanceQualityPeopleRepository.deleteById(attendanceQualityPeople.getId());
            }
        }
        AttendanceQualityPeopleId id = new AttendanceQualityPeopleId(attendanceQuality.getId(),people.getId(), contextId);
        attendanceQualityPeople.setId(id);
        attendanceQualityPeople.setAttendanceQuality(attendanceQuality);
        attendanceQualityPeopleRepository.save(attendanceQualityPeople);
    }

    private AttendanceQualityEnum determineQuality(Double percentage){
        System.out.println("Calculated attendance percentage: " + percentage);
        if(percentage >= AttendanceQualityEnum.HIGH.getValue()){
            return AttendanceQualityEnum.HIGH;
        } else if (percentage >= AttendanceQualityEnum.MEDIUM.getValue()) {
            return AttendanceQualityEnum.MEDIUM;
        } else {
            return AttendanceQualityEnum.LOW;
        }
    }

    private UUID resolveContextId(UUID eventId, TopologyEventType type) {
        // Lógica para resolver el contextId basado en el eventId y el tipo de evento
        Meeting homeGroupsModel= meetingReader.getById(eventId);
        if(type == TopologyEventType.GROUP_MEETING){
            return homeGroupsModel.getGroup().getId();
        }
        return homeGroupsModel.getChurch().getId();
    }
}
