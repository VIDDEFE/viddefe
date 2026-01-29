package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceQualityReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQualityPeople;
import com.viddefe.viddefe_api.worship_meetings.domain.models.serializable.AttendanceQualityPeopleId;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityPeopleRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.PersonCreatedQualityAttendanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PersonCreatedQualityAttendanceEventListener {
    private final AttendanceQualityReader attendanceQualityReader;
    private final PeopleReader peopleReader;
    private final AttendanceQualityPeopleRepository attendanceQualityPeopleRepository;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handlePersonCreatedQualityAttendanceEvent(PersonCreatedQualityAttendanceEvent event) {
        AttendanceQuality attendanceQuality = attendanceQualityReader.findByAttendanceQualityEnum(AttendanceQualityEnum.NO_YET);
        PeopleModel people = peopleReader.getPeopleById(event.getPersonId());
        AttendanceQualityPeopleId id = new AttendanceQualityPeopleId(people.getId(), event.getChurchId());
        AttendanceQualityPeople attendanceQualityPeople = new AttendanceQualityPeople(
                id,
                attendanceQuality,
                people,
                TopologyEventType.TEMPLE_WORHSIP
        );
        attendanceQualityPeopleRepository.save(attendanceQualityPeople);
    }
}
