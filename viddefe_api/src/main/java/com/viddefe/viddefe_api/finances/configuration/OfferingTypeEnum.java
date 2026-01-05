package com.viddefe.viddefe_api.finances.configuration;

import lombok.Getter;

@Getter
public enum OfferingTypeEnum {

    OFFERING("Ofrenda general"),
    TITHING("Diezmo"),
    MISSIONS("Misiones"),
    CONSTRUCTION("Construcción"),
    MAINTENANCE("Mantenimiento"),
    SOCIAL_HELP("Ayuda social"),
    PASTOR_SUPPORT("Sostenimiento pastoral"),
    EVENT_SUPPORT("Apoyo para eventos"),
    DONATION("Donación");

    private final String description;

    OfferingTypeEnum(String description) {
        this.description = description;
    }
}
