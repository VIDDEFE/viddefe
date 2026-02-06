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
     * Attendance qualification
     * =============================== */

    public static final String ATTENDANCE_EXCHANGE = "attendance.exchange";
    public static final String ATTENDANCE_QUALITY_QUEUE = "attendance.quality.queue";

    private RabbitQueues() {}
}
