package com.viddefe.viddefe_api.worship.configuration;

public enum TempleMeetingTypes {

    // Main services
    SUNDAY_SERVICE("Culto Dominical"),
    PRAYER_SERVICE("Culto de Oración"),
    PRAISE_SERVICE("Culto de Alabanza"),
    FAMILY_SERVICE("Culto Familiar"),
    SPECIAL_SERVICE("Culto Especial"),

    // Teaching
    SEMINAR("Seminario"),
    WORKSHOP("Taller"),

    // Youth and children
    YOUTH_SERVICE("Culto Juvenil"),
    CHILDREN_SERVICE("Culto Infantil"),
    CHILDREN_SUNDAY_SCHOOL("Escuela Dominical Infantil"),

    // Prayer and fasting
    PRAYER_JOURNEY("Jornada de Oración"),
    VIGIL("Vigilia"),
    PRAYER_CHAIN("Cadena de Oración"),
    CONGREGATIONAL_FASTING("Ayuno Congregacional"),

    // Special events
    BAPTISM_SERVICE("Bautismos"),
    HOLY_COMMUNION("Santa Cena"),
    CHILD_PRESENTATION("Presentación de Niños"),
    CHURCH_ANNIVERSARY("Aniversario de la Iglesia"),
    CONFERENCE("Congreso");

    private final String label;

    TempleMeetingTypes(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
