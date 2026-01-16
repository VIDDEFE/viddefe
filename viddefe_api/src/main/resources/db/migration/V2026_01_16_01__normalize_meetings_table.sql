-- Migración de normalización de meetings (de-normalización: worship_services + group_meetings → meetings)
-- Tabla base unificada con discriminador

CREATE TABLE IF NOT EXISTS meetings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meeting_type VARCHAR(50) NOT NULL,  -- Discriminador: WORSHIP, GROUP_MEETING
    name VARCHAR(255) NOT NULL,
    description TEXT,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_date TIMESTAMPTZ NOT NULL,
    context_id UUID NOT NULL,  -- church_id (WORSHIP) o home_groups_id (GROUP_MEETING)
    type_id BIGINT NOT NULL,  -- worship_meeting_type_id (WORSHIP) o group_meeting_type_id (GROUP_MEETING)
    worship_meeting_type_id BIGINT,  -- FK solo para WORSHIP
    group_meeting_type_id BIGINT,  -- FK solo para GROUP_MEETING
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_context_type_scheduled UNIQUE(context_id, meeting_type, scheduled_date)
);

CREATE INDEX idx_meetings_context_id ON meetings(context_id);
CREATE INDEX idx_meetings_type_id ON meetings(type_id);
CREATE INDEX idx_meetings_meeting_type ON meetings(meeting_type);
CREATE INDEX idx_meetings_scheduled_date ON meetings(scheduled_date);

-- Tabla de configuración de tipos de reuniones
CREATE TABLE IF NOT EXISTS meeting_type_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meeting_type_enum VARCHAR(50) NOT NULL,  -- WORSHIP, GROUP_MEETING
    subtype_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_meeting_type_configs_enum ON meeting_type_configs(meeting_type_enum);
CREATE INDEX idx_meeting_type_configs_subtype ON meeting_type_configs(meeting_type_enum, subtype_id);

-- Migración de datos: worship_services → meetings (si existen)
-- IMPORTANTE: Ejecutar solo si worship_services existe
INSERT INTO meetings (
    id, meeting_type, name, description, creation_date, scheduled_date,
    context_id, type_id, worship_meeting_type_id
)
SELECT
    w.id,
    'WORSHIP',
    w.name,
    w.description,
    w.creation_date,
    w.scheduled_date,
    w.church_id,
    w.worship_meeting_type_id,
    w.worship_meeting_type_id
FROM worship_services w
WHERE NOT EXISTS (SELECT 1 FROM meetings WHERE id = w.id)
ON CONFLICT DO NOTHING;

-- Migración de datos: group_meetings → meetings (si existen)
-- IMPORTANTE: Ejecutar solo si group_meetings existe
INSERT INTO meetings (
    id, meeting_type, name, description, creation_date, scheduled_date,
    context_id, type_id, group_meeting_type_id
)
SELECT
    g.id,
    'GROUP_MEETING',
    g.name,
    g.description,
    g.creation_date,
    g.scheduled_date,  -- Usar columna 'date' si existe, sino 'scheduled_date'
    g.home_groups_id,
    g.group_meeting_type_id,
    g.group_meeting_type_id
FROM group_meetings g
WHERE NOT EXISTS (SELECT 1 FROM meetings WHERE id = g.id)
ON CONFLICT DO NOTHING;

-- Añadir FKs solo después de que los datos estén migrados
-- Para WorshipMeetingTypes
ALTER TABLE meetings
ADD CONSTRAINT fk_meetings_worship_type
FOREIGN KEY (worship_meeting_type_id) REFERENCES worship_meeting_types(id) ON DELETE SET NULL;

-- Para GroupMeetingTypes
ALTER TABLE meetings
ADD CONSTRAINT fk_meetings_group_type
FOREIGN KEY (group_meeting_type_id) REFERENCES group_meeting_types(id) ON DELETE SET NULL;

-- Comentario de auditoría
-- Esta migración normaliza worship_services y group_meetings en una única tabla 'meetings'
-- con herencia JPA usando SINGLE_TABLE strategy y discriminador 'meeting_type'
-- Las tablas antiguas se pueden mantener para auditoría o eliminar después de verificar

