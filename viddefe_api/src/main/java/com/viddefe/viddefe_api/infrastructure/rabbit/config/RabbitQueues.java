package com.viddefe.viddefe_api.infrastructure.rabbit.config;


public final class RabbitQueues {

    /* ===============================
     * Notifications
     * =============================== */

    public static final String NOTIFICATIONS_EXCHANGE = "notifications.exchange";

    public static final String ACCOUNT_QUEUE  = "notifications.account.queue";
    public static final String PASSWORD_QUEUE = "notifications.password.queue";
    public static final String MINISTRY_QUEUE = "notifications.ministry.queue";

    /* ===============================
     * WhatsApp Resilient Messaging
     * =============================== */

    public static final String WHATSAPP_EXCHANGE = "whatsapp.exchange";
    public static final String WHATSAPP_DLX = "whatsapp.dlx";

    public static final String WHATSAPP_QUEUE = "whatsapp.queue";
    public static final String WHATSAPP_RETRY_QUEUE = "whatsapp.retry.queue";
    public static final String WHATSAPP_DLQ = "whatsapp.dlq";

    public static final String WHATSAPP_ROUTING_KEY = "whatsapp.send";
    public static final String WHATSAPP_RETRY_ROUTING_KEY = "whatsapp.retry";
    public static final String WHATSAPP_DLQ_ROUTING_KEY = "whatsapp.dlq";

    /* ===============================
     * Attendance qualification
     * =============================== */

    public static final String ATTENDANCE_EXCHANGE = "attendance.exchange";
    public static final String ATTENDANCE_QUALITY_QUEUE = "attendance.quality.queue";

    private RabbitQueues() {}
}
