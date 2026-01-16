## 📋 PROMPT PARA AGENTE FRONTEND

**Asunto**: Cambios en Contrato de API - Meetings con Timezone Obligatorio

---

### Contexto

El backend ha completado una **normalización mayor** de la gestión de reuniones (`worship_meetings` y `group_meetings`). 

**Impacto crítico**: Todos los endpoints que aceptan `scheduledDate` AHORA REQUIEREN timezone obligatorio en el formato ISO-8601.

---

### Cambios Principales

#### 1️⃣ DTO: Timezone Obligatorio

**Antes**:
```json
{
  "scheduledDate": "2026-01-15T10:00:00"  // ❌ Rechazado
}
```

**Ahora**:
```json
{
  "scheduledDate": "2026-01-15T10:00:00-05:00"  // ✅ Aceptado (con offset)
}
```

O con UTC:
```json
{
  "scheduledDate": "2026-01-15T15:00:00Z"  // ✅ También aceptado
}
```

**Acción requerida**: 
- [ ] Validar en cliente que `scheduledDate` siempre incluya offset
- [ ] Rechazar formularios sin timezone especificado
- [ ] Usar librería `date-fns-tz` o `moment-timezone` para manejo correcto

#### 2️⃣ Endpoints Afectados

Todos estos endpoints requieren `scheduledDate` con timezone:

- `POST /api/v1/worship-meetings` - Crear culto
- `PUT /api/v1/worship-meetings/{id}` - Actualizar culto
- `POST /api/v1/group-meetings` - Crear reunión de grupo
- `PUT /api/v1/group-meetings/{groupId}/{meetingId}` - Actualizar reunión

#### 3️⃣ Response: UTC Retornado

El backend ahora retorna SIEMPRE en UTC:

```json
{
  "id": "uuid",
  "name": "Culto Dominical",
  "scheduledDate": "2026-01-15T15:00:00Z",  // ← UTC (cambió formato)
  "creationDate": "2026-01-16T12:30:00Z"
}
```

**Acción requerida**:
- [ ] Convertir `scheduledDate` UTC a hora local para display
- [ ] Usar zona horaria del usuario (ej: America/Bogota)
- [ ] Mostrar hora en zona local en UI

---

### Flujo Frontend Recomendado

```typescript
// 1. Usuario selecciona fecha/hora en UI (siempre hora local)
const userLocalDate = new Date('2026-01-15T10:00:00');

// 2. Convertir a ISO-8601 con offset
import { format } from 'date-fns-tz';
const userTimeZone = 'America/Bogota';
const iso8601WithOffset = format(
  userLocalDate,
  'yyyy-MM-dd\'T\'HH:mm:ssXXX',
  { timeZone: userTimeZone }
);
// Resultado: "2026-01-15T10:00:00-05:00"

// 3. Validar antes de enviar
if (!iso8601WithOffset.match(/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}([+-]\d{2}:\d{2}|Z)$/)) {
  throw new Error('Fecha debe incluir timezone');
}

// 4. Enviar al backend
const response = await fetch('/api/v1/worship-meetings', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    meetingType: 'WORSHIP',
    name: 'Culto Dominical',
    scheduledDate: iso8601WithOffset,  // ✅ Con timezone
    worshipTypeId: 1
  })
});

// 5. Backend retorna en UTC
const data = await response.json();
// data.scheduledDate = "2026-01-15T15:00:00Z"

// 6. Convertir UTC a hora local para display
const utcDate = new Date(data.scheduledDate);
const localDate = utcDate.toLocaleString('es-CO', {
  timeZone: userTimeZone
});
// localDate = "15/01/2026, 10:00:00"
```

---

### Errores Esperados

#### ❌ Error 400: Timezone Faltante

**Request**:
```json
{
  "scheduledDate": "2026-01-15T10:00:00"  // Sin offset
}
```

**Response**:
```json
{
  "status": 400,
  "message": "scheduledDate debe incluir zona horaria. Formato: 2026-01-15T10:00:00-05:00"
}
```

**Acción**: 
- Mostrar alerta al usuario
- Re-validar input
- Usar zona del navegador por defecto

#### ❌ Error 409: Conflicto de Reunión

**Response**:
```json
{
  "status": 409,
  "message": "Ya existe una reunión del mismo tipo a esa hora"
}
```

---

### Librerías Recomendadas

Para manejo de timezones en JavaScript/TypeScript:

```json
// package.json
{
  "dependencies": {
    "date-fns": "^2.x",
    "date-fns-tz": "^2.x",
    // O alternativa
    "moment-timezone": "^0.5.x",
    // O la más moderna
    "temporal": "latest"  // (Próximamente en TC39)
  }
}
```

**Recomendación**: Usar `date-fns-tz` (más moderno que moment-timezone)

---

### Código de Ejemplo Completo

**FormComponent.tsx**:

```typescript
import { format, parse } from 'date-fns-tz';
import { useState } from 'react';

export function CreateWorshipForm() {
  const [date, setDate] = useState<Date>(new Date());
  const userTimeZone = 'America/Bogota';

  const handleSubmit = async () => {
    // Validar
    if (!date) {
      alert('Por favor selecciona una fecha');
      return;
    }

    // Convertir a ISO-8601 con offset
    const iso8601 = format(
      date,
      'yyyy-MM-dd\'T\'HH:mm:ssXXX',
      { timeZone: userTimeZone }
    );

    try {
      const res = await fetch('/api/v1/worship-meetings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          meetingType: 'WORSHIP',
          name: 'Culto Dominical',
          scheduledDate: iso8601,  // ✅ Con timezone
          worshipTypeId: 1
        })
      });

      if (!res.ok) {
        const error = await res.json();
        alert(`Error: ${error.message}`);
        return;
      }

      const meeting = await res.json();
      console.log('Culto creado:', meeting);
      
    } catch (err) {
      console.error('Error al crear culto:', err);
    }
  };

  return (
    <div>
      <input 
        type="datetime-local" 
        onChange={(e) => setDate(new Date(e.target.value))} 
      />
      <button onClick={handleSubmit}>Crear Culto</button>
    </div>
  );
}

export function MeetingCardComponent({ meeting }) {
  const userTimeZone = 'America/Bogota';
  
  // Backend retorna en UTC: "2026-01-15T15:00:00Z"
  const utcDate = new Date(meeting.scheduledDate);
  const localDate = utcDate.toLocaleString('es-CO', {
    timeZone: userTimeZone,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });

  return (
    <div className="meeting-card">
      <h3>{meeting.name}</h3>
      <p>Hora: {localDate} ({userTimeZone})</p>
    </div>
  );
}
```

---

### Tareas para Frontend

- [ ] **1. Actualizar DTOs** - Validar `scheduledDate` con regex ISO-8601 + offset
- [ ] **2. Instalar librerías** - `npm install date-fns date-fns-tz`
- [ ] **3. Actualizar formularios** - Convertir hora local a ISO-8601 antes de enviar
- [ ] **4. Actualizar visualización** - Convertir UTC a hora local para display
- [ ] **5. Manejo de errores** - Mostrar alerta si falta timezone
- [ ] **6. Tests** - Validar conversiones con diferentes timezones
- [ ] **7. Comunicación UX** - Informar usuarios sobre cambio de timezone

---

### Zona Horaria por Defecto

Se recomienda usar la zona horaria del navegador:

```typescript
// Detectar zona del navegador automáticamente
const userTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
// Resultado: "America/Bogota" (si está en Bogotá)

// O permitir selección manual en Settings
const userTimeZone = localStorage.getItem('userTimeZone') || 
                     Intl.DateTimeFormat().resolvedOptions().timeZone;
```

---

### Testing

**Casos de prueba recomendados**:

```typescript
describe('Timezone Handling', () => {
  it('should accept scheduledDate with timezone', async () => {
    const payload = {
      scheduledDate: '2026-01-15T10:00:00-05:00'
    };
    const res = await post('/api/v1/worship-meetings', payload);
    expect(res.status).toBe(201);
  });

  it('should reject scheduledDate without timezone', async () => {
    const payload = {
      scheduledDate: '2026-01-15T10:00:00'  // Sin offset
    };
    const res = await post('/api/v1/worship-meetings', payload);
    expect(res.status).toBe(400);
  });

  it('should return scheduledDate in UTC', async () => {
    const res = await post('/api/v1/worship-meetings', { ... });
    const data = await res.json();
    expect(data.scheduledDate).toMatch(/Z$/);  // Termina con Z
  });

  it('should convert UTC to local time correctly', () => {
    const utcDate = new Date('2026-01-15T15:00:00Z');
    const local = utcDate.toLocaleString('es-CO', {
      timeZone: 'America/Bogota'
    });
    expect(local).toContain('10:00');  // 15:00 UTC = 10:00 Bogotá
  });
});
```

---

### Documentación Adicional

- **Backend API Docs**: `/docs/API_CHANGES_FRONTEND.md`
- **Technical Summary**: `/docs/NORMALIZATION_TECHNICAL_SUMMARY.md`
- **Implementation Summary**: `/docs/IMPLEMENTATION_FINAL_SUMMARY.md`

---

### Preguntas Frecuentes

**Q: ¿Por qué cambia el formato del timestamp?**  
A: El backend ahora normaliza todo en UTC internamente. Frontend es responsable de conversión local ↔ UTC.

**Q: ¿Qué pasa si el usuario no especifica timezone?**  
A: El backend rechaza la solicitud con `400 Bad Request`. Frontend debe validar antes de enviar.

**Q: ¿Cómo manejo usuarios en diferentes zonas?**  
A: Cada usuario envía su hora local con su offset. Backend almacena en UTC. Al leer, convierte a su zona.

**Q: ¿Es compatible con móvil?**  
A: Sí, `Intl.DateTimeFormat` funciona en navegadores y React Native.

---

**Contacto Backend**: [Backend Team]  
**Deadline Implementación**: [Date]  
**Estado**: 🟢 Ready for Implementation

