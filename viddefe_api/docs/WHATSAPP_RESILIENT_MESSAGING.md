# 🚀 Estrategia de Mensajería Resiliente para WhatsApp

## 📋 Resumen Ejecutivo

Se ha implementado una estrategia completa de mensajería resiliente para notificaciones de WhatsApp que combina:
- **RabbitMQ** para manejo de reintentos con TTL y Dead Letter Queues
- **Resilience4j** para Circuit Breaker y protección contra fallas de red
- **Separación de responsabilidades** entre capas de resiliencia
- **Manejo granular de errores** (retryables vs no-retryables)

## 🏗️ Arquitectura del Sistema

### Flujo de Mensajes

```
1. Notificación → WhatsappNotifierService → whatsapp.queue
2. whatsapp.queue → WhatsappMessageListener → WhatsappClient (con Circuit Breaker)
3. En caso de error:
   a) Error transitorio → whatsapp.retry.queue (TTL 30s) → vuelta a whatsapp.queue
   b) Error no recuperable → whatsapp.dlq
   c) Máximo reintentos → whatsapp.dlq
```

### Componentes Implementados

#### 1. **RabbitMQ Configuration** (`RabbitConfig.java`)
- **whatsapp.exchange**: Exchange principal
- **whatsapp.queue**: Cola principal con DLX configurado
- **whatsapp.retry.queue**: Cola de reintentos con TTL de 30 segundos
- **whatsapp.dlq**: Dead Letter Queue para mensajes fallidos

#### 2. **WhatsApp Client Resiliente** (`WhatsappClient.java`)
- Circuit Breaker con Resilience4j
- Manejo tipado de errores HTTP
- Clasificación automática: retryable vs no-retryable

#### 3. **Listener Resiliente** (`WhatsappMessageListener.java`)
- Máximo 3 reintentos por mensaje
- Manejo de excepciones tipadas
- Orchestration sin lógica de negocio pesada

#### 4. **DTOs y Excepciones**
- `WhatsappMessageDto`: Incluye metadatos de retry
- `RetryableWhatsappException`: Errores 5xx, timeouts, 429
- `NonRetryableWhatsappException`: Errores 4xx, auth, payload inválido

## 🔧 Configuración

### Dependencies (pom.xml)
```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-circuitbreaker</artifactId>
    <version>2.1.0</version>
</dependency>
```

### Application Properties
```properties
# RabbitMQ Resilient Configuration
spring.rabbitmq.listener.simple.acknowledge-mode=auto
spring.rabbitmq.listener.simple.retry.enabled=false

# Resilience4j Circuit Breaker
resilience4j.circuitbreaker.instances.whatsapp-api.register-health-indicator=true
resilience4j.circuitbreaker.instances.whatsapp-api.sliding-window-size=10
resilience4j.circuitbreaker.instances.whatsapp-api.minimum-number-of-calls=5
resilience4j.circuitbreaker.instances.whatsapp-api.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.whatsapp-api.wait-duration-in-open-state=30s
```

## 📊 Decisiones Arquitectónicas

### 1. **Separación de Responsabilidades**
- **RabbitMQ**: Reintentos con delay (TTL de 30s)
- **Resilience4j**: Fallas inmediatas de I/O (2-3 reintentos rápidos)
- **Sin duplicación**: Evita doble retry entre sistemas

### 2. **TTL de 30 segundos**
- Balance entre velocidad de reintento y no sobrecargar WhatsApp API
- Permite recuperación de fallas transitorias sin ser agresivo

### 3. **Máximo 3 reintentos**
- Evita bucles infinitos
- Mantiene disponibilidad del sistema
- Compatible con SLAs de WhatsApp

### 4. **Circuit Breaker**
- Protege contra cascadas de fallas
- Se abre con 50% de falla en 10 llamadas
- Período de recuperación de 30 segundos

### 5. **DLQ con metadatos**
- Facilita debugging y análisis
- Incluye correlationId para trazabilidad
- Permite reprocesamiento manual si es necesario

## 🔍 Manejo de Errores

### Errores Retryables
- **5xx**: Errores del servidor de WhatsApp
- **429**: Rate limiting
- **408**: Request timeout
- **503**: Service unavailable
- **Network errors**: Timeout de conexión

### Errores No Retryables
- **4xx**: Errores de cliente (excepto 408, 429)
- **401/403**: Errores de autenticación
- **400**: Payload inválido

## 📈 Métricas y Monitoreo

### Circuit Breaker Health Check
```
GET /actuator/health/circuitBreakers
```

### Logs Estructurados
- Todos los reintentos se loggean con nivel WARN
- DLQ se loggea con nivel ERROR
- Incluye correlationId para trazabilidad

### DLQ Monitoring
- `WhatsappDlqListener` procesa mensajes fallidos
- Logs detallados para análisis posterior
- Base para integración con sistemas de alertas

## 🚦 Estado del Sistema

### ✅ Implementado
- [x] Colas RabbitMQ con TTL y DLX
- [x] Circuit Breaker con Resilience4j
- [x] Manejo granular de errores
- [x] Listener resiliente
- [x] DLQ monitoring
- [x] Configuración completa
- [x] Documentación

### 🔄 Integración Existente
- [x] `WhatsappNotifierService` actualizado para usar colas
- [x] Mantiene compatibilidad con `NotificationEvent`
- [x] No rompe funcionalidad existente

## 🎯 Beneficios de la Implementación

1. **Resiliencia**: Sin pérdida de mensajes
2. **Observabilidad**: Logs estructurados y métricas
3. **Escalabilidad**: Manejo asíncrono con backpressure
4. **Mantenibilidad**: Código limpio y bien separado
5. **Compatibilidad**: No rompe funcionalidad existente
6. **Productividad**: Fácil de operar y debuggear

## 🔧 Uso

### Envío Directo (nuevo)
```java
@Autowired
private WhatsappMessagingService whatsappService;

whatsappService.sendSimpleMessage("+573001234567", "Hola mundo");
```

### Envío via Notificaciones (existente)
```java
// Sigue funcionando igual que antes
notificationService.send(notificationDto);
```

La implementación es **backward-compatible** y mejora la resiliencia sin romper código existente.
