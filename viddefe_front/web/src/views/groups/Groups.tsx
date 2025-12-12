import { useState } from 'react';
import type { Group } from '../../models';
import { Button, PageHeader, Table, Modal, Form, Input, Select } from '../../components/shared';
import { useGroups, useCreateGroup } from '../../hooks';

const groupTypeOptions = [
  { value: 'home_group', label: 'Grupo de Hogar' },
  { value: 'youth_group', label: 'Grupo de Jóvenes' },
  { value: 'womens_group', label: 'Grupo de Mujeres' },
  { value: 'mens_group', label: 'Grupo de Hombres' },
  { value: 'prayer_group', label: 'Grupo de Oración' },
  { value: 'study_group', label: 'Grupo de Estudio' },
];

export default function Groups() {
  const { data: groups = [] } = useGroups()
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Partial<Group>>({});

  const createGroup = useCreateGroup();

  const handleAddGroup = () => {
    if (formData.name) {
      createGroup.mutate(
        {
          name: formData.name || '',
          description: formData.description || '',
          churchId: formData.churchId || '1',
          type: (formData.type as any) || 'home_group',
          leader: formData.leader || '1',
          meetingDay: formData.meetingDay || '',
          meetingTime: formData.meetingTime || '',
          location: formData.location || '',
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
    { key: 'name' as const, label: 'Nombre' },
    { key: 'type' as const, label: 'Tipo' },
    { key: 'meetingDay' as const, label: 'Día de Reunión' },
    { key: 'location' as const, label: 'Ubicación' },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Grupos"
        subtitle="Gestiona los grupos de la iglesia"
        actions={<Button variant="primary" onClick={() => setIsModalOpen(true)}>+ Nuevo Grupo</Button>}
      />

      <Table<Group>
        data={groups || []}
        columns={columns}
      />

      <Modal
        isOpen={isModalOpen}
        title="Agregar Nuevo Grupo"
        onClose={() => setIsModalOpen(false)}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleAddGroup} disabled={createGroup.isLoading}>
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
            placeholder="Nombre del grupo"
            value={formData.name || ''}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          />
          <Input
            label="Descripción"
            placeholder="Descripción del grupo"
            value={formData.description || ''}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
          <Select
            label="Tipo de Grupo"
            options={groupTypeOptions}
            value={formData.type || 'home_group'}
            onChange={(e) => setFormData({ ...formData, type: e.target.value as any })}
          />
          <Input
            label="Día de Reunión"
            placeholder="Ej: Lunes y Miércoles"
            value={formData.meetingDay || ''}
            onChange={(e) => setFormData({ ...formData, meetingDay: e.target.value })}
          />
          <Input
            label="Hora de Reunión"
            type="time"
            value={formData.meetingTime || ''}
            onChange={(e) => setFormData({ ...formData, meetingTime: e.target.value })}
          />
          <Input
            label="Ubicación"
            placeholder="Lugar de reunión"
            value={formData.location || ''}
            onChange={(e) => setFormData({ ...formData, location: e.target.value })}
          />
        </Form>
      </Modal>
    </div>
  );
}
