package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSendEventDto extends NotificationEvent{
    private UUID peopleId;
    /**
     * @return
     */
    @Override
    public NotificationTypeEnum getNotificationType() {
       return NotificationTypeEnum.NOTIFICATION_APP_SSE;
    }
}
