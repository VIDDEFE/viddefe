import { useState } from 'react';
import type { Person } from '../../models';
import { Button, PageHeader, Table, Modal, Avatar, PersonForm, initialPersonFormData, type PersonFormData } from '../../components/shared';
import { usePeople, useCreatePerson } from '../../hooks';
import type { States } from '../../services/stateCitiesService';
import { authService, type PersonRequest } from '../../services/authService';

export default function People() {
  const { data: people = [], refetch } = usePeople()
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [personData, setPersonData] = useState<PersonFormData>(initialPersonFormData);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const createPerson = useCreatePerson();

  const handleAddPerson = async () => {
    setError('');

    if (!personData.cc || !personData.firstName || !personData.lastName || !personData.phone || !personData.birthDate) {
      setError('Por favor completa todos los campos requeridos');
      return;
    }

    if (!personData.stateId) {
      setError('Por favor selecciona un departamento');
      return;
    }

    setLoading(true);
    try {
      await authService.createPerson(personData as PersonRequest);
      setPersonData(initialPersonFormData);
      setIsModalOpen(false);
      refetch();
    } catch (err: any) {
      setError(err?.message || 'Error al crear la persona. Intenta de nuevo.');
    } finally {
      setLoading(false);
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

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setPersonData(initialPersonFormData);
    setError('');
  };

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
        onClose={handleCloseModal}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleAddPerson} disabled={loading}>
              {loading ? 'Guardando...' : 'Guardar'}
            </Button>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancelar
            </Button>
          </div>
        }
      >
        {error && (
          <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm mb-4">
            {error}
          </div>
        )}
        <PersonForm
          value={personData}
          onChange={setPersonData}
          disabled={loading}
          showTypeSelector={true}
        />
      </Modal>
    </div>
  );
}
