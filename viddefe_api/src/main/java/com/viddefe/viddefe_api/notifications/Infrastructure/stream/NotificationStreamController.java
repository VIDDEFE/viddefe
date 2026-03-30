package com.viddefe.viddefe_api.notifications.Infrastructure.stream;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/stream")
public class NotificationStreamController implements Notificator {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/{clientId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String clientId) {

        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(clientId, emitter);
        log.debug("SSE client connected: {} | Total active connections: {}", clientId, emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(clientId);
            log.debug("SSE client disconnected (completion): {} | Total active: {}", clientId, emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(clientId);
            log.debug("SSE client disconnected (timeout): {} | Total active: {}", clientId, emitters.size());
        });
        emitter.onError(e -> {
            emitters.remove(clientId);
            log.warn("SSE client error: {} | Reason: {} | Total active: {}", clientId, e.getMessage(), emitters.size());
        });

        return emitter;
    }


    public void sendEvent(String clientId,@NonNull Object payload) {
        SseEmitter emitter = emitters.get(clientId);
        log.debug("Attempting to send event to clientId: {} | Payload: {}", clientId, payload);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("message")
                                .data(payload)
                );
                log.debug("Event sent successfully to clientId: {}", clientId);
            } catch (IOException e) {
                emitters.remove(clientId);
                log.warn("Error sending SSE event to clientId: {} | Reason: {}", clientId, e.getMessage());
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
        log.debug("Sending notification to clientId: {} | Message: {}", clientId, message);
        sendEvent(clientId, message);
    }
}
