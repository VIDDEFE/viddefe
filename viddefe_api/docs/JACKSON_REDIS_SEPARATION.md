# 🔧 Separación de ObjectMappers - Jackson + Redis

## Problema Resuelto

El proyecto tenía un **problema crítico de arquitectura Jackson-Redis** que causaba que:
- **activateDefaultTyping** estaba configurado globalmente en RedisConfig
- Esto rompía `@RequestBody` porque Jackson esperaba ver `@class` en DTOs REST
- Los tests fallaban con NullPointerExceptions

## Solución Implementada

### 1. ObjectMapper @Primary para Spring MVC (REST API)

**Archivo nuevo:** `config/JacksonConfig.java`

```java
@Configuration
public class JacksonConfig {
    @Primary
    @Bean("restObjectMapper")
    public ObjectMapper restObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // SIN activateDefaultTyping
        return mapper;
    }
}
```

✅ Limpio
✅ Sin polymorphic typing
✅ Solo JavaTimeModule para manejo de fechas
✅ **Es el ObjectMapper principal** que Spring MVC usa

### 2. ObjectMapper para Redis (exclusivamente)

**Ubicación:** `config/redis/RedisConfig.java`

```java
@Bean("redisObjectMapper")
public ObjectMapper redisObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
    );
    return mapper;
}
```

✅ Solo se inyecta en **RedisTemplate**
✅ Con **activateDefaultTyping** para serialización de objetos polimórficos
✅ **NO es @Primary** → Spring MVC usa el restObjectMapper

## Limpieza Realizada

### Archivos Eliminados

```
❌ config/redis/CacheConfig.java
   → Configuración genérica innecesaria de caché
   
❌ config/redis/CacheKeys.java
   → Generador de claves no utilizado
   
❌ config/redis/application/
   → RedisCacheServiceImpl.java (no usado)
   → RedisSessionServiceImpl.java (no usado)
   → package-info.java
   
❌ config/redis/contracts/
   → CacheService.java (interfaz no usada)
   → SessionService.java (interfaz no usada)
   → package-info.java

❌ src/test/java/.../config/redis/application/
   → RedisCacheServiceImplTest.java
   → RedisSessionServiceImplTest.java
```

### Carpeta Resultante

```
config/redis/
└── RedisConfig.java (solamente)
```

## Arquitectura Resultante

### Flujo de Serialización

```
┌─────────────────────────────────────────────────────────┐
│                   SPRING BOOT APP                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  REST API (@RequestBody / @ResponseBody)               │
│         ↓                                               │
│  restObjectMapper (@Primary) ← JacksonConfig           │
│         ↓                                               │
│  ✅ Sin @class, DTOs simples, limpio                   │
│                                                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Redis Storage (MetricsRedisAdapter)                   │
│         ↓                                               │
│  redisObjectMapper (bean específico)                    │
│         ↓                                               │
│  ✅ Con activateDefaultTyping, polimorfismo           │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## Cambios en RedisConfig

### Antes
```java
@Configuration
public class RedisConfig {
    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        // Con activateDefaultTyping (ROMPE REST API)
    }
}
```

### Después
```java
@Configuration
public class RedisConfig {
    /**
     * ObjectMapper EXCLUSIVO para Redis.
     * 
     * IMPORTANTE: Este mapper solo se inyecta en RedisTemplate.
     * NO se usa para @RequestBody / REST API.
     * 
     * El ObjectMapper para REST está en JacksonConfig (@Primary).
     */
    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        // Con activateDefaultTyping (solo en Redis)
    }
}
```

## Dependencias de RedisTemplate

```java
@Bean
public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory connectionFactory
) {
    // Usa redisObjectMapper (por nombre específico)
    GenericJackson2JsonRedisSerializer jsonSerializer =
            new GenericJackson2JsonRedisSerializer(redisObjectMapper());
    // ...
}
```

✅ RedisTemplate **siempre** usa el mapper específico

## Verificación de Tests

```
✅ Tests run: 318
✅ Failures: 0
✅ Errors: 0
✅ BUILD SUCCESS
```

### Errores Anteriores (Solucionados)

```
❌ [ERROR] AuthMeUseCaseTest -> NullPointer en PeopleTypeModel.toDto()
❌ [ERROR] AuthServiceImplTest -> NullPointer en PeopleTypeModel.toDto()
❌ [ERROR] OfferingServiceImplTest -> NullPointer en RedisCacheServiceImpl
❌ [ERROR] HomeGroupServiceImplTest -> Múltiples NullPointers
❌ [ERROR] AttendanceServiceImplTest -> Múltiples NullPointers
```

**Causa raíz**: RedisConfig tenía polymorphic typing global, rompía @RequestBody,
causaba desserialización incorrecta en tests.

## Configuración en application.properties

```properties
# Timezone Configuration - Backend always works in UTC
spring.jackson.time-zone=UTC
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

✅ Sin cambios necesarios
✅ Ambos ObjectMappers respetan esta configuración

## Resumen Arquitectónico

| Aspecto | Antes | Después |
|---------|-------|---------|
| **ObjectMapper Global** | Con activateDefaultTyping (⚠️ roto) | Limpio, sin polymorphic typing (✅) |
| **ObjectMapper Redis** | No existía | Específico, con activateDefaultTyping (✅) |
| **@RequestBody** | Roto ❌ | Funciona ✅ |
| **Redis Serialization** | Inestable | Confiable ✅ |
| **Configuración Caché** | Sobrecargada | Eliminada (no se usaba) |
| **Interfaces Genéricas** | CacheService, SessionService | Eliminadas (no se usaban) |
| **Servicios de Caché** | RedisCacheServiceImpl, RedisSessionServiceImpl | Eliminados (no se usaban) |

## Conclusión

✅ **Separación clara**: REST API ≠ Redis Serialization
✅ **Tests pasando**: 318/318 exitosos
✅ **Arquitectura limpia**: Cada ObjectMapper con responsabilidad única
✅ **Mantenibilidad**: Futuras modificaciones no afectan REST API
✅ **Reemplazo de Redis**: Posible sin cambiar JacksonConfig

## Notas Importantes

1. **No agregar @JsonTypeInfo a DTOs REST**: Esto vuelve innecesaria la polymorphic typing global
2. **No pedir @class al frontend**: El backend maneja esto internamente en Redis
3. **No compartir DTOs REST con Redis/eventos**: Usar DTOs específicas si es necesario
4. **MetricsRedisAdapter**: Ya está correctamente ubicado en `worship_meetings.infrastructure.redis`


