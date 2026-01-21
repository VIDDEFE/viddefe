package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.RabbitPriority;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Component
@RequiredArgsConstructor
public class MinistryNotificationJobRoutine {
    private static final int BATCH_SIZE = 100;
    private final MinistryFunctionRepository ministryFunctionRepository;
    private static final ForkJoinPool PUBLISH_POOL = new ForkJoinPool(10);
    private final String TEMPLATE_GROUP_MEETING = """
        Hola {{name}} 👋
        Te recordamos que tienes una función ministerial asignada para la próxima reunión de grupo {{groupName}} en la iglesia {{churchName}}.
        📌 Evento: {{eventName}}
        🙌 Rol: {{role}}
        🗓 Fecha: {{date}}
        Gracias por tu compromiso y servicio 💙
        
        """;
    private final String TEMPLATE_WORSHIP_MEETING_REMINDER = """
        Hola {{name}} 👋

        Te recordamos que tienes una función ministerial asignada para la próxima reunión de adoración en la iglesia {{churchName}}.

        📌 Evento: {{eventName}}
        🙌 Rol: {{role}}
        🗓 Fecha: {{date}}

        Gracias por tu compromiso y servicio 💙
        """;
    private final NotificationEventPublisher notificationEventPublisher;

    @Scheduled(fixedRate = 10000) // Ejecuta cada hora 6000 ms * 60 = 1 hora. 6000 ms = 1 minuto
    @Async
    public void execute() {
        System.out.println("Inicio de la rutina de notificaciones ministeriales");

        OffsetDateTime nowOffset = OffsetDateTime.now();
        OffsetDateTime limitOffset = nowOffset.plusHours(1);

        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<MinistryFunction> page;

        do {
            page = ministryFunctionRepository
                    .findPendingReminders(nowOffset, limitOffset, pageable);

            if (page.isEmpty()) {
                System.out.println("No hay notificaciones pendientes en este rango de tiempo.");
                return;
            }

            processBatch(page.getContent());

            pageable = page.nextPageable();

        } while (page.hasNext());
    }

    private void processBatch(List<MinistryFunction> batch) {
        PUBLISH_POOL.submit(() ->
                batch.parallelStream()
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
        variables.put("eventName", function.getEvent().getName());
        variables.put("role", function.getMinistryFunctionType().getName());
        variables.put("date", function.getEvent().getScheduledDate().toString());
        if(function.getEventType() == TopologyEventType.GROUP_MEETING) {
            variables.put("groupName", function.getEvent().getGroup().getName());
        }
        variables.put("churchName", function.getEvent().getChurch().getName());
        System.out.println("Variables para la notificación: " + variables);
        return variables;
    }

    private NotificationEvent buildNotificationEvent(
            MinistryFunction function
    ) {
        NotificationEvent event = new NotificationEvent();
        event.setCreatedAt(Instant.now());
        event.setChannels(Channels.WHATSAPP);
        event.setPriority(RabbitPriority.LOW);
        event.setPersonId(function.getPeople().getId());
        event.setTemplate(resolveTemplate(function));
        event.setVariables(resolveVariables(function));
        return event;
    }

}
