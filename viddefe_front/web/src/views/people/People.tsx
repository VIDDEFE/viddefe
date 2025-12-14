import { useState } from 'react';
import type { Person } from '../../models';
import { Button, PageHeader, Table, Modal, Form, Input, Select, Avatar } from '../../components/shared';
import { usePeople, useCreatePerson } from '../../hooks';
import type { States } from '../../services/stateCitiesService';

const roleOptions = [
  { value: 'pastor', label: 'Pastor' },
  { value: 'deacon', label: 'Diácono' },
  { value: 'member', label: 'Miembro' },
  { value: 'visitor', label: 'Visitante' },
  { value: 'volunteer', label: 'Voluntario' },
];

export default function People() {
  const { data: people = [] } = usePeople()
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Partial<Person>>({});

  const createPerson = useCreatePerson();

  const handleAddPerson = () => {
    if (formData.firstName && formData.lastName) {
      createPerson.mutate(
        {
          firstName: formData.firstName || '',
          lastName: formData.lastName || '',
          email: formData.email || '',
          phone: formData.phone || '',
          birthDate: formData.birthDate || new Date(),
          role: (formData.role as any) || 'member',
          churchId: formData.churchId || '1',
          state: formData.state || { id: 0, name: '' },
          status: 'active',
        },
        {
          onSuccess() {
            setFormData({});
            setIsModalOpen(false);
          },
        }
      )
    }
  };

  const columns = [
    {
      key: 'id' as const,
      label: 'Imagen',
      render: (_: any, person: Person) => (
        <Avatar 
          src={(person as any).avatar} 
          name={`${person.firstName} ${person.lastName}`} 
          size="sm" 
        />
      )
    },
    { 
      key: 'firstName' as const, 
      label: 'Primer Nombre',
      render: (_: any, person: Person) => `${person.firstName}`
    },
    { key: 'lastName' as const, label: 'Apellido', render: (_: any, person: Person) => `${person.lastName}` },
    { key: 'phone' as const, label: 'Teléfono' },
    { key: 'role' as const, label: 'Rol' },
    { key: 'status' as const, label: 'Estado' },
    { key: 'birthDate' as const, label: 'Fecha de Nacimiento', render: (value: string | Date) => value ? new Date(value).toLocaleDateString() : '' },
    { key: 'state' as const, label: 'Departamento de Nacimiento', render: (_value: string | number | States, item: Person) => item.state?.name },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Personas"
        subtitle="Gestiona todos los miembros y contactos"
        actions={<Button variant="primary" onClick={() => setIsModalOpen(true)}>+ Nueva Persona</Button>}
      />

      <Table<Person>
         data={Array.isArray(people) ? people : (people?.content ?? [])}
          columns={columns}
      />

      <Modal
        isOpen={isModalOpen}
        title="Agregar Nueva Persona"
        onClose={() => setIsModalOpen(false)}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleAddPerson} disabled={createPerson.isPending}>
              Guardar
            </Button>
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>
              Cancelar
            </Button>
          </div>
        }
      >
        <Form>
          <Input
            label="Nombre"
            placeholder="Nombre"
            value={formData.firstName || ''}
            onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
          />
          <Input
            label="Apellido"
            placeholder="Apellido"
            value={formData.lastName || ''}
            onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
          />
          <Input
            label="Email"
            type="email"
            placeholder="correo@example.com"
            value={formData.email || ''}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <Input
            label="Teléfono"
            placeholder="Teléfono"
            value={formData.phone || ''}
            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
          />
          <Input
            label="Fecha de Nacimiento"
            type="date"
            value={formData.birthDate instanceof Date ? formData.birthDate.toISOString().split('T')[0] : ''}
            onChange={(e) => setFormData({ ...formData, birthDate: new Date(e.target.value) })}
          />
          <Select
            label="Rol"
            options={roleOptions}
            value={formData.role || 'member'}
            onChange={(e) => setFormData({ ...formData, role: e.target.value as any })}
          />
        </Form>
      </Modal>
    </div>
  );
}
