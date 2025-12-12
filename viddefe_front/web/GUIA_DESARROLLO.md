# 📚 Guía Rápida de Desarrollo - VIDDEFE

## 🚀 Primeros Pasos

### Instalación
```bash
npm install
npm run dev
```

### Estructura base de una nueva vista
```tsx
import { useState } from 'react';
import { MyType } from '../../models';
import { Button, PageHeader, Table, Modal, Form, Input } from '../../components/shared';

export default function MyView() {
  const [items, setItems] = useState<MyType[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Partial<MyType>>({});

  const handleAdd = () => {
    // Lógica para agregar
  };

  const columns = [
    { key: 'name' as const, label: 'Nombre' },
    // más columnas...
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Mi Vista"
        actions={<Button onClick={() => setIsModalOpen(true)}>+ Nuevo</Button>}
      />
      
      <Table<MyType>
        data={items}
        columns={columns}
      />

      <Modal
        isOpen={isModalOpen}
        title="Agregar Nuevo"
        onClose={() => setIsModalOpen(false)}
        actions={
          <div style={{ display: 'flex', gap: '10px' }}>
            <Button variant="primary" onClick={handleAdd}>Guardar</Button>
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>Cancelar</Button>
          </div>
        }
      >
        <Form>
          {/* Campos del formulario */}
        </Form>
      </Modal>
    </div>
  );
}
```

## 🎨 Componentes Disponibles

### Botón
```tsx
<Button variant="primary" size="md" onClick={handleClick}>
  Hacer algo
</Button>
```
**Variantes**: `primary`, `secondary`, `danger`, `success`  
**Tamaños**: `sm`, `md`, `lg`

### Tarjeta
```tsx
<Card className="my-custom-class">
  <h3>Título</h3>
  <p>Contenido</p>
</Card>
```

### Formulario
```tsx
<Form onSubmit={handleSubmit}>
  <Input 
    label="Nombre" 
    placeholder="Ingresa nombre"
    value={formData.name}
    onChange={(e) => setFormData({...formData, name: e.target.value})}
  />
  
  <TextArea
    label="Descripción"
    value={formData.description}
    onChange={(e) => setFormData({...formData, description: e.target.value})}
  />
  
  <Select
    label="Rol"
    options={roleOptions}
    value={formData.role}
    onChange={(e) => setFormData({...formData, role: e.target.value})}
  />
  
  <Button variant="primary">Enviar</Button>
</Form>
```

### Tabla
```tsx
interface TableColumn<T> {
  key: keyof T;
  label: string;
  render?: (value: T[keyof T], item: T) => React.ReactNode;
}

const columns: TableColumn<MyType>[] = [
  { key: 'name', label: 'Nombre' },
  { 
    key: 'status', 
    label: 'Estado',
    render: (status) => <span className={`status-${status}`}>{status}</span>
  },
];

<Table<MyType>
  data={items}
  columns={columns}
  onRowClick={(item) => console.log(item)}
/>
```

### Modal
```tsx
<Modal
  isOpen={isOpen}
  title="Título del Modal"
  onClose={() => setIsOpen(false)}
  actions={
    <div style={{ display: 'flex', gap: '10px' }}>
      <Button>Guardar</Button>
      <Button variant="secondary">Cancelar</Button>
    </div>
  }
>
  {/* Contenido */}
</Modal>
```

### PageHeader
```tsx
<PageHeader
  title="Mi Página"
  subtitle="Descripción opcional"
  actions={<Button>Acción</Button>}
/>
```

## 🪝 Hooks Personalizados

### useForm
```tsx
import { useForm } from '../../hooks';

const form = useForm({ name: '', email: '' });

form.values.name; // acceder a valores
form.setField('name', 'Juan'); // actualizar campo
form.reset(); // resetear formulario
```

### useModal
```tsx
import { useModal } from '../../hooks';

const modal = useModal();

modal.isOpen; // estado actual
modal.open(); // abrir
modal.close(); // cerrar
modal.toggle(); // alternar
```

### useToggle
```tsx
import { useToggle } from '../../hooks';

const { state, toggle, setTrue, setFalse } = useToggle(false);
```

### useFetch
```tsx
import { useFetch } from '../../hooks';

const { data, loading, error, execute } = useFetch(
  () => apiService.get('/items'),
  []
);

await execute(); // ejecutar fetch
```

## 🔧 Servicios API

### Usar servicios
```tsx
import { churchService, personService } from '../../services';

// GET
const churches = await churchService.getAll();
const church = await churchService.getById('1');

// POST
const newChurch = await churchService.create({ name: '...', ... });

// PUT
await churchService.update('1', { name: 'Nuevo Nombre' });

// DELETE
await churchService.delete('1');
```

### Crear nuevo servicio
```tsx
// src/services/myService.ts
import { apiService } from './api';
import { MyType } from '../models';

export const myService = {
  getAll: () => apiService.get<MyType[]>('/my-endpoint'),
  getById: (id: string) => apiService.get<MyType>(`/my-endpoint/${id}`),
  create: (data: Omit<MyType, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<MyType>('/my-endpoint', data),
  update: (id: string, data: Partial<MyType>) =>
    apiService.put<MyType>(`/my-endpoint/${id}`, data),
  delete: (id: string) => apiService.delete(`/my-endpoint/${id}`),
};
```

## 📦 Tipos y Modelos

### Crear un nuevo modelo
```tsx
// src/models/types.ts
export interface MyType extends BaseEntity {
  name: string;
  description: string;
  // más propiedades
}
```

Todos los modelos heredan `BaseEntity`:
```tsx
export interface BaseEntity {
  id: string;
  createdAt: Date;
  updatedAt: Date;
}
```

## 🎯 Funciones Útiles

```tsx
import { 
  formatDate, 
  formatTime, 
  validateEmail,
  translateRole,
  capitalize 
} from '../../utils';

formatDate(new Date()); // "15 de diciembre de 2025"
formatTime('14:30'); // "14:30"
validateEmail('test@test.com'); // true
translateRole('pastor'); // "Pastor"
capitalize('hola'); // "Hola"
```

## 🎨 Estilos Globales

### Paleta de colores
```css
--primary: #667eea
--secondary: #764ba2
--background: #f5f7fa
--text: #1a202c
--border: #e2e8f0
```

### Aplicar estilos personalizados
```tsx
<div className="my-custom-class">
  Contenido
</div>
```

```css
/* src/App.css o archivo específico */
.my-custom-class {
  padding: 1rem;
  background: white;
  border-radius: 8px;
}
```

## ⚡ Mejores Prácticas

### 1. Separación de responsabilidades
- Componentes en `components/`
- Vistas (páginas) en `views/`
- Lógica de negocio en `services/`
- Tipos en `models/`

### 2. Naming conventions
- Componentes: `PascalCase` (MyComponent.tsx)
- Funciones: `camelCase` (myFunction)
- Constantes: `UPPER_SNAKE_CASE` (MY_CONSTANT)
- Archivos de estilo: `lowercase` (styles.css)

### 3. Tipado TypeScript
```tsx
// ❌ Evitar
const handleClick = (e: any) => { }

// ✅ Preferir
const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => { }
```

### 4. Reutilizar componentes
```tsx
// ✅ Bueno
<Button variant="primary">Guardar</Button>

// ❌ Evitar crear botones nuevos
<button style={{ backgroundColor: '#667eea' }}>Guardar</button>
```

### 5. Manejo de estado
```tsx
// Para estado local simple
const [isOpen, setIsOpen] = useState(false);

// Para formularios
const form = useForm(initialData);

// Para datos globales
const { churches, addChurch } = useAppContext();
```

## 🔗 Rutas útiles

```
/ → Dashboard
/signin → Login
/churches → Iglesias
/people → Personas
/services → Servicios
/groups → Grupos
/events → Eventos
```

## 📖 Estructura de archivos recomendada

```
src/
├── components/
│   ├── shared/
│   │   ├── Button.tsx
│   │   └── index.ts
│   └── layout/
│       ├── Aside.tsx
│       ├── Layout.tsx
│       └── index.ts
├── views/
│   ├── my-new-view/
│   │   └── MyNewView.tsx
│   └── dashboard/
│       └── Dashboard.tsx
├── models/
├── services/
├── utils/
├── hooks/
├── constants/
└── context/
```

## 🆘 Debugging

### Usar React DevTools
```bash
npm install -D @react-devtools/shell
```

### Console logs útiles
```tsx
console.log({ currentState: myVar });
console.table(items); // para arrays
console.time('operation'); // timing
```

### Errores comunes

**Error**: "Cannot read property of undefined"
```tsx
// ❌ Incorrecto
const name = user.profile.name; // puede fallar

// ✅ Correcto
const name = user?.profile?.name; // optional chaining
```

**Error**: React state not updating
```tsx
// ❌ Incorrecto
items.push(newItem); // mutación directa
setItems(items);

// ✅ Correcto
setItems([...items, newItem]); // nueva referencia
```

---

**¿Necesitas ayuda?** Revisa los ejemplos en las vistas existentes o contacta al equipo de desarrollo.
