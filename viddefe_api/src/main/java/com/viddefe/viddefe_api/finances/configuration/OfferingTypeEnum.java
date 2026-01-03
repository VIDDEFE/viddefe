package com.viddefe.viddefe_api.finances.configuration;

import lombok.Getter;

@Getter
public enum OfferingTypeEnum {

    OFFERING("Ofrenda general"),
    TITHING("Diezmo"),
    THANKSGIVING("Acción de gracias"),
    MISSIONS("Misiones"),
    CONSTRUCTION("Construcción"),
    MAINTENANCE("Mantenimiento"),
    YOUTH_MINISTRY("Ministerio de jóvenes"),
    CHILDREN_MINISTRY("Ministerio de niños"),
    WORSHIP_MINISTRY("Ministerio de alabanza"),
    SOCIAL_HELP("Ayuda social"),
    CHARITY("Caridad"),
    SPECIAL_OFFERING("Ofrenda especial"),
    LOVE_OFFERING("Ofrenda de amor"),
    PASTOR_SUPPORT("Sostenimiento pastoral"),
    EVENT_SUPPORT("Apoyo para eventos"),
    CONFERENCE("Conferencias"),
    RETREAT("Retiros espirituales"),
    EDUCATION("Educación cristiana"),
    MEDIA_MINISTRY("Ministerio de medios"),
    MISSIONS_TRIP("Viajes misioneros"),
    DONATION("Donación"),
    BUILDING_FUND("Fondo de edificación"),
    EMERGENCY_FUND("Fondo de emergencia"),
    ANNIVERSARY("Aniversario"),
    THANK_OFFERING("Ofrenda de gratitud"),
    FAITH_PROMISE("Promesa de fe");

    private final String description;

    OfferingTypeEnum(String description) {
        this.description = description;
    }
}
