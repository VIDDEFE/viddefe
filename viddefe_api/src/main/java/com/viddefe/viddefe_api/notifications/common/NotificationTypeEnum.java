package com.viddefe.viddefe_api.notifications.common;

public enum NotificationTypeEnum {

    ACCOUNT_CREATED("notifications.account.created"),
    PASSWORD_RESET("notifications.password.reset"),
    MINISTRY_FUNCTION_REMINDER("notifications.ministry.reminder");

    private final String routingKey;

    NotificationTypeEnum(String routingKey) {
        this.routingKey = routingKey;
    }

    public String routingKey() {
        return routingKey;
    }
}
