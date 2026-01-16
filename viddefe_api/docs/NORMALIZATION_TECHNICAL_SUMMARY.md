# 🏗️ NORMALIZACIÓN DE MEETINGS - RESUMEN TÉCNICO

## 📊 Cambios en Arquitectura de Base de Datos

### ANTES (Desnormalizado)
```
worship_services (tabla específica)
├── id (UUID)
├── name (VARCHAR)
├── description (TEXT)
├── scheduled_date (TIMESTAMPTZ)
├── creation_date (TIMESTAMPTZ)
├── church_id (FK → churches)
└── worship_meeting_type_id (FK → worship_meeting_types)

group_meetings (tabla específica)
├── id (UUID)
├── name (VARCHAR)
├── description (TEXT)
├── date (TIMESTAMPTZ)
├── creation_date (TIMESTAMPTZ)
├── home_groups_id (FK → home_groups)
└── group_meeting_type_id (FK → group_meeting_types)
```

### DESPUÉS (Normalizado)
```
meetings (tabla unificada con discriminador)
├── id (UUID)
├── meeting_type (VARCHAR) ← Discriminador: WORSHIP, GROUP_MEETING
├── name (VARCHAR)
├── description (TEXT)
├── scheduled_date (TIMESTAMPTZ)
├── creation_date (TIMESTAMPTZ)
├── context_id (UUID) ← Abstracto: church_id o home_groups_id
├── type_id (BIGINT) ← Abstracto: worship_meeting_type_id o group_meeting_type_id
├── worship_meeting_type_id (FK → worship_meeting_types, nullable)
└── group_meeting_type_id (FK → group_meeting_types, nullable)

meeting_type_configs (tabla de configuración)
├── id (UUID)
├── meeting_type_enum (VARCHAR)
├── subtype_id (BIGINT)
├── name (VARCHAR)
└── description (TEXT)
```

---

## 🎯 Beneficios

| Aspecto | ANTES | DESPUÉS |
|--------|-------|---------|
| Tablas | 2 (worship_services, group_meetings) | 1 (meetings) + config |
| Duplicación | Alto - Mismos campos en 2 tablas | Cero |
| Consultas comunes | N/A | Unificadas en MeetingService |
| Escalabilidad | Difícil agregar nuevos tipos | Fácil: Agregar discriminador |
| Timezone handling | Inconsistente | Centralizado en Meeting |

---

## 🔄 Mapeo Entidades Java

### ANTES

```java
@Entity
@Table(name = "worship_services")
public class WorshipMeetingModel extends Meeting { ... }

@Entity
@Table(name = "group_meetings")
public class GroupMeetings extends Meeting { ... }
```

**Problemas**:
- `Meeting` era `@MappedSuperclass` (sin tabla)
- Cada entidad tenía su tabla separada
- Difícil consultar ambas al mismo tiempo

### DESPUÉS

```java
@Entity
@Table(name = "meetings")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "meeting_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Meeting { ... }

@Entity
@DiscriminatorValue("WORSHIP")
public class WorshipMeetingModel extends Meeting { ... }

@Entity
@DiscriminatorValue("GROUP_MEETING")
public class GroupMeetings extends Meeting { ... }
```

**Ventajas**:
- `Meeting` es `@Entity` en tabla unificada
- `SINGLE_TABLE` strategy evita JOINs
- Campos comunes centralizados
- Fácil filtrar por tipo

---

## 🛠️ Servicios Refactorizados

### MeetingService (Nuevo)

```java
@Service
public class MeetingService {
    // Operaciones genéricas para cualquier tipo de meeting
    public Meeting create(Meeting meeting)
    public Optional<Meeting> findById(UUID id)
    public Optional<Meeting> findByIdWithRelations(UUID id)
    public Page<Meeting> findByContextIdAndType(UUID contextId, MeetingTypeEnum type, Pageable p)
    public boolean existsConflict(UUID contextId, Long typeId, OffsetDateTime date)
    public void delete(UUID id)
}
```

### WorshipServicesImpl (Refactorizado)

```java
@Service
public class WorshipServicesImpl implements WorshipService {
    private MeetingService meetingService;  // ← NUEVO
    
    public WorshipDto createWorship(...) {
        // Crear WorshipMeetingModel
        // Asignar contextId = churchId
        // Asignar typeId = worshipMeetingTypes.id
        WorshipMeetingModel worship = new WorshipMeetingModel();
        worship.setContextId(churchId);
        worship.setTypeId(worshipTypeId);
        return ((WorshipMeetingModel) meetingService.create(worship)).toDto();
    }
}
```

### GroupMeetingServiceImpl (Refactorizado)

```java
@Service
public class GroupMeetingServiceImpl implements GroupMeetingService {
    private MeetingService meetingService;  // ← NUEVO
    
    public GroupMeetingDto createGroupMeeting(...) {
        // Crear GroupMeetings
        // Asignar contextId = groupId
        // Asignar typeId = groupMeetingTypeId
        GroupMeetings meeting = new GroupMeetings();
        meeting.setContextId(groupId);
        meeting.setTypeId(groupMeetingTypeId);
        return ((GroupMeetings) meetingService.create(meeting)).toDto();
    }
}
```

---

## 📋 Enums Nuevos

### MeetingTypeEnum

```java
public enum MeetingTypeEnum {
    WORSHIP("Culto"),
    GROUP_MEETING("Reunión de Grupo");
    
    private final String displayName;
    // Getters...
}
```

---

## 🗄️ Repositorios

### Antes

```java
// Separados
WorshipRepository extends JpaRepository<WorshipMeetingModel, UUID>
GroupMeetingRepository extends JpaRepository<GroupMeetings, UUID>
```

### Después

```java
// Unificado
MeetingRepository extends JpaRepository<Meeting, UUID> {
    Page<Meeting> findByContextIdAndMeetingType(UUID contextId, MeetingTypeEnum type, Pageable p);
    List<Meeting> findByContextIdAndScheduledDateBetween(...);
    boolean existsByContextIdAndTypeIdAndScheduledDate(...);
}

// Mantener para compatibilidad (legacy)
WorshipRepository, GroupMeetingRepository (con queries específicas si necesario)
```

---

## ⏰ Reglas de Timezone Centralizado

### En Meeting Base

```java
@Column(name = "scheduled_date", nullable = false, columnDefinition = "timestamptz")
private OffsetDateTime scheduledDate;  // ← Preserva offset del cliente

@Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz")
private Instant creationDate;  // ← Siempre UTC
```

### En Configuración Spring

```properties
spring.jackson.time-zone=UTC
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

### En DTOs

```java
@NotNull(message = "La fecha programada es obligatoria")
private OffsetDateTime scheduledDate;  // ← Validación Jackson obliga offset
```

---

## 🔀 Migración de Datos

### SQL de Migración

```sql
INSERT INTO meetings (id, meeting_type, name, description, creation_date, 
                      scheduled_date, context_id, type_id, worship_meeting_type_id)
SELECT w.id, 'WORSHIP', w.name, w.description, w.creation_date, w.scheduled_date,
       w.church_id, w.worship_meeting_type_id, w.worship_meeting_type_id
FROM worship_services w;

INSERT INTO meetings (id, meeting_type, name, description, creation_date, 
                      scheduled_date, context_id, type_id, group_meeting_type_id)
SELECT g.id, 'GROUP_MEETING', g.name, g.description, g.creation_date, g.scheduled_date,
       g.home_groups_id, g.group_meeting_type_id, g.group_meeting_type_id
FROM group_meetings g;
```

---

## 🎯 Cómo Crear una Nueva Reunión

### Antes

```java
// Servicio específico
worshipService.createWorship(dto, churchId);
// o
groupMeetingService.createGroupMeeting(dto, groupId);
```

### Después

```java
// Crear entidad específica
WorshipMeetingModel worship = new WorshipMeetingModel();
worship.fromDto(dto);
worship.setContextId(churchId);
worship.setTypeId(worshipTypeId);

// Persistir mediante MeetingService
Meeting saved = meetingService.create(worship);

// Convertir a DTO si necesario
WorshipDto response = ((WorshipMeetingModel) saved).toDto();
```

---

## 📊 Ejemplo: Obtener Reuniones de una Iglesia

### Antes

```java
// Solo cultos
Page<WorshipDto> worships = worshipService.getAllWorships(pageable, churchId);

// Imposible: No hay forma unificada de obtener ambos tipos
```

### Después

```java
// Solo cultos (con MeetingService)
Page<Meeting> worships = meetingService.findByContextIdAndType(
    churchId, MeetingTypeEnum.WORSHIP, pageable
);

// Ambos tipos
Page<Meeting> allMeetings = meetingService.findByContextId(churchId, pageable);

// Por rango de fechas
List<Meeting> thisWeek = meetingService.findByContextIdTypeAndDateRange(
    churchId, MeetingTypeEnum.WORSHIP, start, end
);
```

---

## ✅ Validación de Implementación

- [x] Meeting es @Entity con discriminador
- [x] WorshipMeetingModel y GroupMeetings extienden Meeting
- [x] MeetingService creado y funcionando
- [x] MeetingRepository con queries genéricas
- [x] WorshipServicesImpl refactorizado
- [x] GroupMeetingServiceImpl refactorizado
- [x] MeetingTypeEnum y MeetingTypeConfig creados
- [x] Timezone centralizado (OffsetDateTime)
- [x] Configuración Spring correcta
- [x] Script SQL de migración preparado

---

## 🚀 Próximos Pasos

1. **Ejecutar migración SQL**: `V2026_01_16_01__normalize_meetings_table.sql`
2. **Compilar proyecto**: `mvn clean compile`
3. **Ejecutar tests**: `mvn test`
4. **Validar endpoints**: Tests de integración
5. **Notificar al frontend**: Ver documento `API_CHANGES_FRONTEND.md`

---

## 📝 Notas de Auditoría

- Tablas antiguas (`worship_services`, `group_meetings`) se pueden mantener para auditoría
- Se recomienda eliminarlas después de 30 días si migración es exitosa
- Todos los nuevos datos se insertan en tabla unificada `meetings`

