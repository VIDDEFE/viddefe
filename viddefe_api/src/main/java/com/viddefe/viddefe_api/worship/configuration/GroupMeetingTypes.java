package com.viddefe.viddefe_api.worship.configuration;

public enum GroupMeetingTypes {

    // Family and small groups
    FAMILY_GROUP("Grupo Familiar"),
    CELL_GROUP("Célula"),
    HOME_GROUP("Grupo en Casa"),

    // Age-based groups
    YOUTH_GROUP("Grupo de Jóvenes"),
    CHILDREN_GROUP("Grupo de Niños"),

    // Spiritual growth
    DISCIPLESHIP_GROUP("Discipulado"),
    BIBLE_STUDY_GROUP("Estudio Bíblico"),
    PRAYER_GROUP("Grupo de Oración"),

    // Leadership and service
    LEADERS_GROUP("Grupo de Líderes"),
    SERVANTS_GROUP("Grupo de Servidores"),
    MINISTRY_GROUP("Grupo de Ministerio"),

    // Special group meetings
    TRAINING_GROUP("Grupo de Capacitación"),
    SUPPORT_GROUP("Grupo de Apoyo"),
    SPECIAL_INTEREST_GROUP("Grupo de Interés Especial");

    private final String label;

    GroupMeetingTypes(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
