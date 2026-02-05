# Métricas de Asistencia - Documentación

## Overview

Se ha implementado un nuevo endpoint `/meetings/metrics` para obtener estadísticas de asistencia de reuniones. El sistema soporta dos tipos de métricas:

1. **GROUP_MEETING**: Métricas de reuniones de grupo
2. **TEMPLE_WORHSIP**: Métricas de cultos (incluye desglose entre grupos e iglesia)

## Estructura de Archivos Creados

```
src/
├── services/
│   └── metricsService.ts          ← Servicio de bajo nivel (llamadas API)
├── hooks/
│   └── useMetrics.ts              ← Hooks de TanStack Query
├── models/
│   └── types.ts                   ← Tipos TypeScript (exportados aquí)
└── EJEMPLOS_METRICAS.tsx          ← Ejemplos de uso (este archivo)
```

## API Endpoints

### GET /meetings/metrics

Obtiene métricas de asistencia para un rango de fechas.

**Parámetros de Query:**
- `type` (string) - Tipo de reunión: `TEMPLE_WORHSIP` o `GROUP_MEETING`
- `contextId` (uuid) - ID del contexto (iglesia para TEMPLE_WORHSIP, grupo para GROUP_MEETING)
- `startTime` (ISO-8601) - Fecha de inicio con timezone offset
- `endTime` (ISO-8601) - Fecha de fin con timezone offset

**Ejemplo de Request:**
```
GET /meetings/metrics?type=GROUP_MEETING&contextId=abc-123&startTime=2026-01-01T00:00:00-05:00&endTime=2026-01-31T23:59:59-05:00
```

## Tipos TypeScript

### BaseMetrics
Métricas básicas que aplican a todos los tipos:

```typescript
interface BaseMetrics {
  newAttendees: number;              // Personas nuevas
  totalPeopleAttended: number;       // Total de personas que asistieron
  totalPeople: number;               // Total de personas registradas
  attendanceRate: number;            // Porcentaje de asistencia (0-100)
  absenceRate: number;               // Porcentaje de ausencia (0-100)
  totalMeetings: number;             // Total de reuniones
  averageAttendancePerMeeting: number; // Promedio de asistentes por reunión
}
```

### WorshipMetrics
Métricas extendidas para TEMPLE_WORHSIP (incluye desglose):

```typescript
interface WorshipMetrics extends BaseMetrics {
  totalGroups: number;              // Total de grupos en la iglesia
  groupMetrics: BaseMetrics;        // Desglose: métricas de grupos
  churchMetrics: BaseMetrics;       // Desglose: métricas de iglesia/cultos
}
```

## Servicio (metricsService)

**Archivo:** `src/services/metricsService.ts`

```typescript
export const metricsService = {
  getMetrics: (params: MetricsQueryParams) => Promise<BaseMetrics | WorshipMetrics>
};
```

**Uso directo:**
```typescript
import { metricsService } from '../services';

const metrics = await metricsService.getMetrics({
  type: 'GROUP_MEETING',
  contextId: 'group-123',
  startTime: '2026-01-01T00:00:00-05:00',
  endTime: '2026-01-31T23:59:59-05:00'
});
```

## Hooks (Recomendado)

**Archivo:** `src/hooks/useMetrics.ts`

### Hook 1: useMetrics (Genérico)
Para cualquier tipo de métrica:

```typescript
const { data, isLoading, error } = useMetrics({
  type: 'GROUP_MEETING',
  contextId: 'group-123',
  startTime: '2026-01-01T00:00:00-05:00',
  endTime: '2026-01-31T23:59:59-05:00'
});
```

### Hook 2: useGroupMetrics (Especializado)
Para métricas de GROUP_MEETING (más type-safe):

```typescript
const { data: metrics, isLoading, error } = useGroupMetrics(
  'group-123',
  '2026-01-01T00:00:00-05:00',
  '2026-01-31T23:59:59-05:00'
);

// metrics es de tipo BaseMetrics
if (metrics) {
  console.log(metrics.attendanceRate);
}
```

### Hook 3: useWorshipMetrics (Especializado)
Para métricas de TEMPLE_WORHSIP (acceso a desglose):

```typescript
const { data: metrics, isLoading, error } = useWorshipMetrics(
  'church-123',
  '2026-01-01T00:00:00-05:00',
  '2026-01-31T23:59:59-05:00'
);

// metrics es de tipo WorshipMetrics
if (metrics) {
  console.log(metrics.attendanceRate);           // General
  console.log(metrics.groupMetrics.attendanceRate);  // Desglose: Grupos
  console.log(metrics.churchMetrics.attendanceRate); // Desglose: Iglesia
}
```

## Ejemplo de Componente React

```tsx
import { useGroupMetrics } from '../hooks';
import { toISOStringWithOffset } from '../utils/helpers';

export function GroupStatistics({ groupId }: { groupId: string }) {
  const startDate = new Date('2026-01-01');
  const endDate = new Date('2026-01-31');
  
  const { data: metrics, isLoading } = useGroupMetrics(
    groupId,
    toISOStringWithOffset(startDate),
    toISOStringWithOffset(endDate)
  );
  
  if (isLoading) return <div>Cargando...</div>;
  if (!metrics) return <div>Sin datos</div>;
  
  return (
    <div className="grid grid-cols-2 gap-4">
      <div>
        <p>Tasa de Asistencia</p>
        <p className="text-2xl font-bold">{metrics.attendanceRate.toFixed(1)}%</p>
      </div>
      <div>
        <p>Total de Reuniones</p>
        <p className="text-2xl font-bold">{metrics.totalMeetings}</p>
      </div>
    </div>
  );
}
```

## Manejo de Fechas

**Importante:** El backend requiere fechas ISO-8601 con timezone offset.

Usar los helpers de `src/utils/helpers.ts`:

```typescript
import { toISOStringWithOffset } from '../utils/helpers';

const startTime = toISOStringWithOffset(new Date('2026-01-01'));
// Resultado: "2026-01-01T00:00:00-05:00"
```

## Caché y Revalidación

Los hooks usan **TanStack Query** con las siguientes características:

- **Caché automático:** Las métricas se cachean por query key
- **Placeholder data:** Se mantiene el último estado conocido durante transiciones
- **Revalidación manual:** Usar `queryClient.invalidateQueries()`

```typescript
import { useQueryClient } from '@tanstack/react-query';

export function RefreshMetrics() {
  const qc = useQueryClient();
  
  const handleRefresh = () => {
    qc.invalidateQueries({ queryKey: ['metrics'] });
  };
  
  return <button onClick={handleRefresh}>Actualizar Métricas</button>;
}
```

## Response Completo (Backend)

### GROUP_MEETING Response:
```json
{
  "success": true,
  "status": 0,
  "message": "string",
  "data": {
    "newAttendees": 0,
    "totalPeopleAttended": 0,
    "totalPeople": 0,
    "attendanceRate": 0.1,
    "absenceRate": 0.1,
    "totalMeetings": 0,
    "averageAttendancePerMeeting": 0.1
  },
  "timestamp": "2026-02-04T14:41:14.216Z"
}
```

### TEMPLE_WORHSIP Response:
```json
{
  "success": true,
  "status": 200,
  "message": "OK",
  "data": {
    "newAttendees": 3,
    "totalPeopleAttended": 2,
    "totalPeople": 3,
    "attendanceRate": 66.66,
    "absenceRate": 33.33,
    "totalMeetings": 5,
    "averageAttendancePerMeeting": 0.4,
    "totalGroups": 2,
    "groupMetrics": {
      "newAttendees": 2,
      "totalPeopleAttended": 2,
      "totalPeople": 2,
      "attendanceRate": 100.0,
      "absenceRate": 0.0,
      "totalMeetings": 2,
      "averageAttendancePerMeeting": 1.0
    },
    "churchMetrics": {
      "newAttendees": 0,
      "totalPeopleAttended": 0,
      "totalPeople": 0,
      "attendanceRate": 0.0,
      "absenceRate": 0.0,
      "totalMeetings": 0,
      "averageAttendancePerMeeting": 0.0
    }
  },
  "timestamp": "2026-02-04T19:41:27.123866400Z"
}
```

**Nota:** El interceptor de `src/services/api.ts` desenvuelve automáticamente `.data`, por lo que los hooks devuelven directamente `BaseMetrics` o `WorshipMetrics`.

## Patrón de Arquitectura Seguido

Se ha respetado el patrón del proyecto:

```
Service → Hook → View Component
```

1. **Service** (`metricsService.ts`): Llamadas API de bajo nivel
2. **Hook** (`useMetrics.ts`): Wrapper de TanStack Query
3. **View**: Consumir el hook en componentes React

## Exportaciones

Las nuevas funcionalidades están correctamente exportadas:

- `src/services/index.ts` → exporta `metricsService`
- `src/hooks/index.ts` → exporta `useMetrics`, `useGroupMetrics`, `useWorshipMetrics`
- `src/models/types.ts` → exporta `BaseMetrics`, `WorshipMetrics`

## Importar en tu Código

```typescript
// Servicio (bajo nivel)
import { metricsService } from '../services';

// Hooks (recomendado)
import { useMetrics, useGroupMetrics, useWorshipMetrics } from '../hooks';

// Tipos
import type { BaseMetrics, WorshipMetrics } from '../models';
```

---

**Creado:** Febrero 4, 2026  
**Status:** ✅ Implementación completa  
**Ejemplo de uso:** Ver `src/EJEMPLOS_METRICAS.tsx`
