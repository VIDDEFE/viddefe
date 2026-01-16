package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.factory.NotificatorFactory;
import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MinistryNotificationJobRoutine {
    private static final int BATCH_SIZE = 100;
    private final NotificatorFactory notificatorFactory;
    private final MinistryFunctionRepository ministryFunctionRepository;

    @Scheduled(fixedRate = 60000 * 60) // Ejecuta cada hora 6000 ms * 60 = 1 hora
    public void execute() {

        Instant now = Instant.now();
        Instant limit = now.plus(Duration.ofHours(1));

        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        Page<MinistryFunction> page;

        /*do {
            page = ministryFunctionRepository
                    .findPendingReminders(now, limit, pageable);

            if (page.isEmpty()) {
                return;
            }

            processBatch(page.getContent());

            pageable = page.nextPageable();

        } while (page.hasNext());*/
    }

    private void processBatch(List<MinistryFunction> batch) {

        Notificator notificator =
                notificatorFactory.get(Channels.WHATSAPP);

        for (MinistryFunction function : batch) {
            try {
                sendReminder(function, notificator);
                //function.markReminderAsSent();
            } catch (Exception e) {
                // log y seguir
            }
        }

        ministryFunctionRepository.saveAll(batch);
    }

    private void sendReminder(MinistryFunction function, Notificator notificator) {
        // TODO: Implementar lógica de envío
    }

}
