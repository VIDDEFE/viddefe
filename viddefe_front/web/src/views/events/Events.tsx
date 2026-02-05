import { useState } from 'react';
import type { Event } from '../../models';
import { Button, PageHeader, Table, Modal, Form, Input, DropDown } from '../../components/shared';
import { useEvents, useCreateEvent } from '../../hooks';
import { formatDateForDisplay } from '../../utils/helpers';

const eventStatusOptions = [
  { value: 'planned', label: 'Planeado' },
  { value: 'in_progress', label: 'En Progreso' },
  { value: 'completed', label: 'Completado' },
  { value: 'cancelled', label: 'Cancelado' },
];

export default function Events() {
  const { data: events = [] } = useEvents()
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Partial<Event>>({});

  const createEvent = useCreateEvent();

  const handleAddEvent = () => {
    if (formData.title) {
      createEvent.mutate(
        {
          title: formData.title || '',
          description: formData.description || '',
          churchId: formData.churchId || '1',
          date: formData.date || new Date(),
          startTime: formData.startTime || '09:00',
          endTime: formData.endTime || '17:00',
          location: formData.location || '',
          organizer: formData.organizer || '1',
          attendees: formData.attendees || [],
          maxCapacity: formData.maxCapacity || 100,
          status: (formData.status as any) || 'planned',
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
    { key: 'title' as const, label: 'Título' },
    { 
      key: 'date' as const, 
      label: 'Fecha',
      render: (date: any) => formatDateForDisplay(date, 'short')
    },
    { key: 'location' as const, label: 'Ubicación' },
    { key: 'status' as const, label: 'Estado' },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Eventos"
        subtitle="Gestiona los eventos de la iglesia"
        actions={<Button variant="primary" onClick={() => setIsModalOpen(true)}>+ Nuevo Evento</Button>}
      />

      <Table<Event>
        data={events || []}
        columns={columns}
      />

      <Modal
        isOpen={isModalOpen}
        title="Agregar Nuevo Evento"
        onClose={() => setIsModalOpen(false)}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleAddEvent} disabled={createEvent.isPending}>
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
            label="Título"
            placeholder="Título del evento"
            value={formData.title || ''}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          />
          <Input
            label="Descripción"
            placeholder="Descripción del evento"
            value={formData.description || ''}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
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
            value={formData.startTime || '09:00'}
            onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
          />
          <Input
            label="Hora de Fin"
            type="time"
            value={formData.endTime || '17:00'}
            onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
          />
          <Input
            label="Ubicación"
            placeholder="Lugar del evento"
            value={formData.location || ''}
            onChange={(e) => setFormData({ ...formData, location: e.target.value })}
          />
          <Input
            label="Capacidad Máxima"
            type="number"
            value={formData.maxCapacity || '100'}
            onChange={(e) => setFormData({ ...formData, maxCapacity: parseInt(e.target.value) })}
          />
          <DropDown
            label="Estado"
            options={eventStatusOptions}
            value={formData.status || 'planned'}
            onChangeValue={(value) => setFormData({ ...formData, status: value as any })}
          />
        </Form>
      </Modal>
    </div>
  );
}
