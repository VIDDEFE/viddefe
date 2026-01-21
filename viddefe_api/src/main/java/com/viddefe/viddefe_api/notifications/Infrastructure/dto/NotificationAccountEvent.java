package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
public class NotificationAccountEvent extends NotificationEvent{
    private UUID peopleId;
    private UUID accountId;
    private Instant createdAt;

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.ACCOUNT_CREATED;
    }
}
