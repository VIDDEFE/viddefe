package com.viddefe.viddefe_api.worship_meetings.domain.models;

/**
 * Enum que define los tipos de reuniones en el sistema.
 * Se usa como discriminador en la tabla normalizada 'meetings'.
 *
 * - WORSHIP: Cultos/servicios de adoración
 * - GROUP_MEETING: Reuniones de grupos pequeños/hogares
 */
public enum MeetingTypeEnum {
    WORSHIP("Culto"),
    GROUP_MEETING("Reunión de Grupo");

    private final String displayName;

    MeetingTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

