package com.viddefe.viddefe_api.notifications.Infrastructure.stream;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/stream")
public class NotificationStreamController implements Notificator {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String clientId) {

        SseEmitter emitter = new SseEmitter(0L); // no timeout

        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));
        emitter.onError(e -> emitters.remove(clientId));

        return emitter;
    }


    public void sendEvent(String clientId, Object payload) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("message")
                                .data(payload)
                );
            } catch (IOException e) {
                emitters.remove(clientId);
            }
        }
    }

    /**
     * @return
     */
    @Override
    public Channels channel() {
        return Channels.APP;
    }

    /**
     * @param notificationDto The notification details
     */
    @Override
    public void send(NotificationDto notificationDto) {
        String clientId = notificationDto.getTo();
        String message = ResolverMessage.resolveMessage(notificationDto.getTemplate(), notificationDto.getVariables());
        sendEvent(clientId, message);
    }
}
