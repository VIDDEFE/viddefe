# Arquitectura de Dependencias - Viddefe API

## 📋 Resumen Ejecutivo

Este documento describe la arquitectura refactorizada para eliminar ciclos de dependencia potenciales y mejorar la mantenibilidad del código.

## 🎯 Problema Original

El proyecto tenía servicios con **responsabilidades mixtas** que generaban riesgo de ciclos:

```
┌─────────────────────────────────────────────────────────────────┐
│ ANTES: PeopleLookup tenía métodos de lectura Y escritura        │
│                                                                  │
│   PeopleLookup (interface)                                       │
│   ├── getPeopleById(UUID)          → Lectura                    │
│   ├── getPastorByCcWithoutChurch() → Lectura                    │
│   ├── save(PeopleDTO)              → Escritura (usa ChurchLookup)│
│   └── enrollPersonToChurch()       → Escritura (usa Church)      │
│                                                                  │
│   Esto violaba ISP y creaba acoplamiento innecesario            │
└─────────────────────────────────────────────────────────────────┘
```

## ✅ Solución Implementada

### Principios Aplicados

1. **ISP (Interface Segregation Principle)**: Interfaces pequeñas y específicas
2. **CQRS Light**: Separación de operaciones de lectura y escritura
3. **Single Responsibility**: Cada servicio tiene una responsabilidad clara
4. **Dependency Inversion**: Dependemos de abstracciones, no de implementaciones

### Nueva Estructura de Interfaces

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        NUEVA ARQUITECTURA                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐  │
│  │  PeopleReader   │    │  PeopleWriter   │    │ ChurchMembershipService │  │
│  │  (Solo Lectura) │    │ (Solo Escritura)│    │   (Membresía a Iglesia) │  │
│  ├─────────────────┤    ├─────────────────┤    ├─────────────────────────┤  │
│  │ getPeopleById() │    │ createPerson()  │    │ assignToChurchAsPastor()│  │
│  │ findPeopleById()│    │ updatePerson()  │    │ assignToChurch()        │  │
│  │ getPastorByCC() │    │ deletePerson()  │    │ removeChurchAssignment()│  │
│  │ existsPastor()  │    │                 │    │ transferToChurch()      │  │
│  └────────┬────────┘    └────────┬────────┘    └───────────┬─────────────┘  │
│           │                      │                         │                 │
│           ▼                      ▼                         ▼                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐  │
│  │PeopleReaderImpl │    │PeopleWriterImpl │    │ChurchMembershipServiceImpl│ │
│  ├─────────────────┤    ├─────────────────┤    ├─────────────────────────┤  │
│  │ - PeopleRepo    │    │ - PeopleRepo    │    │ - PeopleRepo            │  │
│  │ - PeopleType    │    │ - PeopleType    │    │ - PeopleType            │  │
│  │                 │    │ - StatesCities  │    │ - ChurchLookup (READ)   │  │
│  │ NO CROSS-DOMAIN │    │ - ChurchLookup  │    │                         │  │
│  │   DEPENDENCIES  │    │   (READ ONLY)   │    │                         │  │
│  └─────────────────┘    └─────────────────┘    └─────────────────────────┘  │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Grafo de Dependencias Seguro

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                          FLUJO DE DEPENDENCIAS                                │
│                     (Sin posibilidad de ciclos)                               │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  Nivel 0 (Base - Solo Repositorios):                                         │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐            │
│  │ PeopleTypeService│  │StatesCitiesService│ │ ChurchLookupImpl │            │
│  │   (PeopleType    │  │  (Cities/States  │  │  (ChurchRepo)    │            │
│  │    Repository)   │  │   Repository)    │  │                  │            │
│  └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘            │
│           │                     │                     │                       │
│           ▼                     ▼                     ▼                       │
│  Nivel 1 (Lectura):                                                          │
│  ┌──────────────────────────────────────────────────────────────┐            │
│  │                      PeopleReaderImpl                         │            │
│  │         (PeopleRepository + PeopleTypeService)                │            │
│  │              ★ NO DEPENDE DE CHURCH DOMAIN ★                  │            │
│  └───────────────────────────┬──────────────────────────────────┘            │
│                              │                                                │
│                              ▼                                                │
│  Nivel 2 (Escritura):                                                        │
│  ┌────────────────────────┐  ┌────────────────────────────────────┐          │
│  │    PeopleWriterImpl    │  │   ChurchMembershipServiceImpl      │          │
│  │  (Repo + Types +       │  │   (Repo + Types + ChurchLookup)    │          │
│  │   StatesCities +       │  │                                    │          │
│  │   ChurchLookup)        │  │   ★ ChurchLookup es SOLO LECTURA ★ │          │
│  └───────────┬────────────┘  └──────────────┬─────────────────────┘          │
│              │                              │                                 │
│              ▼                              ▼                                 │
│  Nivel 3 (Negocio Cross-Domain):                                             │
│  ┌──────────────────────────────────────────────────────────────┐            │
│  │                    ChurchPastorImpl                           │            │
│  │        (PeopleReader + ChurchMembershipService)               │            │
│  │                                                               │            │
│  │    ★ USA SOLO INTERFACES SEGREGADAS - NO HAY CICLO ★         │            │
│  └───────────────────────────┬──────────────────────────────────┘            │
│                              │                                                │
│                              ▼                                                │
│  Nivel 4 (Orquestación):                                                     │
│  ┌──────────────────────────┐  ┌──────────────────────────────────┐          │
│  │    ChurchServiceImpl     │  │       AuthServiceImpl            │          │
│  │  (ChurchPastorService)   │  │  (PeopleReader + PeopleWriter)   │          │
│  └──────────────────────────┘  └──────────────────────────────────┘          │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

## 📁 Archivos Creados/Modificados

### Nuevas Interfaces

| Archivo | Propósito |
|---------|-----------|
| `people/contracts/PeopleReader.java` | Operaciones de solo lectura sobre personas |
| `people/contracts/PeopleWriter.java` | Operaciones de escritura sobre personas |
| `people/contracts/ChurchMembershipService.java` | Gestión de membresía persona-iglesia |

### Nuevas Implementaciones

| Archivo | Dependencias | Nivel |
|---------|--------------|-------|
| `PeopleReaderImpl.java` | PeopleRepository, PeopleTypeService | 1 (Lectura) |
| `PeopleWriterImpl.java` | PeopleRepo, Types, StatesCities, ChurchLookup | 2 (Escritura) |
| `ChurchMembershipServiceImpl.java` | PeopleRepo, Types, ChurchLookup | 2 (Escritura) |

### Clases Modificadas

| Archivo | Cambio |
|---------|--------|
| `ChurchPastorImpl.java` | Usa `PeopleReader` + `ChurchMembershipService` en lugar de `PeopleLookup` |
| `AuthServiceImpl.java` | Usa `PeopleReader` + `PeopleWriter` en lugar de `PeopleLookup` |
| `PeopleServiceImpl.java` | Delega a `PeopleReader` + `PeopleWriter` |
| `PeopleLookupImpl.java` | Marcado `@Deprecated`, delega a nuevas interfaces |

## 🧪 Tests Añadidos

| Test | Valida |
|------|--------|
| `ChurchMembershipServiceImplTest` | Operaciones de membresía funcionan correctamente |
| `PeopleReaderImplTest` | Operaciones de lectura funcionan y lanzan excepciones apropiadas |
| `NoCyclicDependenciesTest` | Valida estructuralmente que no hay ciclos |

## 📋 Estrategia de Migración

### Fase 1: Completada ✅
- Crear interfaces segregadas
- Crear implementaciones
- Actualizar servicios principales

### Fase 2: Recomendada
1. Deprecar `PeopleLookup` interface gradualmente
2. Actualizar controladores para usar interfaces específicas
3. Remover la clase `PeopleLookupImpl` cuando no tenga usages

### Fase 3: Opcional
- Aplicar el mismo patrón a `ChurchService` si crece
- Considerar módulos de Maven para separación física

## ⚡ Beneficios Obtenidos

| Aspecto | Antes | Después |
|---------|-------|---------|
| Ciclos potenciales | Sí (PeopleLookup mezclaba responsabilidades) | No |
| Testabilidad | Difícil (dependencias amplias) | Fácil (interfaces pequeñas) |
| Mantenibilidad | Cambio en lectura afecta escritura | Independientes |
| Extensibilidad | Modificar existente | Añadir nuevas implementaciones |
| Spring Boot compatibility | Funciona | Funciona igual |

## 🔧 Uso Recomendado

```java
// Para operaciones de solo lectura
@RequiredArgsConstructor
public class MyReadOnlyService {
    private final PeopleReader peopleReader;  // ✅ Solo lo que necesita
    
    public PersonDto getInfo(UUID id) {
        return peopleReader.getPeopleById(id).toDto();
    }
}

// Para crear/actualizar personas
@RequiredArgsConstructor
public class MyWriteService {
    private final PeopleWriter peopleWriter;  // ✅ Solo lo que necesita
    
    public PersonDto create(PeopleDTO dto) {
        return peopleWriter.createPerson(dto).toDto();
    }
}

// Para gestionar membresía a iglesias
@RequiredArgsConstructor
public class MyChurchService {
    private final ChurchMembershipService membershipService;  // ✅ Específico
    
    public void assignPastor(UUID personId, UUID churchId) {
        membershipService.assignPersonToChurchAsPastor(personId, churchId);
    }
}
```

## 📊 Compatibilidad

| Spring Boot | Estado |
|-------------|--------|
| 2.7.x | ✅ Compatible |
| 3.0.x | ✅ Compatible |
| 3.1.x | ✅ Compatible |
| 3.2.x | ✅ Compatible |

Las anotaciones usadas (`@Service`, `@RequiredArgsConstructor`, `@Transactional`) son estándar y compatibles con todas las versiones.
