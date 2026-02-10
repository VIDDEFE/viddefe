package com.viddefe.viddefe_api.infrastructure.rabbit.config;

public enum RabbitPriority {
    HIGH(9),
    MEDIUM(5),
    LOW(2);

    private final int value;

    RabbitPriority(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
