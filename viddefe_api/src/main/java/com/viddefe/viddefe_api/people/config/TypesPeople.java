package com.viddefe.viddefe_api.people.config;

/**
 * Represents the different types of people within the church ecosystem.
 * This classification is used for roles, responsibilities and access rules.
 */
public enum TypesPeople {

    // Comunidad general
    SHEEP("Oveja / Miembro"),
    VISITOR("Visitante"),

    // Liderazgo espiritual
    PASTOR("Pastor"),
    ASSOCIATE_PASTOR("Pastor Asociado"),
    YOUTH_PASTOR("Pastor de Jóvenes"),
    CHILDREN_PASTOR("Pastor de Niños"),

    // Ministerio de adoración
    WORSHIP_LEADER("Líder de Alabanza"),
    WORSHIP_MEMBER("Integrante de Alabanza"),
    MUSICIAN("Músico"),
    SINGER("Cantante"),
    CHOIR_MEMBER("Coro"),

    // Servicio y apoyo
    VOLUNTEER("Voluntario"),
    USHER("Ujier"),
    LOGISTICS("Logística"),
    MEDIA("Medios / Multimedia"),
    SOUND_TECH("Sonido"),
    CAMERA_OPERATOR("Cámaras"),

    // Formación
    TEACHER("Maestro"),
    BIBLE_SCHOOL_STUDENT("Estudiante Escuela Bíblica"),

    // Administración
    ADMIN_STAFF("Administrativo"),
    TREASURER("Tesorero"),
    SECRETARY("Secretaría"),

    // Casos especiales
    INTERCESSOR("Intercesor"),
    COUNSELOR("Consejero"),
    DISCIPLESHIP_LEADER("Líder de Discipulado");

    private final String label;

    TypesPeople(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
