package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import java.util.List;

import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class ApplicationSendEventDto extends NotificationEvent{
    /**
     * @return
     */
    @Override
    public NotificationTypeEnum getNotificationType() {
       return NotificationTypeEnum.NOTIFICATION_APP_SSE;
    }
}
