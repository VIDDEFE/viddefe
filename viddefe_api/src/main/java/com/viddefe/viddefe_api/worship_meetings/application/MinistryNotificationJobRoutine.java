package com.viddefe.viddefe_api.worship_meetings.application;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitPriority;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationMeetingEvent;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MinistryNotificationJobRoutine {
    private static final int BATCH_SIZE = 100;
    private final MinistryFunctionRepository ministryFunctionRepository;
    private static final ForkJoinPool PUBLISH_POOL = new ForkJoinPool(10);
    private static final Integer DAYS_BEFORE_MEETING = 1;
    private static final Integer HOURS_BEFORE_MEETING = 5; // 5 hours before meeting
    private static final String TEMPLATE_GROUP_MEETING = """
        Hola {{name}} 👋
        Te recordamos que tienes una función ministerial asignada para la próxima reunión de grupo {{groupName}} en la iglesia {{churchName}}.
        📌 Evento: {{eventName}}
        🙌 Rol: {{role}}
        🗓 Fecha: {{date}}
        Gracias por tu compromiso y servicio 💙
        
        """;
    private static final String TEMPLATE_WORSHIP_MEETING_REMINDER = """
        Hola {{name}} 👋

        Te recordamos que tienes una función ministerial asignada para la próxima reunión de adoración en la iglesia {{churchName}}.

        📌 Evento: {{eventName}}
        🙌 Rol: {{role}}
        🗓 Fecha: {{date}}

        Gracias por tu compromiso y servicio 💙
        """;
    private final NotificationEventPublisher notificationEventPublisher;

    @Scheduled(fixedRate = 6000 * 20 ) // Ejecuta cada hora 6000 ms * 20 = 20 minutes. 6000 ms = 1 minuto
    @Async
    public void execute() {
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<MinistryFunction> page;

        ZonedDateTime now =
                ZonedDateTime.now(ZoneId.of("America/Bogota"));

        OffsetDateTime beforeDateTime =
                now.minusMonths(3).toOffsetDateTime();
        do {
            page = ministryFunctionRepository
                    .findUpcomingMinistryFunctions(now.toOffsetDateTime(),beforeDateTime,pageable);
            if (page.isEmpty()) {
                return;
            }

            processBatch(page.getContent());

            pageable = page.nextPageable();

        } while (page.hasNext());
    }

    private boolean isReminderDue(MinistryFunction function) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime scheduledDate = function.getMeeting().getScheduledDate();

        // 0. Evento ya pasó → nunca enviar
        if (scheduledDate.isBefore(now)) {
            return false;
        }

        // 1. Ventana de envío
        OffsetDateTime windowStart = scheduledDate.minusDays(DAYS_BEFORE_MEETING);
        OffsetDateTime windowEnd   = scheduledDate.minusHours(HOURS_BEFORE_MEETING);

        if (now.isBefore(windowStart) || now.isAfter(windowEnd)) {
            return false;
        }

        // 2. No enviar más de una vez el mismo día
        Instant reminderSentAt = function.getReminderSentAt();

        if (reminderSentAt != null) {
            LocalDate lastSentDay =
                    reminderSentAt.atOffset(ZoneOffset.UTC).toLocalDate();

            LocalDate today =
                    now.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();

            return !lastSentDay.equals(today);
        }
        return true;
    }


    private void processBatch(List<MinistryFunction> batch) {
        PUBLISH_POOL.submit(() ->
                batch.parallelStream()
                        .filter(this::isReminderDue)
                        .map(this::buildNotificationEvent)
                        .forEach(notificationEventPublisher::publish)
        ).join();
    }

    private String resolveTemplate(MinistryFunction function) {
        return switch (function.getEventType()) {
            case GROUP_MEETING -> TEMPLATE_GROUP_MEETING;
            case TEMPLE_WORHSIP -> TEMPLATE_WORSHIP_MEETING_REMINDER;
            default -> throw new IllegalArgumentException("Tipo de evento no soportado: " + function.getEventType());
        };
    }

    private Map<String, Object> resolveVariables(MinistryFunction function) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", function.getPeople().getFirstName());
        variables.put("eventName", function.getMeeting().getName());
        variables.put("role", function.getMinistryFunctionType().getName());
        variables.put("date", function.getMeeting().getScheduledDate().toString());
        if(function.getEventType() == TopologyEventType.GROUP_MEETING) {
            variables.put("groupName", function.getMeeting().getGroup().getName());
        }
        variables.put("churchName", function.getMeeting().getChurch().getName());
        return variables;
    }

    private NotificationMeetingEvent buildNotificationEvent(
            MinistryFunction function
    ) {
        return NotificationMeetingEvent.builder()
                .createdAt(Instant.now())
                .meetingId(function.getMeeting().getId())
                .channels(Channels.WHATSAPP)
                .priority(RabbitPriority.LOW)
                .personId(function.getPeople().getId())
                .template(resolveTemplate(function))
                .variables(resolveVariables(function))
                .build();
    }

    /**
     * Create a general notification for many different people involve in the meeting.
     *  for example, all the people with a ministry function assigned to the meeting, or all the people that are part of the group of the meeting, etc. 
     * This notification is not for a specific person, but for many people involved in the meeting, so it does not have a personId, 
     * but it has a list of variables that can be used to personalize the notification for each person.
     * @param function
     * @return
     */
    private NotificationMeetingEvent buildGeneralNotificationEvent(
            MinistryFunction function
    ) {
        return NotificationMeetingEvent.builder()
                .createdAt(Instant.now())
                .meetingId(function.getMeeting().getId())
                .channels(Channels.APP)
                .priority(RabbitPriority.LOW)
                .personId(function.getPeople().getId())
                .template("Tienes una función ministerial asignada para la próxima reunión de adoración en la iglesia {{churchName}}. Evento: {{eventName}}, Rol: {{role}}, Fecha: {{date}}")
                .variables(resolveVariables(function))
                .build();
    }
}
