package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.config.rabbit.RabbitQueues;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceQualityReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQualityPeople;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.serializable.AttendanceQualityPeopleId;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityPeopleRepository;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceQualityRecalcDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceQualifyConsumer {

    private final AttendanceQualityPeopleRepository attendanceQualityPeopleRepository;
    private final PeopleReader peopleReader;
    private final AttendanceQualityReader attendanceQualityReader;
    private final AttendanceRepository attendanceRepository;

    @Async
    @RabbitListener(
            queues = RabbitQueues.ATTENDANCE_QUALITY_QUEUE,
            concurrency = "1-5"
    )
    public void consumeAttendanceQualifyEvent(AttendanceQualityRecalcDto event) {

        log.info(
                "Consuming AttendanceQualifyEvent | peopleId={} contextId={} eventType={} from={} to={}",
                event.getPeopleId(),
                event.getContextId(),
                event.getEventType(),
                event.getFrom(),
                event.getTo()
        );

        PeopleModel people = peopleReader.getPeopleById(event.getPeopleId());

        Double percentage = attendanceRepository.calculateAttendancePercentage(
                people.getId(),
                event.getEventType(),
                event.getTo(),
                event.getFrom()
        );

        log.debug(
                "Attendance percentage calculated | peopleId={} eventType={} percentage={}",
                people.getId(),
                event.getEventType(),
                percentage
        );

        AttendanceQualityEnum quality = determineQuality(percentage);
        TopologyEventType eventType = event.getEventType();

        log.debug(
                "Attendance quality determined | peopleId={} percentage={} quality={}",
                people.getId(),
                percentage,
                quality
        );

        AttendanceQuality attendanceQuality =
                attendanceQualityReader.findByAttendanceQualityEnum(quality);

        AttendanceQualityPeople attendanceQualityPeople =
                attendanceQualityPeopleRepository
                        .findByPeopleIdAndContextIdAndEventType(
                                people.getId(),
                                event.getContextId(),
                                eventType
                        )
                        .orElseGet(() -> {
                            AttendanceQualityPeople entity =
                                    new AttendanceQualityPeople();
                            entity.setPeople(people);
                            entity.setEventType(eventType);
                            return entity;
                        });

        // 👇 idempotency guard
        AttendanceQualityEnum currentQuality =
                attendanceQualityPeople.getAttendanceQuality() != null
                        ? attendanceQualityPeople.getAttendanceQuality().getAttendanceQuality()
                        : null;

        if (currentQuality == quality) {
            log.debug(
                    "No attendance quality change | peopleId={} contextId={} eventType={} quality={}",
                    people.getId(),
                    event.getContextId(),
                    eventType,
                    quality
            );
            return;
        }

        log.info(
                "Upserting attendance quality | peopleId={} contextId={} eventType={} from={} to={}",
                people.getId(),
                event.getContextId(),
                eventType,
                currentQuality,
                quality
        );

        AttendanceQualityPeopleId id = new AttendanceQualityPeopleId(
                people.getId(),
                event.getContextId()
        );

        attendanceQualityPeople.setId(id);
        attendanceQualityPeople.setAttendanceQuality(attendanceQuality);

        attendanceQualityPeopleRepository.save(attendanceQualityPeople);

        log.info(
                "Attendance quality saved | peopleId={} contextId={} eventType={} quality={}",
                people.getId(),
                event.getContextId(),
                eventType,
                quality
        );
    }

    private AttendanceQualityEnum determineQuality(Double percentage){
        log.debug("Determining attendance quality | percentage={}", percentage);
        if(percentage >= AttendanceQualityEnum.HIGH.getValue()){
            return AttendanceQualityEnum.HIGH;
        } else if (percentage >= AttendanceQualityEnum.MEDIUM.getValue()) {
            return AttendanceQualityEnum.MEDIUM;
        } else if(percentage >= AttendanceQualityEnum.LOW.getValue() || percentage > AttendanceQualityEnum.NO_YET.getValue()) {
            return AttendanceQualityEnum.LOW;
        }else {
            return AttendanceQualityEnum.NO_YET;
        }
    }
}
