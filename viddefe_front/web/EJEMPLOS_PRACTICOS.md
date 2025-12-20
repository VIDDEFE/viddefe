# 🎬 Ejemplos Prácticos de Uso

## Ejemplo 1: Crear una vista completa usando todos los componentes

```tsx
import { useState } from 'react';
import { useModal, useForm } from '../../hooks';
import { organizationService } from '../../services';
import { Organization } from '../../models';
import {
  Button,
  Card,
  Form,
  Input,
  Table,
  PageHeader,
  Modal,
  Select,
} from '../../components/shared';

const ORGANIZATION_OPTIONS = [
  { value: 'ngo', label: 'ONG' },
  { value: 'church', label: 'Iglesia' },
  { value: 'community', label: 'Comunidad' },
];

export default function Organizations() {
  const [organizations, setOrganizations] = useState<Organization[]>([]);
  const modal = useModal();
  const form = useForm({ name: '', type: '', email: '' });

  const handleAdd = async () => {
    if (form.values.name) {
      try {
        const newOrg = await organizationService.create({
          name: form.values.name,
          type: form.values.type || 'ngo',
          email: form.values.email,
        } as any);
        setOrganizations([...organizations, newOrg]);
        form.reset();
        modal.close();
      } catch (error) {
        console.error('Error creando organización:', error);
      }
    }
  };

  const columns = [
    { key: 'name' as const, label: 'Nombre' },
    { key: 'type' as const, label: 'Tipo' },
    { key: 'email' as const, label: 'Email' },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Organizaciones"
        subtitle="Gestiona todas las organizaciones"
        actions={
          <Button variant="primary" onClick={modal.open}>
            + Nueva Organización
          </Button>
        }
      />

      <Card>
        <Table<Organization>
          data={organizations}
          columns={columns}
          onRowClick={}
        />
      </Card>

      <Modal
        isOpen={modal.isOpen}
        title="Agregar Organización"
        onClose={modal.close}
        actions={
          <div style={{ display: 'flex', gap: '10px' }}>
            <Button variant="primary" onClick={handleAdd}>
              Guardar
            </Button>
            <Button variant="secondary" onClick={modal.close}>
              Cancelar
            </Button>
          </div>
        }
      >
        <Form>
          <Input
            label="Nombre"
            placeholder="Nombre de la organización"
            value={form.values.name}
            onChange={(e) => form.setField('name', e.target.value)}
          />
          <Select
            label="Tipo"
            options={ORGANIZATION_OPTIONS}
            value={form.values.type}
            onChange={(e) => form.setField('type', e.target.value)}
          />
          <Input
            label="Email"
            type="email"
            placeholder="correo@ejemplo.com"
            value={form.values.email}
            onChange={(e) => form.setField('email', e.target.value)}
          />
        </Form>
      </Modal>
    </div>
  );
}
```

## Ejemplo 2: Usar Context API para estado global

```tsx
import { useAppContext } from '../../context/AppContext';
import { Button, Card } from '../../components/shared';

export default function Statistics() {
  const { churches, people, services, groups, events } = useAppContext();

  return (
    <div className="stats-container">
      <Card>
        <h3>Iglesias: {churches.length}</h3>
        <Button onClick={}>Ver Iglesias</Button>
      </Card>
      
      <Card>
        <h3>Personas: {people.length}</h3>
        <Button onClick={}>Ver Personas</Button>
      </Card>

      <Card>
        <h3>Servicios: {services.length}</h3>
      </Card>

      <Card>
        <h3>Grupos: {groups.length}</h3>
      </Card>

      <Card>
        <h3>Eventos: {events.length}</h3>
      </Card>
    </div>
  );
}
```

## Ejemplo 3: Crear un formulario con validación

```tsx
import { useState } from 'react';
import { useForm } from '../../hooks';
import { validateEmail, validatePhone } from '../../utils';
import { Button, Form, Input, TextArea } from '../../components/shared';

export default function ContactForm() {
  const form = useForm({
    name: '',
    email: '',
    phone: '',
    message: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const newErrors: Record<string, string> = {};

    if (!form.values.name) newErrors.name = 'El nombre es requerido';
    if (!validateEmail(form.values.email)) newErrors.email = 'Email inválido';
    if (!validatePhone(form.values.phone)) newErrors.phone = 'Teléfono inválido';
    if (!form.values.message) newErrors.message = 'El mensaje es requerido';

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      form.reset();
      setErrors({});
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Input
        label="Nombre"
        value={form.values.name}
        onChange={(e) => form.setField('name', e.target.value)}
        error={errors.name}
      />

      <Input
        label="Email"
        type="email"
        value={form.values.email}
        onChange={(e) => form.setField('email', e.target.value)}
        error={errors.email}
      />

      <Input
        label="Teléfono"
        value={form.values.phone}
        onChange={(e) => form.setField('phone', e.target.value)}
        error={errors.phone}
      />

      <TextArea
        label="Mensaje"
        value={form.values.message}
        onChange={(e) => form.setField('message', e.target.value)}
        error={errors.message}
      />

      <Button variant="primary">Enviar</Button>
    </Form>
  );
}
```

## Ejemplo 4: Tabla con acciones

```tsx
import { Card, Table, Button } from '../../components/shared';
import { Person } from '../../models';

export default function PeopleList() {
  const [people] = useState<Person[]>([
    {
      id: '1',
      firstName: 'Juan',
      lastName: 'Pérez',
      email: 'juan@ejemplo.com',
      phone: '1234567890',
      birthDate: new Date('1990-01-01'),
      role: 'pastor',
      churchId: '1',
      status: 'active',
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  ]);

  const handleDelete = (id: string) => {
    if (confirm('¿Estás seguro?')) {
      console.log('Eliminar:', id);
    }
  };

  const columns = [
    {
      key: 'firstName' as const,
      label: 'Nombre Completo',
      render: (_, person: Person) => `${person.firstName} ${person.lastName}`,
    },
    { key: 'email' as const, label: 'Email' },
    { key: 'role' as const, label: 'Rol' },
    {
      key: 'status' as const,
      label: 'Estado',
      render: (status: string) => (
        <span style={{ color: status === 'active' ? 'green' : 'red' }}>
          {status}
        </span>
      ),
    },
  ];

  return (
    <Card>
      <Table<Person>
        data={people}
        columns={columns}
        actions={
          <div style={{ display: 'flex', gap: '5px' }}>
            <Button size="sm" variant="secondary">Editar</Button>
            <Button
              size="sm"
              variant="danger"
              onClick={() => handleDelete('1')}
            >
              Eliminar
            </Button>
          </div>
        }
      />
    </Card>
  );
}
```

## Ejemplo 5: Usar funciones utilitarias

```tsx
import {
  formatDate,
  translateRole,
  translateServiceType,
  capitalize,
} from '../../utils';
import { Card } from '../../components/shared';

export default function Information() {
  const today = new Date();
  const role = 'pastor';
  const serviceType = 'sunday_service';

  return (
    <Card>
      <h3>Información Formateada</h3>
      <p>Hoy es: {formatDate(today)}</p>
      <p>Mi rol es: {translateRole(role)}</p>
      <p>Tipo de servicio: {translateServiceType(serviceType)}</p>
      <p>Mensaje: {capitalize('hola mundo')}</p>
    </Card>
  );
}
```

## Ejemplo 6: Hook personalizado useFetch

```tsx
import { useEffect } from 'react';
import { useFetch } from '../../hooks';
import { churchService } from '../../services';
import { Card, Button } from '../../components/shared';

export default function ChurchesList() {
  const { data: churches, loading, error, execute } = useFetch(
    () => churchService.getAll(),
    []
  );

  useEffect(() => {
    execute();
  }, [execute]);

  if (loading) return <div>Cargando iglesias...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <Card>
      <h2>Iglesias ({churches?.length || 0})</h2>
      {churches?.map((church) => (
        <div key={church.id} style={{ padding: '10px', borderBottom: '1px solid #ccc' }}>
          <h4>{church.name}</h4>
          <p>Pastor: {church.pastor}</p>
          <p>Miembros: {church.memberCount}</p>
        </div>
      ))}
      <Button onClick={() => execute()}>Actualizar</Button>
    </Card>
  );
}
```

## Ejemplo 7: Combinando múltiples hooks

```tsx
import { useEffect } from 'react';
import { useForm, useModal, useFetch } from '../../hooks';
import { personService } from '../../services';
import { Person } from '../../models';
import {
  Button,
  Form,
  Input,
  Select,
  Modal,
  Table,
  PageHeader,
} from '../../components/shared';

const ROLE_OPTIONS = [
  { value: 'pastor', label: 'Pastor' },
  { value: 'deacon', label: 'Diácono' },
  { value: 'member', label: 'Miembro' },
];

export default function AdvancedPeople() {
  const modal = useModal();
  const form = useForm<Partial<Person>>({
    firstName: '',
    lastName: '',
    email: '',
    role: 'member',
  });
  const { data: people, execute: fetchPeople } = useFetch(
    () => personService.getAll(),
    []
  );

  useEffect(() => {
    fetchPeople();
  }, []);

  const handleAddPerson = async () => {
    if (form.values.firstName && form.values.lastName) {
      try {
        await personService.create(form.values as any);
        form.reset();
        modal.close();
        await fetchPeople();
      } catch (error) {
        console.error('Error:', error);
      }
    }
  };

  const columns = [
    {
      key: 'firstName' as const,
      label: 'Nombre',
      render: (_, person: Person) => `${person.firstName} ${person.lastName}`,
    },
    { key: 'email' as const, label: 'Email' },
    { key: 'role' as const, label: 'Rol' },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Gestión Avanzada de Personas"
        actions={
          <Button variant="primary" onClick={modal.open}>
            + Agregar
          </Button>
        }
      />

      <Table<Person> data={people || []} columns={columns} />

      <Modal
        isOpen={modal.isOpen}
        title="Nueva Persona"
        onClose={modal.close}
        actions={
          <div style={{ display: 'flex', gap: '10px' }}>
            <Button variant="primary" onClick={handleAddPerson}>
              Guardar
            </Button>
            <Button variant="secondary" onClick={modal.close}>
              Cancelar
            </Button>
          </div>
        }
      >
        <Form>
          <Input
            label="Nombre"
            value={form.values.firstName || ''}
            onChange={(e) => form.setField('firstName', e.target.value)}
          />
          <Input
            label="Apellido"
            value={form.values.lastName || ''}
            onChange={(e) => form.setField('lastName', e.target.value)}
          />
          <Input
            label="Email"
            type="email"
            value={form.values.email || ''}
            onChange={(e) => form.setField('email', e.target.value)}
          />
          <Select
            label="Rol"
            options={ROLE_OPTIONS}
            value={form.values.role || 'member'}
            onChange={(e) => form.setField('role', e.target.value)}
          />
        </Form>
      </Modal>
    </div>
  );
}
```

---

Estos ejemplos muestran cómo usar los diferentes componentes, hooks y servicios de forma conjunta. ¡Puedes adaptarlos a tus necesidades específicas!
