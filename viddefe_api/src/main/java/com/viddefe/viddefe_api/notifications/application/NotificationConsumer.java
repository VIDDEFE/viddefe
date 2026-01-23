package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationMeetingEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.factory.NotificatorFactory;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.common.RabbitQueues;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionReminderSentWriter;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final PeopleReader peopleReader;
    private final AuthMeService authMeService;
    private final NotificatorFactory notificatorFactory;
    private final MinistryFunctionReader ministryFunctionReader;
    private final MinistryFunctionReminderSentWriter ministryFunctionReminderSentWriter;

    @RabbitListener(
            queues = RabbitQueues.MINISTRY_QUEUE,
            concurrency = "1-5"
    )
    public void consumeMinistry(NotificationEvent event) {

        log.info("Starting MINISTRY notification processing");

        printDataEvent(event);

        PeopleResDto person =
                peopleReader.getPeopleById(event.getPersonId()).toDto();

        sendNotification(person.getPhone(), event);

        handleMinistryFunctionReminder(event);

        log.info("MINISTRY notification sent successfully at {}", Instant.now());
    }

    @RabbitListener(
            queues = RabbitQueues.ACCOUNT_QUEUE,
            concurrency = "1-5"
    )
    public void consumeAccount(NotificationEvent event) throws InterruptedException {

        log.info("Starting ACCOUNT notification processing");

        printDataEvent(event);

        String contact =
                authMeService.getContactByPersonId(event.getPersonId());

        sendNotification(contact, event);

        log.info("ACCOUNT notification sent successfully at {}", Instant.now());
    }

    private void sendNotification(String to, NotificationEvent event) {

        Notificator notificator =
                notificatorFactory.get(event.getChannels());

        NotificationDto dto = resolveNotificationDto(to, event);

        notificator.send(dto);
    }

    private void handleMinistryFunctionReminder(NotificationEvent event) {

        MinistryFunction ministryFunction =
                ministryFunctionReader.getByPeopleIdAndMeetingId(
                        event.getPersonId(),
                        event.getMeetingId()
                );

        ministryFunctionReminderSentWriter
                .writeMinistryFunctionReminderSent(ministryFunction);
    }

    private NotificationDto resolveNotificationDto(String to, NotificationEvent event) {

        NotificationDto dto = new NotificationDto();

        dto.setTo(to);
        dto.setTemplate(event.getTemplate());
        dto.setVariables(event.getVariables());
        dto.setChannels(event.getChannels());

        if (event.getChannels() == Channels.EMAIL) {
            dto.setSubject(event.getSubject());
        }

        return dto;
    }

    private void printDataEvent(NotificationEvent event) {

        log.debug("Notification Event Data:");
        log.debug("Person ID: {}", event.getPersonId());
        log.debug("Channels: {}", event.getChannels());
        log.debug("Template: {}", event.getTemplate());
        log.debug("Subject: {}", event.getSubject());
        log.debug("Variables: {}", event.getVariables());
    }
}
