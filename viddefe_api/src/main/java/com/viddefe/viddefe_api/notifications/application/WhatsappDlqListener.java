package com.viddefe.viddefe_api.notifications.application;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.ApplicationSendEventDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.FailureWhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.config.MessagesFailuresToClientSSE;
import com.viddefe.viddefe_api.notifications.config.SseFailureType;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.notifications.contracts.NotificationService;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;

import lombok.extern.slf4j.Slf4j;

/**
 * Listener para la Dead Letter Queue (DLQ) de WhatsApp.
 * Registra mensajes que no pudieron ser procesados para análisis posterior.
 */
@Slf4j
@Component
public class WhatsappDlqListener {

    private final NotificationService notificationFailedService;
    private final NotificationEventPublisher eventPublisher;
    private final PeopleService peopleService;
    private final MinistryFunctionReader ministryFunctionReader;

    public WhatsappDlqListener(NotificationService notificationFailedService, NotificationEventPublisher eventPublisher,
         PeopleService peopleService, MinistryFunctionReader ministryFunctionReader) {
        this.notificationFailedService = notificationFailedService;
        this.eventPublisher = eventPublisher;
        this.peopleService = peopleService;
        this.ministryFunctionReader = ministryFunctionReader;
    }

    @RabbitListener(queues = RabbitQueues.WHATSAPP_DLQ)
    public void handleDlqMessage(FailureWhatsappMessageDto failureMessageDto) {

        String correlationId = failureMessageDto.getCorrelationId();
        String failureTime = failureMessageDto.getLastRetryAt() != null ? failureMessageDto.getLastRetryAt().toString() : "Unknown";
        String originalMessage = ResolverMessage.resolveMessage(
            failureMessageDto.getTemplate(),
            failureMessageDto.getVariables()
        );

        log.error("WhatsApp message sent to DLQ - CorrelationId: {}, Reason: {}, Time: {}, Message: {}",
                  correlationId, failureMessageDto, failureTime, originalMessage);
        notificationFailedService.createFailedNotification(failureMessageDto);
        sendErrorNotificationToSse(failureMessageDto);
    }

    private void sendErrorNotificationToSse(FailureWhatsappMessageDto messageDto) {

        String template = resolveTemplateForSse(messageDto.getSseFailureType());

        Map<String, Object> variables = resolveVariablesForSse(messageDto.getSseFailureType(), messageDto);
        ApplicationSendEventDto eventDto = ApplicationSendEventDto.builder()
            .personId(messageDto.getRemitter())
            .channels(Channels.APP)
            .template(template)
            .variables(variables)
            .build();

        eventPublisher.publish(eventDto);
    }

    private String resolveTemplateForSse(SseFailureType failureReason) {
        return switch (failureReason){
            case INVALID_PHONE_NUMBER -> MessagesFailuresToClientSSE.INVALID_PHONE_NUMBER;
            case MINISTRY_REMINDER_FAILURE ->  MessagesFailuresToClientSSE.MINISTRY_REMINDER_FAILURE;
            case WHATSAPP_SERVICE_HEALTH -> MessagesFailuresToClientSSE.WHATSAPP_SERVICE_HEALTH;
            case UNKNOWN_ERROR ->  MessagesFailuresToClientSSE.UNKNOWN_ERROR;
            case ACCOUNT_CREATION_FAILURE ->   MessagesFailuresToClientSSE.ACCOUNT_CREATION_FAILURE;
            default -> "generic_failure_notification";
        };
    }

    protected Map<String, Object> resolveVariablesForSse(SseFailureType failureReason, FailureWhatsappMessageDto messageDto) {
        switch (failureReason) {
            case INVALID_PHONE_NUMBER -> {
                PeopleResDto people = peopleService.getPeopleById(messageDto.getToId());
                PeopleResDto personRemmiter = peopleService.getPeopleById(messageDto.getRemitter());
                return Map.of("personName", people.getFirstName() + " " + people.getLastName(),
                        "remmiterName", personRemmiter.getFirstName());
            }
            case MINISTRY_REMINDER_FAILURE -> {
                PeopleResDto people = peopleService.getPeopleById(messageDto.getToId());
                UUID meetingId = UUID.fromString(messageDto.getOriginalEventId());
                MinistryFunction ministryFunctionDto = ministryFunctionReader.getByPeopleIdAndMeetingId(messageDto.getToId(), meetingId);
                return
                        Map.of("name", people.getFirstName() + " " + people.getLastName(),
                                "ministryFunction", ministryFunctionDto.getMinistryFunctionType().getName());
            }
            case ACCOUNT_CREATION_FAILURE -> {
                PeopleResDto people = peopleService.getPeopleById(messageDto.getRemitter());
                return Map.of("name", people.getFirstName() + " " + people.getLastName());
            }
            default -> {
                PeopleResDto people = peopleService.getPeopleById(messageDto.getRemitter());
                return Map.of("remmiterName", people.getFirstName());
            }
        }
    }
}