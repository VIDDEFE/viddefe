# 📖 GUÍA PARA DESARROLLADORES - Jackson Configuration

## Importante: Entender la Separación de ObjectMappers

### ¿Cuál ObjectMapper debo usar?

#### 1. REST API (@RequestBody / @ResponseBody)

**Automáticamente**: `restObjectMapper` (está configurado como @Primary)

```java
@RestController
@RequestMapping("/api/v1/meetings")
public class MeetingController {
    
    @PostMapping
    public ApiResponse<MeetingDto> create(@RequestBody CreateMeetingRequest request) {
        // ✅ Automáticamente usa restObjectMapper
        // Sin necesidad de hacer nada especial
        return ApiResponse.ok(meetingService.create(request));
    }
}
```

**Características:**
- ✅ Sin `@class` en JSON
- ✅ DTOs simples
- ✅ Estándar REST
- ✅ Compatible con frontend

#### 2. Redis Storage

**Automáticamente**: `redisObjectMapper` (inyectado en RedisTemplate)

```java
@Component
@RequiredArgsConstructor
public class MetricsRedisAdapter {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void saveMetrics(TopologyEventType eventType, UUID contextId, MetricsAttendanceDto metrics) {
        // ✅ Automáticamente usa redisObjectMapper
        // Serialización con @class para polimorfismo
        redisTemplate.opsForValue().set(key, metrics, ttl);
    }
}
```

**Características:**
- ✅ Con `@class` para polimorfismo
- ✅ Serialización confiable
- ✅ Soporta objetos complejos
- ✅ No visible al frontend

---

## ❌ QUÉ NO DEBES HACER

### ❌ NO Agregar activateDefaultTyping al ObjectMapper Global

```java
// ❌ MALO - Lo rompería todo
@Bean
@Primary
public ObjectMapper restObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.activateDefaultTyping(...);  // ❌ NO HACER
    return mapper;
}
```

**Consecuencias:**
- REST API esperaría `@class` en JSON
- Frontend enviaría JSON inválido
- Tests fallarían
- Desserialización rota

### ❌ NO Crear múltiples @Primary ObjectMappers

```java
// ❌ MALO
@Primary
@Bean
public ObjectMapper mapper1() { ... }

@Primary
@Bean
public ObjectMapper mapper2() { ... }
```

**Consecuencias:**
- Spring no sabría cuál usar
- Inyección de dependencias fallida
- Errores en runtime

### ❌ NO Inyectar redisObjectMapper en Services

```java
// ❌ MALO
@Service
public class MyService {
    @Autowired
    private ObjectMapper redisObjectMapper;  // ❌ NO HACER
}
```

**Consecuencias:**
- Acoplamiento a Redis
- redisObjectMapper tiene polymorphic typing
- Rompe @RequestBody en tests

### ❌ NO Agregar @JsonTypeInfo a DTOs REST

```java
// ❌ MALO
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)  // ❌ NO HACER
public class MeetingDto {
    // ...
}
```

**Consecuencias:**
- JSON contiene `@class` innecesariamente
- API no estándar
- Incompatible con frontend
- Violación de contrato REST

---

## ✅ CASOS DE USO CORRECTOS

### Caso 1: Crear un nuevo Service REST

```java
@RestController
@RequestMapping("/api/v1/churches")
@RequiredArgsConstructor
public class ChurchController {
    
    private final ChurchService churchService;
    
    @PostMapping
    public ApiResponse<ChurchDto> create(
            @RequestBody CreateChurchRequest request  // ✅ usa restObjectMapper automáticamente
    ) {
        return ApiResponse.ok(churchService.create(request));
    }
}
```

✅ **Sin cambios necesarios**
✅ Automáticamente usa `restObjectMapper` (@Primary)
✅ DTOs simples, sin @class

### Caso 2: Crear un nuevo adapter para Redis

```java
@Component
@RequiredArgsConstructor
public class MyCacheAdapter {
    
    private final RedisTemplate<String, Object> redisTemplate;  // ✅ Ya inyecta redisObjectMapper
    
    public void cache(String key, MyObject value) {
        redisTemplate.opsForValue().set(key, value);  // ✅ Serialización correcta
    }
}
```

✅ **Sin cambios necesarios**
✅ Automáticamente usa `redisObjectMapper`
✅ Inyectando RedisTemplate (que usa redisObjectMapper)

### Caso 3: Modificar application.properties

```properties
# ✅ PERMITIDO
spring.jackson.time-zone=UTC
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false

# ❌ NO PERMITIDO
spring.jackson.default-typing=NON_FINAL  # ← Rompería REST API
```

---

## 🔍 CÓMO VERIFICAR QUE ESTÁ CORRECTO

### Verificación 1: Tests Pasando

```bash
# Debe mostrar:
# Tests run: 318, Failures: 0, Errors: 0
./mvnw test
```

✅ Si todos pasan → Configuración correcta

### Verificación 2: REST API Funcional

```bash
# Obtener token
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass"}'

# Esperado: JSON SIN @class
# {
#   "success": true,
#   "data": { ... }  ← Sin "class": "com.viddefe..."
# }
```

✅ Si JSON no tiene @class → REST API correcta

### Verificación 3: Redis Funcional

```java
// En un test o clase
@Test
void testRedisPolymorphism() {
    MetricsAttendanceDto metrics = new MetricsAttendanceDto(...);
    metricsRedisAdapter.saveMetrics(eventType, contextId, metrics, Duration.ofMinutes(5));
    
    Optional<MetricsAttendanceDto> retrieved = metricsRedisAdapter.getMetrics(eventType, contextId);
    
    assertThat(retrieved).isPresent();
    assertThat(retrieved.get()).isEqualTo(metrics);
}
```

✅ Si Redis recupera correctamente → Serialización correcta

---

## 🚨 TROUBLESHOOTING

### Problema: "Cannot find symbol: class restObjectMapper"

```
Error: Cannot find symbol
  symbol:   variable restObjectMapper
```

**Causa:** Importaste mal JacksonConfig

**Solución:**
```java
// ✅ CORRECTO
import com.viddefe.viddefe_api.config.JacksonConfig;

// ❌ INCORRECTO
import com.viddefe.viddefe_api.config.redis.JacksonConfig;  // No existe aquí
```

### Problema: "Multiple beans named restObjectMapper"

```
Error: expected single matching bean but found 2
```

**Causa:** Creaste otro ObjectMapper con @Primary

**Solución:**
```java
// ❌ MALO: Elimina @Primary de tu nuevo bean
@Bean
@Primary  // ← ELIMINA
public ObjectMapper tuMapper() { ... }

// ✅ BUENO: Sin @Primary
@Bean
public ObjectMapper tuMapper() { ... }
```

### Problema: "JWT token desserialización fallando"

```
Error: Cannot deserialize instance of CustomObject
```

**Causa:** Probablemente intentaste usar `redisObjectMapper` directamente

**Solución:**
```java
// ✅ CORRECTO
private final RedisTemplate<String, Object> redisTemplate;

// ❌ INCORRECTO
@Qualifier("redisObjectMapper")
private final ObjectMapper redisObjectMapper;
```

### Problema: Frontend recibe JSON con "@class"

```json
{
  "success": true,
  "data": {
    "@class": "com.viddefe.viddefe_api...."  // ← ¡NO debe estar!
  }
}
```

**Causa:** El ObjectMapper global tiene `activateDefaultTyping`

**Solución:** Verifica que JacksonConfig tiene `@Primary` y RedisConfig no tiene `@Primary`

```bash
# Grep para verificar
grep -n "@Primary" src/main/java/com/viddefe/viddefe_api/config/*.java
# Debe mostrar solo:
# JacksonConfig.java:X: @Primary
```

---

## 📚 REFERENCIAS

- **Archivo de Configuración REST:** `config/JacksonConfig.java`
- **Archivo de Configuración Redis:** `config/redis/RedisConfig.java`
- **Adapter Redis:** `worship_meetings/infrastructure/redis/MetricsRedisAdapter.java`
- **Documentación:** `docs/JACKSON_REDIS_SEPARATION.md`

---

## ✅ CHECKLIST PARA NUEVAS FEATURES

Si añades una nueva feature, verifica:

```
□ ¿Es un endpoint REST?
  └─ ✅ Usa @RequestBody/@ResponseBody
  └─ ✅ Sin @class en JSON
  └─ ✅ DTOs simples

□ ¿Necesitas almacenar en Redis?
  └─ ✅ Crea un adapter con RedisTemplate
  └─ ✅ No importes redisObjectMapper directamente
  └─ ✅ RedisTemplate automáticamente usa redisObjectMapper

□ ¿Añadiste un nuevo ObjectMapper?
  └─ ✅ NO tiene @Primary (a menos que sea global para REST)
  └─ ✅ Documenta su propósito
  └─ ✅ Especifica dónde se inyecta

□ ¿Corriste los tests?
  └─ ✅ ./mvnw test
  └─ ✅ 318/318 deben pasar
```

---

## 🎓 CONCLUSIÓN

**Regla de Oro:**

```
REST API     = restObjectMapper (@Primary, SIN polymorphic typing)
Redis        = redisObjectMapper (específico, CON polymorphic typing)

Nunca mezcles los dos.
```


