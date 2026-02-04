# Refactorización de MetricsRedisService a MetricsRedisAdapter

## Resumen de Cambios

Esta refactorización implementa los principios de arquitectura limpia y hexagonal para el manejo de Redis en el módulo `worship_meetings`.

### Cambios Realizados

1. **Creación de nuevo componente**: `MetricsRedisAdapter`
   - Ubicación: `com.viddefe.viddefe_api.worship_meetings.infrastructure.redis.MetricsRedisAdapter`
   - Clasificación: `@Component` (componente de Spring)
   - Tipo: Adaptador de infraestructura que encapsula acceso a Redis

2. **Eliminación de componente antiguo**: `MetricsRedisService`
   - Ubicación anterior: `com.viddefe.viddefe_api.worship_meetings.application.MetricsRedisService`
   - Razón: Violar el principio de arquitectura limpia al estar en la capa `application`

3. **Actualización de dependencias**:
   - `MetricsReportingServiceImpl` ahora inyecta `MetricsRedisAdapter` en lugar de `MetricsRedisService`
   - Todas las llamadas se actualizaron de `metricsRedisService.*` a `metricsRedisAdapter.*`

### Estructura Final

```
worship_meetings/
├── application/
│   ├── MetricsReportingServiceImpl.java  (actualizado con MetricsRedisAdapter)
│   └── ... otros servicios
├── infrastructure/
│   ├── redis/
│   │   └── MetricsRedisAdapter.java    (nuevo)
│   ├── dto/
│   ├── web/
│   └── ...
├── domain/
├── contracts/
└── ...
```

### Beneficios Arquitectónicos

1. **Separación de Responsabilidades**: El adaptador de Redis está donde debe estar: en la capa de infraestructura.

2. **Principio de Inversión de Dependencias**: La capa `application` no depende de `RedisTemplate` directamente.

3. **Testabilidad**: El componente de Redis está aislado y puede ser fácilmente mockeado en tests.

4. **Reemplazabilidad**: Si en el futuro se desea cambiar la implementación de Redis (ej: usar otra estrategia de caché), será fácil hacerlo sin afectar la lógica de negocio.

### Detalles de Implementación

#### MetricsRedisAdapter

- **Responsabilidad**: Adaptar las operaciones de Redis para almacenamiento y recuperación de métricas
- **Métodos principales**:
  - `saveMetrics()`: Guarda métricas en Redis con TTL
  - `getMetrics()`: Recupera métricas en caché
  - `deleteMetrics()`: Elimina métricas del caché
  - `exists()`: Verifica existencia de métricas en caché

#### Pattern de Uso: Cache-Aside

El `MetricsRedisAdapter` implementa el patrón cache-aside:

```java
// En MetricsReportingServiceImpl
MetricsAttendanceDto metrics = metricsRedisAdapter.getMetrics(eventType, contextId)
    .orElseGet(() -> buildingChurchMetrics(contextId, startTime, endTime));

metricsRedisAdapter.saveMetrics(eventType, contextId, metrics, Duration.ofMinutes(20));
```

### Notas de Compatibilidad

- **Cambios Públicos**: Ninguno. El servicio `MetricsReportingService` mantiene la misma interfaz pública.
- **Cambios Internos**: Solo la ubicación e inyección de dependencias del adaptador de Redis.
- **Migración**: La migración es completamente transparente para los clientes de `MetricsReportingService`.

### Verificación

Todos los cambios han sido compilados y validados sin errores. El proyecto compila exitosamente:

```bash
mvn clean compile -q
```

### Futuras Mejoras Opcionales

Si se requiere abstraer aún más la dependencia de Redis, se podría introducir una interfaz:

```java
public interface MetricsCache {
    void saveMetrics(TopologyEventType eventType, UUID contextId, 
                     MetricsAttendanceDto metrics, Duration ttl);
    Optional<MetricsAttendanceDto> getMetrics(TopologyEventType eventType, UUID contextId);
    void deleteMetrics(TopologyEventType eventType, UUID contextId);
    boolean exists(TopologyEventType eventType, UUID contextId);
}
```

Sin embargo, **no se recomienda** a menos que sea necesario soportar múltiples implementaciones de caché en el mismo proyecto.

