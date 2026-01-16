# 📋 CAMBIOS EN CONTRATOS DE API - Frontend (IMPORTANTE)

## 🎯 Resumen Ejecutivo

El backend ha normalizado la gestión de reuniones/meetings. **Todos los endpoints de meetings ahora requieren `scheduledDate` con timezone obligatorio**.

---

## 🔑 CAMBIOS CRÍTICOS EN DTOs

### ✅ Requerimiento Obligatorio: Timezone en Timestamps

Todos los DTOs para crear/actualizar reuniones AHORA REQUIEREN timezone.

#### ❌ ANTES (Rechazado)
```json
{
  "meetingType": "WORSHIP",
  "name": "Culto Dominical",
  "scheduledDate": "2026-01-15T10:00:00"
}
```
**Resultado**: `400 Bad Request` - Timezone obligatorio

#### ✅ AHORA (Aceptado)
```json
{
  "meetingType": "WORSHIP",
  "name": "Culto Dominical",
  "scheduledDate": "2026-01-15T10:00:00-05:00"
}
```

O con UTC:
```json
{
  "meetingType": "WORSHIP",
  "name": "Culto Dominical",
  "scheduledDate": "2026-01-15T15:00:00Z"
}
```

---

## 📡 ENDPOINTS AFECTADOS

### 1. POST /worship-meetings (Crear Culto)

**Request - Cambio Obligatorio**:
```json
{
  "meetingType": "WORSHIP",
  "name": "string",
  "description": "string (opcional)",
  "scheduledDate": "2026-01-15T10:00:00-05:00",  // ← TIMEZONE OBLIGATORIO
  "worshipTypeId": 1
}
```

**Response** (Sin cambios, pero con timezone):
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "scheduledDate": "2026-01-15T15:00:00Z",  // ← Retorna en UTC
  "creationDate": "2026-01-16T12:30:00Z",
  "worshipType": {
    "id": 1,
    "name": "Dominical"
  }
}
```

### 2. POST /group-meetings (Crear Reunión de Grupo)

**Request - Cambio Obligatorio**:
```json
{
  "meetingType": "GROUP_MEETING",
  "name": "string",
  "description": "string (opcional)",
  "scheduledDate": "2026-01-15T19:00:00-05:00",  // ← TIMEZONE OBLIGATORIO
  "groupMeetingTypeId": 1
}
```

### 3. PUT /worship-meetings/{id}

**Request - Cambio Obligatorio**:
```json
{
  "name": "string",
  "description": "string",
  "scheduledDate": "2026-01-15T10:00:00-05:00",  // ← TIMEZONE OBLIGATORIO
  "worshipTypeId": 1
}
```

### 4. PUT /group-meetings/{groupId}/{meetingId}

**Request - Cambio Obligatorio**:
```json
{
  "name": "string",
  "description": "string",
  "scheduledDate": "2026-01-15T19:00:00-05:00",  // ← TIMEZONE OBLIGATORIO
  "groupMeetingTypeId": 1
}
```

---

## 🛠️ INSTRUCCIONES PARA FRONTEND

### Paso 1: Generar OffsetDateTime en JavaScript/TypeScript

```typescript
// Obtener hora local con timezone
const now = new Date();
const offset = -now.getTimezoneOffset() / 60;
const offsetStr = (offset >= 0 ? '+' : '') + String(offset).padStart(2, '0') + ':00';
const isoWithOffset = now.toISOString().slice(0, -1) + offsetStr;

// O usar librería recomendada:
import { format } from 'date-fns-tz';
const userTimeZone = 'America/Bogota';
const formattedDate = format(
  new Date('2026-01-15T10:00:00'),
  'yyyy-MM-dd\'T\'HH:mm:ssXXX',
  { timeZone: userTimeZone }
);
```

### Paso 2: Validar en Cliente Antes de Enviar

```typescript
function validateScheduledDate(dateString: string): boolean {
  // Debe cumplir con ISO-8601 con offset
  const pattern = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}([+-]\d{2}:\d{2}|Z)$/;
  return pattern.test(dateString);
}

// Uso:
if (!validateScheduledDate(form.scheduledDate)) {
  throw new Error('La fecha debe incluir zona horaria (ej: -05:00 o Z)');
}
```

### Paso 3: Enviar al Backend

```typescript
const payload = {
  meetingType: 'WORSHIP',
  name: 'Culto Dominical',
  scheduledDate: '2026-01-15T10:00:00-05:00',  // ✅ Con timezone
  worshipTypeId: 1
};

const response = await fetch('/api/v1/worship-meetings', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(payload)
});

if (response.status === 400) {
  const error = await response.json();
  console.error('Timezone requerido:', error);
}
```

---

## ⚠️ MANEJO DE ERRORES

### Error 400: Timezone Faltante

**Respuesta del Servidor**:
```json
{
  "status": 400,
  "message": "scheduledDate debe incluir zona horaria. Formato: 2026-01-15T10:00:00-05:00 o 2026-01-15T10:00:00Z",
  "timestamp": "2026-01-16T12:30:00Z"
}
```

**Acciones en Frontend**:
1. Mostrar mensaje: "Por favor incluye tu zona horaria"
2. Re-validar el input
3. Usar zona horaria del navegador por defecto

### Error 409: Conflicto de Reunión

**Respuesta del Servidor**:
```json
{
  "status": 409,
  "message": "Ya existe una reunión del mismo tipo a esa hora",
  "timestamp": "2026-01-16T12:30:00Z"
}
```

---

## 📅 CONVERSIÓN DE HORAS: LOCAL → UTC

**Regla**: El backend almacena en UTC, frontend convierte local → UTC antes de enviar.

### Ejemplo Práctico

**Usuario en Bogotá (UTC-5)** quiere agendar reunión para **2026-01-15 a las 10:00 AM**:

1. **Hora local**: `2026-01-15T10:00:00` (Bogotá)
2. **Con offset**: `2026-01-15T10:00:00-05:00`
3. **Backend almacena (UTC)**: `2026-01-15T15:00:00Z`
4. **Backend retorna**: `2026-01-15T15:00:00Z`
5. **Frontend convierte a local**: `2026-01-15T10:00:00` (Bogotá)

```typescript
// Frontend: Convertir de local a ISO con offset
const localDate = new Date('2026-01-15T10:00:00');
const isoWithOffset = localDate.toLocaleString('sv-SE', {
  timeZone: 'America/Bogota'
}).replace(' ', 'T') + '-05:00';

console.log(isoWithOffset); // 2026-01-15T10:00:00-05:00
```

---

## 🔄 RESPONSE: Retorno de Reuniones (GET)

El backend ahora retorna siempre en **UTC** con offset `Z`:

```json
{
  "id": "12345678-1234-1234-1234-123456789012",
  "name": "Culto Dominical",
  "description": "Servicio de adoración",
  "scheduledDate": "2026-01-15T15:00:00Z",
  "creationDate": "2026-01-16T12:30:00Z",
  "worshipType": {
    "id": 1,
    "name": "Dominical"
  }
}
```

**Frontend**: Convertir `2026-01-15T15:00:00Z` a hora local del usuario:

```typescript
const utcDate = new Date('2026-01-15T15:00:00Z');
const localDate = new Date(utcDate.toLocaleString('es-CO', {
  timeZone: 'America/Bogota'
}));

console.log(localDate); // 2026-01-15T10:00:00 (Bogotá)
```

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

- [ ] Validar que `scheduledDate` siempre incluya offset
- [ ] Usar librería `date-fns-tz` o similar para manejo de timezones
- [ ] Mostrar mensaje de error si timezone falta
- [ ] Convertir hora local a ISO-8601 antes de enviar
- [ ] Convertir respuesta UTC a hora local para display
- [ ] Usar timezone del navegador como default (si usuario no especifica)
- [ ] Probar con diferentes zonas horarias (Bogotá, NY, London, etc.)

---

## 📚 RECURSOS

- **ISO-8601 Standard**: https://en.wikipedia.org/wiki/ISO_8601
- **date-fns-tz Docs**: https://date-fns.org/docs/Locale
- **Timezone List**: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones

---

## 🆘 SOPORTE

Si encuentras errores `400 Bad Request` al enviar `scheduledDate`:
1. Verifica que incluya offset: `±HH:MM` o `Z`
2. Usa formato ISO-8601: `YYYY-MM-DDTHH:MM:SS±HH:MM`
3. Consulta el error exacto en la respuesta del servidor

