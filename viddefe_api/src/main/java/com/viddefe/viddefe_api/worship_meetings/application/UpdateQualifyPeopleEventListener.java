package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.config.rabbit.AttendanceRoutingKey;
import com.viddefe.viddefe_api.config.rabbit.RabbitQueues;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceQualityRecalcDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.PeopleAttendanceEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateQualifyPeopleEventListener {
    private final MeetingReader meetingReader;
    private static final int BATCH_SIZE = 50;
    private final RabbitTemplate rabbitTemplate;
    private static final int MONTHS_BACK = 6;
    private final PeopleReader peopleReader;

    @Async
    @TransactionalEventListener
    public void updateRatingQualifyPeople(PeopleAttendanceEventDto peopleAttendanceEventDto) {
        //OffsetDateTime to = OffsetDateTime.now();
        //OffsetDateTime from = to.minusMonths(MONTHS_BACK);
        //Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        //Page<UUID> peopleIdPage;
        //do {
        //    peopleIdPage = peopleReader.findAllPeopleIdByContextId();
        //    List<UUID> peopleIds = peopleIdPage.getContent();
        //    log.debug("peopleIds size: {}", peopleIds.size());
        //    peopleIds.stream().parallel().forEach(peopleId ->
        //        publishInQueu(peopleId, peopleAttendanceEventDto, from, to)
        //    );
        //}while (peopleIdPage.hasNext());
    }

    private void publishInQueu(UUID peopleId, PeopleAttendanceEventDto peopleAttendanceEventDto,
                               OffsetDateTime from, OffsetDateTime to) {
        UUID contextId = peopleAttendanceEventDto.getContextId();
        TopologyEventType eventType = peopleAttendanceEventDto.getEventType();
        new AttendanceQualityRecalcDto();
        AttendanceQualityRecalcDto attendanceQualityRecalcDto = AttendanceQualityRecalcDto.builder()
                .peopleId(peopleId)
                .contextId(contextId)
                .eventType(eventType)
                .from(from)
                .to(to)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitQueues.ATTENDANCE_EXCHANGE,
                AttendanceRoutingKey.RECALCULATE_ATTENDANCE_QUALITY.routingKey(),
                attendanceQualityRecalcDto
        );

    }
}
