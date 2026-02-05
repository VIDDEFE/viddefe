# Implementación - Endpoint /meetings/metrics

## ✅ Archivos Creados

### 1. Servicio API
📄 **`src/services/metricsService.ts`** (94 líneas)
- Función `metricsService.getMetrics()` para obtener métricas
- Tipos: `BaseMetrics`, `WorshipMetrics`, `MetricsQueryParams`
- Helper: `buildMetricsQueryParams()` para construir query strings
- Documentación completa con ejemplos

### 2. Hooks de TanStack Query
📄 **`src/hooks/useMetrics.ts`** (95 líneas)
- `useMetrics(params?)` - Hook genérico para cualquier tipo
- `useGroupMetrics(groupId?, startTime?, endTime?)` - Hook especializado para GROUP_MEETING
- `useWorshipMetrics(churchId?, startTime?, endTime?)` - Hook especializado para TEMPLE_WORHSIP
- Caché automático, placeholder data, validación de parámetros

### 3. Documentación
📄 **`METRICAS_DOCUMENTACION.md`** - Guía completa
📄 **`src/EJEMPLOS_METRICAS.tsx`** - 4 ejemplos prácticos de uso

## ✅ Archivos Actualizados

### 4. Tipos TypeScript
📄 **`src/models/types.ts`**
- Agregados: `BaseMetrics`, `WorshipMetrics`
- Reutilizables en todo el proyecto

### 5. Exportaciones
📄 **`src/services/index.ts`** - Exporta `metricsService`
📄 **`src/hooks/index.ts`** - Exporta `useMetrics`, `useGroupMetrics`, `useWorshipMetrics`

## 🏗️ Arquitectura

```
Service Layer
┌─────────────────────────────────────┐
│  metricsService.getMetrics()        │ ← Llamadas HTTP directas
│  - Construye query params           │
│  - Llama /meetings/metrics endpoint │
└─────────────────────────────────────┘
          ↓ (usa)
React Query Wrapper
┌─────────────────────────────────────┐
│  useMetrics(params)                 │ ← Hook genérico
│  useGroupMetrics(...)               │ ← Hook especializado GROUP_MEETING
│  useWorshipMetrics(...)             │ ← Hook especializado TEMPLE_WORHSIP
│  - Caché automático                 │
│  - Placeholder data                 │
│  - Manejo de errores                │
└─────────────────────────────────────┘
          ↓ (consume)
View/Component
┌─────────────────────────────────────┐
│  const { data, isLoading } =        │
│    useGroupMetrics(groupId, ...)    │
│                                     │
│  <MetricsCard data={data} />        │
└─────────────────────────────────────┘
```

## 📊 Respuesta del Endpoint

### GROUP_MEETING Response
```json
{
  "newAttendees": 0,
  "totalPeopleAttended": 0,
  "totalPeople": 0,
  "attendanceRate": 66.67,
  "absenceRate": 33.33,
  "totalMeetings": 5,
  "averageAttendancePerMeeting": 2.5
}
```

### TEMPLE_WORHSIP Response (con desglose)
```json
{
  "newAttendees": 3,
  "totalPeopleAttended": 2,
  "totalPeople": 3,
  "attendanceRate": 66.67,
  "absenceRate": 33.33,
  "totalMeetings": 5,
  "averageAttendancePerMeeting": 0.4,
  "totalGroups": 2,
  "groupMetrics": { ... },      ← Desglose de grupos
  "churchMetrics": { ... }      ← Desglose de iglesia
}
```

## 🔧 Cómo Usar

### Opción 1: Hook Genérico
```typescript
import { useMetrics } from '../hooks';

const { data, isLoading } = useMetrics({
  type: 'GROUP_MEETING',
  contextId: 'group-123',
  startTime: '2026-01-01T00:00:00-05:00',
  endTime: '2026-01-31T23:59:59-05:00'
});
```

### Opción 2: Hook Especializado (Recomendado)
```typescript
import { useGroupMetrics } from '../hooks';

const { data: metrics, isLoading } = useGroupMetrics(
  'group-123',
  '2026-01-01T00:00:00-05:00',
  '2026-01-31T23:59:59-05:00'
);

// metrics es de tipo BaseMetrics - type-safe
console.log(metrics?.attendanceRate);
```

### Opción 3: Desglose de Iglesia
```typescript
import { useWorshipMetrics } from '../hooks';

const { data: metrics } = useWorshipMetrics(
  'church-123',
  startTime,
  endTime
);

// Acceso a las tres capas
console.log(metrics?.attendanceRate);              // General
console.log(metrics?.groupMetrics.attendanceRate); // Solo grupos
console.log(metrics?.churchMetrics.attendanceRate); // Solo iglesia
```

## ✨ Características

✅ **Type-safe**: Tipos TypeScript completos  
✅ **Caché automático**: TanStack Query maneja el caché  
✅ **Placeholder data**: Transiciones suaves  
✅ **Error handling**: Manejo automático de errores  
✅ **Enabled queries**: No hace request hasta tener todos los parámetros  
✅ **Documentación completa**: JSDoc en cada función  
✅ **Ejemplos prácticos**: Ver `src/EJEMPLOS_METRICAS.tsx`  
✅ **Patrón consistente**: Sigue el patrón del proyecto (Service → Hook → View)

## 🔗 Parámetros de Query

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|----------|-------------|
| `type` | string | ✅ | `TEMPLE_WORHSIP` o `GROUP_MEETING` |
| `contextId` | uuid | ✅ | ID de iglesia (worship) o ID de grupo (meeting) |
| `startTime` | ISO-8601 | ✅ | Fecha de inicio con timezone offset |
| `endTime` | ISO-8601 | ✅ | Fecha de fin con timezone offset |

**Ejemplo de fecha correcta:**
```
"2026-01-01T00:00:00-05:00"  ✅ Correcto
"2026-01-01T00:00:00Z"       ❌ Sin offset
```

Usar el helper: `toISOStringWithOffset(date)`

## 📦 Imports

```typescript
// Servicio (bajo nivel)
import { metricsService } from '../services';

// Hooks (recomendado)
import { useMetrics, useGroupMetrics, useWorshipMetrics } from '../hooks';

// Tipos
import type { BaseMetrics, WorshipMetrics, MetricsQueryParams } from '../models';
```

## 🎯 Próximos Pasos

1. Importar los hooks en tu vista/componente
2. Pasar los parámetros requeridos (groupId/churchId, startTime, endTime)
3. Renderizar los datos mientras `isLoading` es false
4. Acceder a las propiedades: `attendanceRate`, `totalMeetings`, etc.

## 📚 Documentación Completa

Ver: **`METRICAS_DOCUMENTACION.md`**  
Ejemplos: **`src/EJEMPLOS_METRICAS.tsx`**

---

**Status:** ✅ Listo para usar  
**Fecha:** Febrero 4, 2026
