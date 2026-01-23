package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
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
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class AttendancePeopleListener {
    private final PeopleReader peopleReader;
        private final AttendanceRepository attendanceRepository;
    private final AttendanceQualityRepository attendanceQualityRepository;
    private final AttendanceQualityPeopleRepository attendanceQualityPeopleRepository;
    private static final Integer MONTHS_AGO = 3;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleAttendancePeopleEvent(AttendancePeopleEvent event) {
        System.out.println("AttendancePeopleListener");
        PeopleModel people = peopleReader.getPeopleById(event.getPeopleId());
        OffsetDateTime from = event.getToday().minusMonths(MONTHS_AGO);
        Double percentage = attendanceRepository.calculateAttendancePercentage(
                people.getId(),
                event.getEventType(),
                event.getToday(),
                from
        );
        AttendanceQualityEnum quality = determineQuality(percentage);
        AttendanceQuality attendanceQuality = attendanceQualityRepository.findByAttendanceQuality(quality);
        AttendanceQualityPeople attendanceQualityPeople = attendanceQualityPeopleRepository.findByPeopleId(people.getId())
                .orElseGet(()-> new AttendanceQualityPeople(
                        null,
                        attendanceQuality,
                        people,
                        event.getEventType(),
                        event.getContextId()
                ));
        AttendanceQualityPeopleId id = new AttendanceQualityPeopleId(attendanceQuality.getId(),people.getId());
        attendanceQualityPeople.setId(id);
        attendanceQualityPeople.setAttendanceQuality(attendanceQuality);
        attendanceQualityPeopleRepository.save(attendanceQualityPeople);
    }

    private AttendanceQualityEnum determineQuality(Double percentage){
        if(percentage >= AttendanceQualityEnum.HIGH.getValue()){
            return AttendanceQualityEnum.HIGH;
        } else if (percentage >= AttendanceQualityEnum.MEDIUM.getValue()) {
            return AttendanceQualityEnum.MEDIUM;
        } else {
            return AttendanceQualityEnum.LOW;
        }
    }
}
