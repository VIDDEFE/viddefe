# ⚡ Inicio Rápido - VIDDEFE

## 🚀 En 5 Minutos

### 1. Instalar Dependencias
```bash
npm install
```

### 2. Ejecutar en Desarrollo
```bash
npm run dev
```

### 3. Abrir en el Navegador
```
http://localhost:5173
```

### 4. Ver la Aplicación
- **Dashboard**: Estadísticas principales
- **Iglesias**: Gestiona iglesias
- **Personas**: Gestiona miembros
- **Servicios**: Gestiona cultos
- **Grupos**: Gestiona grupos
- **Eventos**: Gestiona eventos

---

## 📖 Documentación por Uso

### 👤 Soy nuevo en el proyecto
→ Lee [README_DOCUMENTACION.md](README_DOCUMENTACION.md)

### 💻 Voy a escribir código
→ Lee [GUIA_DESARROLLO.md](GUIA_DESARROLLO.md)

### 🎨 Necesito entender la estructura
→ Lee [ESTRUCTURA_PROYECTO.md](ESTRUCTURA_PROYECTO.md) y [MAPA_VISUAL.md](MAPA_VISUAL.md)

### 💡 Necesito ejemplos
→ Lee [EJEMPLOS_PRACTICOS.md](EJEMPLOS_PRACTICOS.md)

### ⚙️ Necesito configurar algo
→ Lee [CONFIGURACION.md](CONFIGURACION.md)

### 📊 Quiero un resumen ejecutivo
→ Lee [RESUMEN_PROYECTO.md](RESUMEN_PROYECTO.md)

---

## 🎯 Tareas Comunes

### Crear una Nueva Vista

1. Crea archivo en `src/views/my-view/MyView.tsx`
2. Copia estructura de ejemplo (ver [GUIA_DESARROLLO.md](GUIA_DESARROLLO.md))
3. Importa en `src/router/index.tsx`
4. Agrega ruta en la configuración

### Crear un Nuevo Componente

1. Crea archivo en `src/components/shared/MyComponent.tsx`
2. Exporta en `src/components/shared/index.ts`
3. Usa en tus vistas

### Conectar API

1. Crea servicio en `src/services/myService.ts`
2. Usa en tu componente:
```tsx
import { myService } from '../../services';
const data = await myService.getAll();
```

### Usar Formulario

```tsx
const form = useForm({ name: '', email: '' });

<Input
  label="Nombre"
  value={form.values.name}
  onChange={(e) => form.setField('name', e.target.value)}
/>
```

---

## 📁 Estructura Esencial

```
src/
├── components/shared/        # Componentes reutilizables
├── views/                    # Páginas
├── models/                   # Tipos TypeScript
├── services/                 # Servicios API
├── utils/                    # Funciones útiles
├── hooks/                    # Custom hooks
├── router/                   # Rutas
└── App.tsx
```

---

## 🎨 Componentes Principales

```tsx
// Botón
<Button variant="primary" onClick={handleClick}>
  Guardar
</Button>

// Formulario
<Form>
  <Input label="Nombre" value={name} onChange={(e) => setName(e.target.value)} />
  <Button>Enviar</Button>
</Form>

// Tabla
<Table data={items} columns={columns} />

// Modal
<Modal isOpen={open} title="Título" onClose={closeModal}>
  Contenido
</Modal>

// Tarjeta
<Card>
  Contenido
</Card>
```

---

## 🔧 Hooks Útiles

```tsx
// Formulario
const form = useForm({ name: '' });

// Modal
const modal = useModal();
modal.open(); modal.close();

// Toggle
const { state, toggle } = useToggle();

// Fetch
const { data, loading } = useFetch(() => apiCall());
```

---

## 📱 Rutas

| Ruta | Página |
|------|--------|
| `/` | Dashboard |
| `/signin` | Login |
| `/dashboard` | Dashboard |
| `/churches` | Iglesias |
| `/people` | Personas |
| `/services` | Servicios |
| `/groups` | Grupos |
| `/events` | Eventos |

---

## 🎨 Colores

```
Primario:   #667eea
Secundario: #764ba2
Fondo:      #f5f7fa
Texto:      #1a202c
```

---

## 📦 Dependencias Principales

- `react` - UI Framework
- `react-router-dom` - Enrutamiento
- `typescript` - Tipado

---

## 🆘 Ayuda Rápida

**Pregunta**: ¿Cómo agrego un botón?
**Respuesta**: 
```tsx
import { Button } from '../../components/shared';
<Button variant="primary">Click me</Button>
```

**Pregunta**: ¿Cómo hago una llamada a API?
**Respuesta**:
```tsx
import { churchService } from '../../services';
const churches = await churchService.getAll();
```

**Pregunta**: ¿Cómo manejo un formulario?
**Respuesta**:
```tsx
const form = useForm({ name: '' });
form.setField('name', value);
form.reset();
```

---

## ✅ Checklist de Inicio

- [ ] `npm install`
- [ ] `npm run dev`
- [ ] Abre http://localhost:5173
- [ ] Navega por las vistas
- [ ] Lee [GUIA_DESARROLLO.md](GUIA_DESARROLLO.md)
- [ ] Crea tu primera vista
- [ ] ¡Felicidades! 🎉

---

## 📚 Documentación Completa

1. **[README_DOCUMENTACION.md](README_DOCUMENTACION.md)** - Índice de documentación
2. **[GUIA_DESARROLLO.md](GUIA_DESARROLLO.md)** - Guía para desarrolladores
3. **[EJEMPLOS_PRACTICOS.md](EJEMPLOS_PRACTICOS.md)** - Ejemplos de código
4. **[ESTRUCTURA_PROYECTO.md](ESTRUCTURA_PROYECTO.md)** - Arquitectura
5. **[CONFIGURACION.md](CONFIGURACION.md)** - Setup del proyecto
6. **[MAPA_VISUAL.md](MAPA_VISUAL.md)** - Visualización del proyecto
7. **[RESUMEN_PROYECTO.md](RESUMEN_PROYECTO.md)** - Resumen ejecutivo

---

## 🚀 Siguiente Paso

Lee [README_DOCUMENTACION.md](README_DOCUMENTACION.md) para entender toda la documentación disponible.

---

**¡Que disfrutes desarrollando! 🎉**
