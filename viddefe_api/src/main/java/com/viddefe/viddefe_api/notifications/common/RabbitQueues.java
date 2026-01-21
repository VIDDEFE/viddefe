package com.viddefe.viddefe_api.notifications.common;


public final class RabbitQueues {

    private RabbitQueues() {
        // prevent instantiation
    }

    public static final String NOTIFICATIONS_EXCHANGE = "notifications.exchange";
    public static final String NOTIFICATIONS_QUEUE    = "notifications.queue";
    public static final String NOTIFICATIONS_ROUTING  = "notifications.send";
}
