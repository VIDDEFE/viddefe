import { useState } from 'react';
import type { Service } from '../../models';
import { Button, PageHeader, Table, Modal, Form, Input, DropDown } from '../../components/shared';
import { useServices, useCreateService } from '../../hooks';

const serviceTypeOptions = [
  { value: 'sunday_service', label: 'Servicio Dominical' },
  { value: 'wednesday_service', label: 'Servicio Miércoles' },
  { value: 'prayer_night', label: 'Noche de Oración' },
  { value: 'special_event', label: 'Evento Especial' },
  { value: 'youth_service', label: 'Servicio de Jóvenes' },
];

export default function Services() {
  const { data: services = [] } = useServices()
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Partial<Service>>({});

  const createService = useCreateService();

  const handleAddService = () => {
    if (formData.name) {
      createService.mutate(
        {
          name: formData.name || '',
          description: formData.description || '',
          churchId: formData.churchId || '1',
          date: formData.date || new Date(),
          startTime: formData.startTime || '10:00',
          endTime: formData.endTime || '12:00',
          type: (formData.type as any) || 'sunday_service',
          attendees: formData.attendees || [],
          pastor: formData.pastor || '1',
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
    { 
      key: 'date' as const, 
      label: 'Fecha',
      render: (date: any) => new Date(date).toLocaleDateString('es-ES')
    },
    { key: 'location' as const, label: 'Ubicación' },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Servicios"
        subtitle="Gestiona los servicios y cultos"
        actions={<Button variant="primary" onClick={() => setIsModalOpen(true)}>+ Nuevo Servicio</Button>}
      />

      <Table<Service>
        data={services || []}
        columns={columns}
      />

      <Modal
        isOpen={isModalOpen}
        title="Agregar Nuevo Servicio"
        onClose={() => setIsModalOpen(false)}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleAddService} disabled={createService.isPending}>
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
            placeholder="Nombre del servicio"
            value={formData.name || ''}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          />
          <Input
            label="Descripción"
            placeholder="Descripción"
            value={formData.description || ''}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
          <DropDown
            label="Tipo"
            options={serviceTypeOptions}
            value={formData.type || 'sunday_service'}
            onChangeValue={(value) => setFormData({ ...formData, type: value as any })}
          />
          <Input
            label="Fecha"
            type="date"
            value={formData.date instanceof Date ? formData.date.toISOString().split('T')[0] : ''}
            onChange={(e) => setFormData({ ...formData, date: new Date(e.target.value) })}
          />
          <Input
            label="Hora de Inicio"
            type="time"
            value={formData.startTime || '10:00'}
            onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
          />
          <Input
            label="Hora de Fin"
            type="time"
            value={formData.endTime || '12:00'}
            onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
          />
          <Input
            label="Ubicación"
            placeholder="Lugar del servicio"
            value={formData.location || ''}
            onChange={(e) => setFormData({ ...formData, location: e.target.value })}
          />
        </Form>
      </Modal>
    </div>
  );
}
