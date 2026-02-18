package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Specialized event class for account-related notifications, specifically for notifying users about the creation of their accounts.
 * Contains specific fields relevant to account events, such as the person's ID, account ID, and the timestamp of when the event was created.
 * This class extends the base NotificationEvent, allowing it to be processed by the notification system while
 * providing additional context specific to account creation events.
 */
public class NotificationAccountEvent extends NotificationEvent{
    private UUID peopleId;
    private UUID accountId;
    private Instant createdAt;

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.ACCOUNT_CREATED;
    }
}
