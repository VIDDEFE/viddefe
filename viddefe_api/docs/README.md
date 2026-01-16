# 📚 NORMALIZACIÓN DE MEETINGS - DOCUMENTACIÓN COMPLETA

> **Status**: ✅ Backend Completado | ⏳ Implementación en Progreso  
> **Fecha**: 2026-01-16  
> **Versión**: 1.0

---

## 📖 Índice de Documentación

### 🎯 Empezar Aquí
1. **[IMPLEMENTATION_CHECKLIST.md](./IMPLEMENTATION_CHECKLIST.md)** ← EMPIEZA AQUÍ
   - Checklist de todas las fases
   - Próximos pasos definidos
   - Matriz de responsabilidades

2. **[IMPLEMENTATION_FINAL_SUMMARY.md](./IMPLEMENTATION_FINAL_SUMMARY.md)**
   - Resumen de cambios implementados
   - Archivos creados y modificados
   - Instrucciones de deployment

### 🏗️ Documentación Técnica
3. **[NORMALIZATION_TECHNICAL_SUMMARY.md](./NORMALIZATION_TECHNICAL_SUMMARY.md)**
   - Cambios en BD (antes/después)
   - Mapeo de entidades Java
   - Servicios refactorizados
   - Ejemplos de código

### 🔗 Para Frontend
4. **[FRONTEND_AGENT_PROMPT.md](./FRONTEND_AGENT_PROMPT.md)** ← LEER SI ERES FRONTEND
   - Prompt para agente frontend
   - Cambios en contrato de API
   - Ejemplos de código TypeScript
   - Casos de prueba

5. **[API_CHANGES_FRONTEND.md](./API_CHANGES_FRONTEND.md)**
   - Cambios en DTOs
   - Endpoints afectados
   - Instrucciones para cliente
   - Manejo de errores
   - Ejemplos de conversión de timezones

### 📋 Otros
6. **[ARCHITECTURE_DEPENDENCY_REFACTORING.md](./ARCHITECTURE_DEPENDENCY_REFACTORING.md)**
   - Arquitectura general del proyecto
   - Dependencias entre módulos

---

## 🚀 Flujo Recomendado

### Para Backend/DevOps
```
1. IMPLEMENTATION_CHECKLIST.md       (entender qué se hizo)
   ↓
2. NORMALIZATION_TECHNICAL_SUMMARY   (entender la técnica)
   ↓
3. Ejecutar migración SQL
   ↓
4. Ejecutar tests
   ↓
5. Deploy
```

### Para Frontend
```
1. FRONTEND_AGENT_PROMPT.md          (entender cambios)
   ↓
2. API_CHANGES_FRONTEND.md           (detalles de API)
   ↓
3. Actualizar código
   ↓
4. Tests de conversión de timezone
   ↓
5. Deploy
```

### Para QA
```
1. IMPLEMENTATION_CHECKLIST.md       (entender fases)
   ↓
2. API_CHANGES_FRONTEND.md           (casos de prueba)
   ↓
3. Tests end-to-end
   ↓
4. Validación de datos
```

---

## 📊 Resumen Ejecutivo

### ¿Qué se hizo?

**Normalización**: 2 tablas redundantes (`worship_services`, `group_meetings`) → 1 tabla unificada (`meetings`) con discriminador JPA.

### ¿Por qué?

- ❌ Duplicación de código
- ❌ Inconsistencias en timezone
- ❌ Difícil de mantener y escalar

### ✅ Resultados

- **1 tabla**: `meetings` con discriminador SINGLE_TABLE
- **1 servicio**: `MeetingService` centralizado
- **1 repositorio**: `MeetingRepository` genérico
- **Timezone**: Centralizado en `OffsetDateTime` sin conversiones
- **Escalabilidad**: Fácil agregar nuevos tipos de reuniones

---

## 📦 Archivos Creados

### Entidades
```java
✅ MeetingTypeEnum.java
✅ MeetingTypeConfig.java
```

### Servicios
```java
✅ MeetingService.java
✅ MeetingTypeConfigService.java
```

### Repositorios
```java
✅ MeetingRepository.java
✅ MeetingTypeConfigRepository.java
```

### DTOs
```java
✅ MeetingTypeConfigDto.java
```

### Base de Datos
```sql
✅ V2026_01_16_01__normalize_meetings_table.sql
```

### Documentación
```markdown
✅ IMPLEMENTATION_CHECKLIST.md
✅ IMPLEMENTATION_FINAL_SUMMARY.md
✅ NORMALIZATION_TECHNICAL_SUMMARY.md
✅ FRONTEND_AGENT_PROMPT.md
✅ API_CHANGES_FRONTEND.md
✅ README.md (este archivo)
```

---

## 📝 Archivos Modificados

### Entidades
```java
✅ Meeting.java                    (@Entity con discriminador)
✅ WorshipMeetingModel.java        (@DiscriminatorValue("WORSHIP"))
✅ GroupMeetings.java              (@DiscriminatorValue("GROUP_MEETING"))
```

### Servicios
```java
✅ WorshipServicesImpl.java         (Refactorizado para usar MeetingService)
✅ GroupMeetingServiceImpl.java     (Refactorizado para usar MeetingService)
✅ MinistryNotificationJobRoutine  (Corregidos imports)
```

### Configuración
```properties
✅ application.properties           (spring.jackson.time-zone=UTC, etc.)
```

---

## 🔄 Cambios en Arquitectura

### ANTES (Desnormalizado)

```
┌─────────────────────────────┐
│   worship_services          │
│  - id, name, description    │
│  - scheduled_date           │
│  - church_id (FK)           │
│  - worship_meeting_type_id  │
└─────────────────────────────┘

┌─────────────────────────────┐
│   group_meetings            │
│  - id, name, description    │
│  - date                     │
│  - home_groups_id (FK)      │
│  - group_meeting_type_id    │
└─────────────────────────────┘
```

**Problemas**: Duplicación, inconsistencias, difícil de mantener

### DESPUÉS (Normalizado)

```
┌──────────────────────────────────────┐
│   meetings (SINGLE_TABLE)            │
│  - id                                │
│  - meeting_type (discriminador)      │
│  - name, description                 │
│  - scheduled_date (OffsetDateTime)   │
│  - context_id (iglesia o grupo)      │
│  - type_id (tipo genérico)           │
│  - worship_meeting_type_id (nullable)│
│  - group_meeting_type_id (nullable)  │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│   meeting_type_configs               │
│  - id                                │
│  - meeting_type_enum                 │
│  - subtype_id                        │
│  - name, description                 │
└──────────────────────────────────────┘
```

**Beneficios**: Sin duplicación, centralizado, fácil de escalar

---

## ⏰ Timezone - Cambio Crítico

### Regla de Oro
**Backend en UTC, Frontend maneja conversión local**

### Configuración
```properties
spring.jackson.time-zone=UTC
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

### Flujo
```
Frontend (Input Local)
  "2026-01-15T10:00:00-05:00"
         ↓
Backend (Almacena UTC)
  "2026-01-15T15:00:00Z" (internamente)
         ↓
Frontend (Display Local)
  "2026-01-15T10:00:00" (Bogotá)
```

### Validación
- ✅ DTOs requieren `@NotNull` en `scheduledDate`
- ✅ Jackson rechaza sin timezone (400 Bad Request)
- ✅ Backend retorna siempre en UTC
- ✅ Frontend convierte para display

---

## 🧪 Testing Recomendado

### Tests Unitarios
```bash
mvn test
```

**Casos**:
- Crear culto → Se persiste en BD
- Obtener culto → Se retorna en UTC
- Timezone se preserva sin conversiones

### Tests de Integración
```bash
mvn verify
```

**Casos**:
- Endpoint POST /worship-meetings acepta con timezone
- Endpoint POST /worship-meetings rechaza sin timezone
- Endpoint GET /worship-meetings retorna en UTC

### Tests E2E (Manual)
```bash
# Crear con timezone
curl -X POST http://localhost:8080/api/v1/worship-meetings \
  -H "Content-Type: application/json" \
  -d '{
    "meetingType": "WORSHIP",
    "name": "Culto",
    "scheduledDate": "2026-01-15T10:00:00-05:00",
    "worshipTypeId": 1
  }'

# Debe retornar 201 con scheduledDate en UTC
```

---

## 🚀 Deployment

### Pre-requisitos
- [x] Compilación exitosa: `mvn clean compile`
- [ ] Tests pasados: `mvn test`
- [ ] Migración SQL ejecutada
- [ ] Datos validados

### Pasos
1. **Ejecutar migración SQL**
   ```bash
   mvn liquibase:update
   # o
   psql -f src/main/resources/db/migration/V2026_01_16_01__normalize_meetings_table.sql
   ```

2. **Compilar**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar tests**
   ```bash
   mvn test
   ```

4. **Iniciar aplicación**
   ```bash
   mvn spring-boot:run
   ```

5. **Validar endpoints**
   ```bash
   curl http://localhost:8080/api/v1/worship-meetings
   ```

---

## ⚠️ Consideraciones Importantes

### Datos Existentes
- Migración SQL preserva datos históricos
- Tablas antiguas se mantienen para auditoría (30 días mín.)
- Se pueden eliminar después de validar

### Backward Compatibility
- Repositorios antiguos se mantienen
- Servicios nuevos son principales
- APIs no cambian (solo timezone obligatorio)

### Performance
- SINGLE_TABLE evita JOINs
- Índices en `context_id`, `type_id`, `meeting_type`
- Constraint unique previene duplicados

---

## 📞 Contacto y Soporte

### Por Módulo

| Módulo | Contacto | Doc |
|--------|----------|-----|
| Backend | Backend Team | NORMALIZATION_TECHNICAL_SUMMARY.md |
| Frontend | Frontend Team | FRONTEND_AGENT_PROMPT.md |
| Database | DBA Team | V2026_01_16_01__normalize_meetings_table.sql |
| QA | QA Team | IMPLEMENTATION_CHECKLIST.md |

### FAQ

**Q: ¿Debo cambiar mi código ahora?**  
A: No si eres backend. Sí si eres frontend (timezone obligatorio).

**Q: ¿Se pierden datos?**  
A: No. La migración SQL preserva todos los datos.

**Q: ¿Cuándo ejecuto la migración?**  
A: Antes de iniciar la aplicación después del deploy.

**Q: ¿Qué pasa con los datos históricos?**  
A: Se migran automáticamente a la nueva tabla con timestamp convertido.

---

## 🎯 Próximos Pasos

1. **Hoy**: Backend Team entrega código ✅
2. **Mañana**: DBA Team ejecuta migración SQL ⏳
3. **Siguiente**: QA Team valida datos ⏳
4. **Siguiente**: Frontend Team actualiza código ⏳
5. **Siguiente**: Deploy a staging ⏳
6. **Final**: Deploy a producción ⏳

---

## 📊 Progreso

```
Backend Implementation    ████████████████████ 100% ✅
Database Migration        ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Testing & QA             ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Frontend Updates          ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Deployment               ░░░░░░░░░░░░░░░░░░░░   0% ⏳
```

---

## 📚 Referencias Externas

- [PostgreSQL timestamptz](https://www.postgresql.org/docs/current/datatype-datetime.html)
- [JPA Single Table Inheritance](https://www.baeldung.com/hibernate-single-table-inheritance)
- [ISO 8601 Standard](https://en.wikipedia.org/wiki/ISO_8601)
- [date-fns-tz](https://date-fns.org/docs/Locale)

---

**Documento**: README.md  
**Última Actualización**: 2026-01-16  
**Versión**: 1.0  
**Estado**: ✅ Backend Completado

