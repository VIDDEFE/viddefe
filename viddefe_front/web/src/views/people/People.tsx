import { useState, useEffect } from 'react';
import type { Person } from '../../models';
import { Button, PageHeader, Table, Modal, Avatar, PersonForm, initialPersonFormData, type PersonFormData } from '../../components/shared';
import { usePeople, usePerson, useUpdatePerson, useDeletePerson } from '../../hooks';
import type { States } from '../../services/stateCitiesService';
import { authService, type PersonRequest } from '../../services/authService';
import { formatDate } from '../../utils';

type ModalMode = 'create' | 'edit' | 'view' | 'delete' | null;

export default function People() {
  const { data: people, isLoading, refetch } = usePeople();
  
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [selectedPerson, setSelectedPerson] = useState<Person | null>(null);
  const [personData, setPersonData] = useState<PersonFormData>(initialPersonFormData);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Hook para obtener detalles de la persona seleccionada
  const { data: personDetails, isLoading: isLoadingDetails } = usePerson(selectedPerson?.id);
  const updatePerson = useUpdatePerson();
  const deletePerson = useDeletePerson();

  // Cargar datos de personDetails cuando se obtienen (para edición)
  useEffect(() => {
    if (personDetails && (modalMode === 'edit' || modalMode === 'view')) {
      setPersonData({
        cc: (personDetails as any).cc || '',
        firstName: personDetails.firstName,
        lastName: personDetails.lastName,
        phone: personDetails.phone,
        avatar: (personDetails as any).avatar || '',
        birthDate: personDetails.birthDate ? new Date(personDetails.birthDate).toISOString().split('T')[0] : '',
        typePersonId: (personDetails as any).typePersonId || 0,
        stateId: personDetails.state?.id || 0,
        churchId: personDetails.churchId,
      });
    }
  }, [personDetails, modalMode]);

  const resetForm = () => {
    setPersonData(initialPersonFormData);
    setSelectedPerson(null);
    setError('');
  };

  const openCreateModal = () => {
    resetForm();
    setModalMode('create');
  };

  const openEditModal = (person: Person) => {
    setSelectedPerson(person);
    setModalMode('edit');
  };

  const openViewModal = (person: Person) => {
    setSelectedPerson(person);
    setModalMode('view');
  };

  const openDeleteModal = (person: Person) => {
    setSelectedPerson(person);
    setModalMode('delete');
  };

  const closeModal = () => {
    setModalMode(null);
    resetForm();
  };

  const validateForm = (): boolean => {
    if (!personData.cc || !personData.firstName || !personData.lastName || !personData.phone || !personData.birthDate) {
      setError('Por favor completa todos los campos requeridos');
      return false;
    }
    if (!personData.stateId) {
      setError('Por favor selecciona un departamento');
      return false;
    }
    return true;
  };

  const handleCreate = async () => {
    setError('');
    if (!validateForm()) return;

    setLoading(true);
    try {
      await authService.createPerson(personData as PersonRequest);
      closeModal();
      refetch();
    } catch (err: any) {
      setError(err?.message || 'Error al crear la persona. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async () => {
    if (!selectedPerson?.id) return;
    setError('');
    if (!validateForm()) return;

    setLoading(true);
    try {
      await updatePerson.mutateAsync({
        id: selectedPerson.id,
        data: {
          cc: personData.cc,
          firstName: personData.firstName,
          lastName: personData.lastName,
          phone: personData.phone,
          birthDate: new Date(personData.birthDate),
          typePersonId: personData.typePersonId,
          stateId: personData.stateId,
        } as any,
      });
      closeModal();
    } catch (err: any) {
      setError(err?.message || 'Error al actualizar la persona. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedPerson?.id) return;
    
    setLoading(true);
    try {
      await deletePerson.mutateAsync(selectedPerson.id);
      closeModal();
    } catch (err: any) {
      setError(err?.message || 'Error al eliminar la persona. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  // Helper para obtener el tipo de persona
  const getPersonTypeName = (typeId: number) => {
    const types: Record<number, string> = {
      1: 'Oveja',
      2: 'Voluntario',
      3: 'Pastor',
    };
    return types[typeId] || '-';
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
      label: 'Nombres',
      render: (_: any, person: Person) => `${person.firstName}`
    },
    { 
      key: 'lastName' as const, 
      label: 'Apellidos', 
      render: (_: any, person: Person) => `${person.lastName}` 
    },
    { key: 'phone' as const, label: 'Teléfono' },
    { 
      key: 'birthDate' as const, 
      label: 'Fecha de Nacimiento', 
      render: (value: string | Date) => value ? formatDate(value) : '-' 
    },
    { 
      key: 'state' as const, 
      label: 'Departamento', 
      render: (_value: string | number | States, item: Person) => item.state?.name || '-' 
    },
  ];

  const tableActions = [
    {
      icon: 'view' as const,
      label: 'Ver detalles',
      onClick: openViewModal,
      variant: 'secondary' as const,
    },
    {
      icon: 'edit' as const,
      label: 'Editar',
      onClick: openEditModal,
      variant: 'primary' as const,
    },
    {
      icon: 'delete' as const,
      label: 'Eliminar',
      onClick: openDeleteModal,
      variant: 'danger' as const,
    },
  ];

  const peopleData = Array.isArray(people) ? people : (people?.content ?? []);
  const isFormModalOpen = modalMode === 'create' || modalMode === 'edit';
  const isMutating = loading || updatePerson.isPending || deletePerson.isPending;

  return (
    <div className="page-container">
      <PageHeader
        title="Personas"
        subtitle="Gestiona todos los miembros y contactos"
        actions={<Button variant="primary" onClick={openCreateModal}>+ Nueva Persona</Button>}
      />

      <Table<Person>
        data={peopleData}
        columns={columns}
        actions={tableActions}
        loading={isLoading}
        pagination={{ mode: 'auto', pageSize: 10 }}
      />

      {/* Modal de Crear/Editar */}
      <Modal
        isOpen={isFormModalOpen}
        title={modalMode === 'create' ? 'Agregar Nueva Persona' : 'Editar Persona'}
        onClose={closeModal}
        actions={
          <div className="flex gap-2">
            <Button 
              variant="primary" 
              onClick={modalMode === 'create' ? handleCreate : handleUpdate} 
              disabled={isMutating || (modalMode === 'edit' && isLoadingDetails)}
            >
              {isMutating ? 'Guardando...' : 'Guardar'}
            </Button>
            <Button variant="secondary" onClick={closeModal}>
              Cancelar
            </Button>
          </div>
        }
      >
        {modalMode === 'edit' && isLoadingDetails ? (
          <div className="flex justify-center items-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            <span className="ml-2 text-neutral-600">Cargando datos...</span>
          </div>
        ) : (
          <>
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm mb-4">
                {error}
              </div>
            )}
            <PersonForm
              value={personData}
              onChange={setPersonData}
              disabled={isMutating}
              showTypeSelector={true}
            />
          </>
        )}
      </Modal>

      {/* Modal de Ver Detalles */}
      <Modal
        isOpen={modalMode === 'view'}
        title="Detalles de la Persona"
        onClose={closeModal}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={() => selectedPerson && openEditModal(selectedPerson)}>
              Editar
            </Button>
            <Button variant="secondary" onClick={closeModal}>
              Cerrar
            </Button>
          </div>
        }
      >
        {isLoadingDetails ? (
          <div className="flex justify-center items-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            <span className="ml-2 text-neutral-600">Cargando datos...</span>
          </div>
        ) : (
          selectedPerson && (
            <div className="space-y-4">
              <div className="flex items-center gap-4 pb-4 border-b border-neutral-200">
                <Avatar 
                  src={(personDetails as any)?.avatar || (selectedPerson as any)?.avatar} 
                  name={`${personDetails?.firstName || selectedPerson.firstName} ${personDetails?.lastName || selectedPerson.lastName}`} 
                  size="lg" 
                />
                <div>
                  <h3 className="text-xl font-semibold text-primary-900">
                    {personDetails?.firstName || selectedPerson.firstName} {personDetails?.lastName || selectedPerson.lastName}
                  </h3>
                  <p className="text-neutral-600">
                    {getPersonTypeName((personDetails as any)?.typePersonId || 0)}
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-neutral-500">Cédula</label>
                  <p className="text-neutral-800">{(personDetails as any)?.cc || '-'}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-neutral-500">Teléfono</label>
                  <p className="text-neutral-800">{personDetails?.phone || selectedPerson.phone || '-'}</p>
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-neutral-500">Fecha de Nacimiento</label>
                  <p className="text-neutral-800">
                    {personDetails?.birthDate ? formatDate(personDetails.birthDate) : '-'}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium text-neutral-500">Departamento</label>
                  <p className="text-neutral-800">{personDetails?.state?.name || selectedPerson.state?.name || '-'}</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-neutral-500">Email</label>
                  <p className="text-neutral-800">{personDetails?.email || selectedPerson.email || '-'}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-neutral-500">Estado</label>
                  <p className="text-neutral-800">
                    <span className={`inline-flex px-2 py-1 rounded-full text-xs font-medium ${
                      selectedPerson.status === 'active' 
                        ? 'bg-green-100 text-green-800' 
                        : selectedPerson.status === 'inactive'
                        ? 'bg-neutral-100 text-neutral-800'
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {selectedPerson.status === 'active' ? 'Activo' : selectedPerson.status === 'inactive' ? 'Inactivo' : 'Suspendido'}
                    </span>
                  </p>
                </div>
              </div>
            </div>
          )
        )}
      </Modal>

      {/* Modal de Eliminar */}
      <Modal
        isOpen={modalMode === 'delete'}
        title="Eliminar Persona"
        onClose={closeModal}
        actions={
          <div className="flex gap-2">
            <Button 
              variant="danger" 
              onClick={handleDelete} 
              disabled={isMutating}
            >
              {isMutating ? 'Eliminando...' : 'Eliminar'}
            </Button>
            <Button variant="secondary" onClick={closeModal}>
              Cancelar
            </Button>
          </div>
        }
      >
        <div className="text-center py-4">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <h3 className="text-lg font-semibold text-neutral-900 mb-2">
            ¿Estás seguro de eliminar esta persona?
          </h3>
          <p className="text-neutral-600">
            Se eliminará a <strong>"{selectedPerson?.firstName} {selectedPerson?.lastName}"</strong>. Esta acción no se puede deshacer.
          </p>
        </div>
      </Modal>
    </div>
  );
}
