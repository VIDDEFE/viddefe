package com.viddefe.viddefe_api.infrastructure.rabbit.config;

public enum AttendanceRoutingKey {

    RECALCULATE_ATTENDANCE_QUALITY("attendance.recalculate");

    private final String routingKey;

    AttendanceRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String routingKey() {
        return routingKey;
    }
}
