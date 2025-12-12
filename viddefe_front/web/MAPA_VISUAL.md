# 🎨 Mapa Visual del Proyecto VIDDEFE

## 📱 Estructura de Navegación

```
┌─────────────────────────────────────────────────────┐
│                    VIDDEFE                          │
│         Sistema de Gestión de Iglesias             │
└─────────────────────────────────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
    [ /signin ]       [ / → /dashboard ]    
    (Login)           (Home)
        
        ┌───────────────────────────────────────────────────┐
        │                     LAYOUT                        │
        │  ┌──────────────────────────────────────────────┐ │
        │  │              ASIDE (Sidebar)                 │ │
        │  │  ⛪ VIDDEFE                                  │ │
        │  │  📊 Dashboard                               │ │
        │  │  ⛪ Iglesias                                │ │
        │  │  👥 Personas                                │ │
        │  │  🙏 Servicios                               │ │
        │  │  👫 Grupos                                  │ │
        │  │  📅 Eventos                                 │ │
        │  │                                              │ │
        │  │  [Cerrar Sesión]                            │ │
        │  └──────────────────────────────────────────────┘ │
        │                                                    │
        │  ┌──────────────────────────────────────────────┐ │
        │  │           MAIN CONTENT                       │ │
        │  │                                              │ │
        │  │  ┌─ Dashboard ────────────────────────────┐ │ │
        │  │  │ 📊 Stats: 5 | 245 | 12 | 8            │ │ │
        │  │  │ ┌─ Acciones Rápidas ────────────────┐ │ │ │
        │  │  │ │ [Nueva Iglesia] [Nueva Persona]  │ │ │ │
        │  │  │ │ [Nuevo Servicio] [Nuevo Grupo]   │ │ │ │
        │  │  │ └────────────────────────────────────┘ │ │ │
        │  │  │ ┌─ Actividad Reciente ───────────────┐ │ │ │
        │  │  │ │ • Se agregó a Juan Pérez         │ │ │ │
        │  │  │ │ • Nuevo servicio programado      │ │ │ │
        │  │  │ │ • Grupo de oración creado        │ │ │ │
        │  │  │ └────────────────────────────────────┘ │ │ │
        │  │  └────────────────────────────────────────┘ │ │
        │  │                                              │ │
        │  │  ┌─ Iglesias ─────────────────────────────┐ │ │
        │  │  │ [+ Nueva Iglesia]                      │ │ │
        │  │  │ ┌─ Tabla ──────────────────────────┐   │ │ │
        │  │  │ │ Nombre | Ciudad | Pastor | Miem │   │ │ │
        │  │  │ ├───────────────────────────────────┤   │ │ │
        │  │  │ │ Iglesia Central | Madrid | Juan  │   │ │ │
        │  │  │ └────────────────────────────────────┘   │ │ │
        │  │  └────────────────────────────────────────┘ │ │
        │  │                                              │ │
        │  │  [Modal: Agregar Nueva Iglesia]            │ │
        │  │  ┌────────────────────────────────────┐    │ │
        │  │  │ Nombre: [_________]                │    │ │
        │  │  │ Dirección: [_________]             │    │ │
        │  │  │ Pastor: [_________]                │    │ │
        │  │  │ Email: [_________]                 │    │ │
        │  │  │ [Guardar] [Cancelar]               │    │ │
        │  │  └────────────────────────────────────┘    │ │
        │  │                                              │ │
        │  └──────────────────────────────────────────────┘ │
        │                                                    │
        └───────────────────────────────────────────────────┘
```

## 🎯 Flujo de Componentes

```
┌─────────────────────────────────┐
│         App.tsx                 │
│      (Router Setup)             │
└──────────────┬──────────────────┘
               │
        ┌──────┴──────┐
        │             │
        ▼             ▼
    [SignIn]    [Layout]
                  │
        ┌─────────┼──────────┬──────────┬─────────┬─────────┐
        │         │          │          │         │         │
        ▼         ▼          ▼          ▼         ▼         ▼
    Dashboard Iglesias Personas Servicios Grupos Eventos
       │          │         │          │         │        │
       ├─→ Stats  ├─→ Table ├─→ Table  ├─→Table ├─→Table ├─→ Table
       ├─→ Cards  ├─→ Modal ├─→ Modal  ├─→Modal ├─→Modal ├─→ Modal
       └─→ List   └─→ Form  └─→ Form   └─→Form  └─→Form  └─→ Form

Componentes Compartidos:
  Button → Utilizado en todas las vistas
  Card → Utilizado en Dashboard y modales
  Form → Utilizado en todos los modales
  Table → Utilizado en todas las vistas de datos
  Modal → Utilizado en todas las vistas
  PageHeader → Utilizado en todas las vistas
```

## 🔄 Flujo de Datos

```
Vista (React Component)
    │
    ├─→ useState (Estado Local)
    │
    ├─→ useForm (Formularios)
    │
    ├─→ useModal (Modales)
    │
    ├─→ useAppContext (Estado Global)
    │
    └─→ servicios (API)
            │
            ├─→ churchService
            ├─→ personService
            ├─→ serviceService
            ├─→ groupService
            └─→ eventService
                │
                └─→ apiService (HTTP Client)
                    │
                    └─→ Backend API
```

## 🎨 Sistema de Estilos

```
CSS Hierarchy:
│
├─ index.css (Estilos Globales)
│  ├─ Reset (*)
│  ├─ Root variables
│  ├─ Typography
│  ├─ Scrollbar
│  └─ General styles
│
└─ App.css (Component Styles)
   ├─ .app-layout (Contenedor principal)
   ├─ .aside (Barra lateral)
   ├─ .main-content (Contenido principal)
   ├─ .page-container (Contenedor de página)
   ├─ .button (Botones)
   ├─ .form (Formularios)
   ├─ .table (Tablas)
   ├─ .modal (Modales)
   ├─ .card (Tarjetas)
   ├─ .stats-grid (Grid de estadísticas)
   └─ .signin-container (Página de login)
```

## 📦 Archivos por Tipo

```
src/
├── 🎯 Componentes (7 archivos)
│   ├── Button.tsx
│   ├── Card.tsx
│   ├── Form.tsx
│   ├── Table.tsx
│   ├── Modal.tsx
│   ├── PageHeader.tsx
│   ├── Layout.tsx
│   └── Aside.tsx
│
├── 📄 Vistas (6 archivos)
│   ├── Dashboard.tsx
│   ├── Churches.tsx
│   ├── People.tsx
│   ├── Services.tsx
│   ├── Groups.tsx
│   ├── Events.tsx
│   └── SignIn.tsx
│
├── 🔧 Servicios (6 archivos)
│   ├── api.ts
│   ├── churchService.ts
│   ├── personService.ts
│   ├── serviceService.ts
│   ├── groupService.ts
│   └── eventService.ts
│
├── 🪝 Hooks (1 archivo)
│   └── useCustom.ts
│
├── 📚 Modelos (1 archivo)
│   └── types.ts
│
├── 🛠️ Utilidades (1 archivo)
│   └── helpers.ts
│
├── 📋 Constantes (1 archivo)
│   └── index.ts
│
├── 🎭 Context (1 archivo)
│   └── AppContext.tsx
│
└── 🚦 Router (1 archivo)
    └── index.tsx
```

## 🎯 Casos de Uso Principales

```
1. CREAR IGLESIA
   Usuario → Click [+Nueva Iglesia]
   ↓
   Modal abre
   ↓
   Llena formulario
   ↓
   Click [Guardar]
   ↓
   churchService.create()
   ↓
   API POST /churches
   ↓
   Tabla actualiza

2. VER PERSONAS
   Usuario → Click [Personas]
   ↓
   Vista carga datos
   ↓
   personService.getAll()
   ↓
   API GET /people
   ↓
   Tabla muestra datos
   ↓
   Usuario puede:
      → Click en fila (detalle)
      → [+ Nueva Persona] (crear)
      → [Editar] (modificar)
      → [Eliminar] (borrar)

3. DASHBOARD
   Usuario → Accede a /dashboard
   ↓
   Carga estadísticas
   ↓
   Obtiene datos de:
      → churches.length
      → people.length
      → services.length
      → groups.length
      → events.length
   ↓
   Muestra tarjetas con stats
   ↓
   Muestra acciones rápidas
   ↓
   Muestra actividad reciente
```

## 🔐 Seguridad & Validación

```
Frontend Validation:
  ├─ validateEmail()
  ├─ validatePhone()
  ├─ Type checking (TypeScript)
  └─ Required fields check

API Layer:
  ├─ JWT Token handling
  ├─ Request/Response types
  └─ Error handling

Backend (Recomendado):
  ├─ Input validation
  ├─ Authorization checks
  ├─ SQL injection prevention
  └─ Rate limiting
```

## 📊 Datos Modelo

```
Church
├─ id: string
├─ name: string
├─ address: string
├─ city: string
├─ phone: string
├─ email: string
├─ pastor: string
├─ foundedYear: number
└─ memberCount: number

Person
├─ id: string
├─ firstName: string
├─ lastName: string
├─ email: string
├─ phone: string
├─ birthDate: Date
├─ role: 'pastor'|'deacon'|'member'|'visitor'|'volunteer'
├─ churchId: string
└─ status: 'active'|'inactive'|'suspended'

Service
├─ id: string
├─ name: string
├─ description: string
├─ churchId: string
├─ date: Date
├─ startTime: string
├─ endTime: string
├─ type: 'sunday_service'|...
├─ attendees: string[]
├─ pastor: string
└─ location: string

Group
├─ id: string
├─ name: string
├─ description: string
├─ churchId: string
├─ type: 'home_group'|...
├─ leader: string
├─ members: string[]
├─ meetingDay: string
├─ meetingTime: string
└─ location: string

Event
├─ id: string
├─ title: string
├─ description: string
├─ churchId: string
├─ date: Date
├─ startTime: string
├─ endTime: string
├─ location: string
├─ organizer: string
├─ attendees: string[]
├─ maxCapacity: number
└─ status: 'planned'|...
```

## ⚡ Performance

```
Optimizaciones Implementadas:
  ├─ Component Memoization (React.memo)
  ├─ Reusable Components (DRY)
  ├─ CSS Classes (No inline styles)
  ├─ TypeScript (Type safety)
  ├─ Service Layer (Centralized API)
  ├─ Custom Hooks (Logic reuse)
  └─ Context API (State management)

Mejoras Recomendadas:
  ├─ Code Splitting (React.lazy)
  ├─ Image Optimization
  ├─ Caching Strategy
  ├─ Virtual Scrolling (big tables)
  ├─ Service Worker
  └─ CDN (production)
```

## 🚀 Despliegue

```
Development: npm run dev
             localhost:5173

Production: npm run build
            dist/ folder
            npm run preview
            
Docker:     docker build .
            docker run -p 3000:3000

Deployment: Push to:
            - Vercel
            - Netlify
            - AWS S3 + CloudFront
            - Azure Static Web Apps
```

---

**Este mapa visual muestra cómo todas las partes del proyecto funcionan juntas para crear una aplicación cohesiva y profesional.**

*Última actualización: Diciembre 2025*
