package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.UserInfo;
import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.factory.NotificatorFactory;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.RabbitQueues;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final PeopleReader peopleReader;
    private final NotificatorFactory notificatorFactory;
    private final AuthMeService authMeService;
    @RabbitListener(
        queues = RabbitQueues.NOTIFICATIONS_QUEUE,
            concurrency = "1-5" // Ajusta según la capacidad del sistema, permite entre 1 y 5 consumidores concurrentes
    )
    public void consume(NotificationEvent event) {
        Notificator notificator = notificatorFactory.get(event.getChannels());
        PeopleResDto person = peopleReader.getPeopleById(event.getPersonId()).toDto();
        UserInfo user = authMeService.getUserInfo(person.id());
        NotificationDto dto = resolveNotificationDto(user.user() ,event);
        notificator.send(dto);
    }

    private NotificationDto resolveNotificationDto(String to ,NotificationEvent event){
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
}