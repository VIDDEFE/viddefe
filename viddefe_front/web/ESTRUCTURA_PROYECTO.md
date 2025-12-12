# VIDDEFE - Sistema de Gestión de Iglesias

Una aplicación web moderna para gestionar iglesias, miembros, servicios, grupos y eventos.

## 📁 Estructura del Proyecto

```
src/
├── components/
│   ├── auth/                    # Componentes de autenticación
│   │   └── AuthForm.tsx
│   ├── layout/                  # Componentes de diseño
│   │   ├── Aside.tsx           # Barra lateral de navegación
│   │   ├── Layout.tsx          # Contenedor principal
│   │   └── index.ts
│   └── shared/                  # Componentes reutilizables
│       ├── Button.tsx           # Botón
│       ├── Card.tsx             # Tarjeta
│       ├── Form.tsx             # Formulario y campos
│       ├── Table.tsx            # Tabla
│       ├── PageHeader.tsx        # Encabezado de página
│       ├── Modal.tsx            # Ventana modal
│       └── index.ts
├── models/                      # Tipos TypeScript
│   ├── types.ts                # Definiciones de tipos
│   └── index.ts
├── router/
│   └── index.tsx               # Configuración de rutas
├── utils/                       # Funciones utilitarias
│   ├── helpers.ts              # Funciones de ayuda
│   └── index.ts
├── views/                       # Páginas principales
│   ├── dashboard/
│   │   └── Dashboard.tsx        # Panel principal
│   ├── churches/
│   │   └── Churches.tsx         # Gestión de iglesias
│   ├── people/
│   │   └── People.tsx           # Gestión de personas
│   ├── services/
│   │   └── Services.tsx         # Gestión de servicios/cultos
│   ├── groups/
│   │   └── Groups.tsx           # Gestión de grupos
│   ├── events/
│   │   └── Events.tsx           # Gestión de eventos
│   └── signin.tsx               # Página de login
├── App.tsx
├── App.css                      # Estilos principales
├── index.css                    # Estilos globales
└── main.tsx
```

## 🎯 Características Principales

### 1. **Dashboard**
   - Vista general con estadísticas
   - Resumen de iglesias, personas, servicios y grupos
   - Acciones rápidas
   - Actividad reciente

### 2. **Gestión de Iglesias**
   - Crear, editar y eliminar iglesias
   - Información de pastor y contacto
   - Cantidad de miembros
   - Datos de ubicación

### 3. **Gestión de Personas**
   - Agregar miembros a la iglesia
   - Roles: Pastor, Diácono, Miembro, Visitante, Voluntario
   - Información de contacto
   - Estado del miembro (activo, inactivo, suspendido)

### 4. **Servicios/Cultos**
   - Programar servicios dominicales, de oración, etc.
   - Registrar asistentes
   - Gestionar horarios
   - Tipos: Servicio Dominical, Miércoles, Noche de Oración, etc.

### 5. **Grupos**
   - Crear grupos por tipo (hogar, jóvenes, mujeres, hombres, oración, estudio)
   - Asignar líderes
   - Registrar miembros
   - Horarios de reunión

### 6. **Eventos**
   - Planificar eventos especiales
   - Registrar asistentes
   - Capacidad máxima
   - Estados: Planeado, En Progreso, Completado, Cancelado

## 🛠️ Tecnologías Utilizadas

- **React 18**: Framework UI
- **TypeScript**: Tipado estático
- **React Router DOM**: Enrutamiento
- **Vite**: Build tool
- **CSS 3**: Estilos responsive

## 📦 Componentes Reutilizables

### Button
```tsx
<Button variant="primary" size="md">
  Texto
</Button>
```

### Card
```tsx
<Card className="custom-class">
  Contenido
</Card>
```

### Form Components
```tsx
<Form>
  <Input label="Nombre" placeholder="..." />
  <TextArea label="Descripción" />
  <Select label="Rol" options={options} />
</Form>
```

### Table
```tsx
<Table<MyType>
  data={items}
  columns={columns}
  onRowClick={handleClick}
/>
```

### Modal
```tsx
<Modal 
  isOpen={isOpen} 
  title="Título" 
  onClose={handleClose}
>
  Contenido
</Modal>
```

## 🎨 Estilos

El proyecto utiliza un sistema de colores coherente:
- **Primario**: #667eea (Púrpura azul)
- **Secundario**: #764ba2 (Púrpura oscuro)
- **Fondo**: #f5f7fa (Gris claro)
- **Texto**: #1a202c (Gris oscuro)
- **Bordes**: #e2e8f0 (Gris muy claro)

## 🚀 Para Empezar

1. Instalar dependencias:
```bash
npm install
```

2. Ejecutar en desarrollo:
```bash
npm run dev
```

3. Compilar para producción:
```bash
npm run build
```

## 📝 Rutas Disponibles

- `/` - Redirige a dashboard
- `/signin` - Página de login
- `/dashboard` - Panel principal
- `/churches` - Gestión de iglesias
- `/people` - Gestión de personas
- `/services` - Gestión de servicios
- `/groups` - Gestión de grupos
- `/events` - Gestión de eventos

## 🔒 Autenticación

El sistema cuenta con una página de login básica. Para la autenticación real, integrar:
- JWT tokens
- API backend
- Gestión de sesiones

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la licencia MIT.

---

**Última actualización**: Diciembre 2025
