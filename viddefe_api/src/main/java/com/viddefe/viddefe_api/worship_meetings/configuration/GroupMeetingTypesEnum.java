package com.viddefe.viddefe_api.worship_meetings.configuration;

public enum GroupMeetingTypesEnum {

    REUNION_CORRIENTE("Reunión Corriente"),
    REUNION_DE_NINOS("Reunión de Niños"),
    REUNION_DE_JOVENES("Reunión de Jóvenes"),
    REUNION_DE_ADULTOS("Reunión de Adultos"),
    REUNION_FAMILIAR("Reunión Familiar"),

    REUNION_DE_LIDERES("Reunión de Líderes"),
    REUNION_DE_SERVIDORES("Reunión de Servidores"),

    REUNION_DE_ORACION("Reunión de Oración"),
    REUNION_DE_ESTUDIO("Reunión de Estudio Bíblico"),

    REUNION_ESPECIAL("Reunión Especial"),
    REUNION_CONJUNTA("Reunión Conjunta"),
    REUNION_EXTRAORDINARIA("Reunión Extraordinaria");

    private final String label;

    GroupMeetingTypesEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
