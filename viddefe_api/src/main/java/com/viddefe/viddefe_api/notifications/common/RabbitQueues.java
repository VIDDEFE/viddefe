package com.viddefe.viddefe_api.notifications.common;


public final class RabbitQueues {

    public static final String NOTIFICATIONS_EXCHANGE = "notifications.exchange";

    public static final String ACCOUNT_QUEUE  = "notifications.account.queue";
    public static final String PASSWORD_QUEUE = "notifications.password.queue";
    public static final String MINISTRY_QUEUE = "notifications.ministry.queue";

    private RabbitQueues() {}
}
