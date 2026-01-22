package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class NotificationMeetingEvent extends  NotificationEvent {
    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER;
    }
}
