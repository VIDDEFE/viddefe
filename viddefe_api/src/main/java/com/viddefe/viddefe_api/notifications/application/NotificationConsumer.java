package com.viddefe.viddefe_api.notifications.application;

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
import java.util.Objects;

import static java.rmi.server.LogStream.log;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final PeopleReader peopleReader;
    private final NotificatorFactory notificatorFactory;
    private final MinistryFunctionReader ministryFunctionReader;
    private final MinistryFunctionReminderSentWriter ministryFunctionReminderSentWriter;

    @RabbitListener(
        queues = RabbitQueues.NOTIFICATIONS_QUEUE,
            concurrency = "1-5" // Ajusta según la capacidad del sistema, permite entre 1 y 5 consumidores concurrentes
    )
    public void consume(NotificationEvent event) {
        log("Iniciando notificacao");
        printDataEvent(event);
        Notificator notificator = notificatorFactory.get(event.getChannels());
        PeopleResDto person = peopleReader.getPeopleById(event.getPersonId()).toDto();
        NotificationDto dto = resolveNotificationDto(person.phone() ,event);
        notificator.send(dto);
        resolveHandleSending(event);
        log("Notificacao enviada com sucesso em: " + Instant.now());

    }

    private void resolveHandleSending(NotificationEvent event){
        if (Objects.requireNonNull(event.getNotificationType()) == NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER) {
            handleMinistryFunctionReminder(event);
        } else {
            log("No additional handling for notification type: " + event.getNotificationType());
        }
    }


    private void handleMinistryFunctionReminder(NotificationEvent event){
        MinistryFunction ministryFunction = ministryFunctionReader.getByPeopleIdAndMeetingId(
                event.getPersonId(),
                event.getMeetingId()
        );
        ministryFunctionReminderSentWriter.writeMinistryFunctionReminderSent(ministryFunction);
    }

    private NotificationDto resolveNotificationDto(String to , NotificationEvent event){
        NotificationDto dto = new NotificationDto();
        if(event.getChannels() == Channels.EMAIL){
            dto.setSubject(event.getSubject());
        }
        dto.setTo(to);
        dto.setTemplate(event.getTemplate());
        dto.setVariables(event.getVariables());
        dto.setChannels(event.getChannels());
        return dto;
    }

    private void printDataEvent(NotificationEvent event) {
        log("Notification Event Data:");
        log("Person ID: " + event.getPersonId());
        log("Channels: " + event.getChannels());
        log("Template: " + event.getTemplate());
        log("Subject: " + event.getSubject());
        log("Variables: " + event.getVariables());
    }
}