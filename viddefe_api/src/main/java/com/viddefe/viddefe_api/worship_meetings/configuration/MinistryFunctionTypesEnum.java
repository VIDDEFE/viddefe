package com.viddefe.viddefe_api.worship_meetings.configuration;

public enum MinistryFunctionTypesEnum {

    // Leadership & Word
    PREACH("Predicar"),
    TEACH("Enseñar"),
    BIBLE_READING("Lectura Bíblica"),
    EXHORTATION("Exhortación"),

    // Worship & Music
    SING("Cantar"),
    WORSHIP_LEADER("Director de Alabanza"),
    MUSICIAN("Músico / Instrumentista"),
    CHOIR("Coro"),

    // Prayer & Ministry
    PRAY("Orar"),
    INTERCESSION("Intercesión"),
    MINISTRATION("Ministración"),

    // Technical & Media
    SOUND("Sonido"),
    MULTIMEDIA("Multimedia / Proyección"),
    STREAMING("Transmisión / Streaming"),
    LIGHTING("Iluminación"),

    // Logistics & Order
    GREETING("Acogida"),
    USHER("Ujier"),
    LOGISTICS("Logística"),
    SECURITY("Seguridad"),
    ORDER("Orden"),

    // Pastoral Support
    COUNSELING("Consejería"),
    VISITOR_CARE("Atención a Visitantes"),
    HEALING_PRAYER("Oración por Sanidad"),

    // Children & Youth
    CHILDREN_TEACHER("Maestro de Niños"),
    CHILDREN_ASSISTANT("Auxiliar de Niños"),
    YOUTH_TEACHER("Maestro de Jóvenes"),

    // Administration
    OFFERING("Ofrendas"),
    ANNOUNCEMENTS("Anuncios"),
    ATTENDANCE("Registro de Asistencia"),

    // Special Events
    TESTIMONY("Testimonio"),
    SPECIAL_PRESENTATION("Presentación Especial"),
    DRAMA("Drama / Representación"),
    SPECIAL_EVENT("Evento Especial"),
    // Alabanza & Música (extendido)
    BACKUP_SINGER("Corista de Apoyo"),
    INSTRUMENT_TECH("Técnico de Instrumentos"),
    MUSIC_COORDINATOR("Coordinador de Música"),

    // Oración & ministración (extendido)
    ALTAR_CALL("Llamado al Altar"),
    SPIRITUAL_SUPPORT("Acompañamiento Espiritual"),
    FASTING_PRAYER("Oración y Ayuno"),

    // Técnica & medios (extendido)
    CAMERA_OPERATOR("Operador de Cámara"),
    VIDEO_PRODUCTION("Producción de Video"),
    AUDIO_RECORDING("Grabación de Audio"),
    SOCIAL_MEDIA("Redes Sociales"),
    CONTENT_MANAGER("Gestión de Contenidos"),

    // Logística & orden (extendido)
    PARKING("Parqueadero"),
    TRANSPORT("Transporte"),
    CROWD_CONTROL("Control de Asistencia"),
    ACCESS_CONTROL("Control de Acceso"),
    CLEANING("Limpieza"),

    // Atención pastoral (extendido)
    FOLLOW_UP("Seguimiento Pastoral"),
    VISITATION("Visitas Pastorales"),
    HOSPITAL_VISIT("Visita Hospitalaria"),
    NEW_BELIEVERS("Atención a Nuevos Creyentes"),

    // Niños & familias (extendido)
    CHILDCARE("Cuidado Infantil"),
    PARENT_SUPPORT("Apoyo a Padres"),
    FAMILY_SUPPORT("Apoyo Familiar"),

    // Administración & organización (extendido)
    COORDINATION("Coordinación General"),
    SCHEDULING("Programación"),
    INVENTORY("Inventario"),
    FINANCE_SUPPORT("Apoyo Financiero"),
    DONATION_MANAGEMENT("Gestión de Donaciones"),

    // Eventos & producción (extendido)
    EVENT_COORDINATOR("Coordinador de Evento"),
    STAGE_MANAGER("Encargado de Escenario"),
    DECORATION("Decoración"),
    PROTOCOL("Protocolo"),
    GUEST_SUPPORT("Atención a Invitados");

    private final String label;

    MinistryFunctionTypesEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
