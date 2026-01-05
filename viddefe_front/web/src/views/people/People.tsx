import { useState, useEffect } from 'react';
import type { Person, PersonRole } from '../../models';
import { Button, PageHeader, Table, Modal, Avatar, PersonForm, initialPersonFormData, type PersonFormData, DropDown } from '../../components/shared';
import { usePeople, usePerson, useUpdatePerson, useDeletePerson, usePersonTypes } from '../../hooks';
import { authService, type PersonRequest } from '../../services/authService';
import { formatDate } from '../../utils';
import CreateUserModal from '../../components/people/CreateUserModal';
import { useAppContext } from '../../context/AppContext';
import { PeoplePermission, UserPermission } from '../../services/userService';
import type { SortConfig } from '../../services/api';

type ModalMode = 'create' | 'edit' | 'view' | 'delete' | 'createUser' | null;

const DEFAULT_PAGE_SIZE = 10;

export default function People() {
  // Estado de paginación
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  
  // Estado de filtro por tipo de persona
  const [selectedTypeId, setSelectedTypeId] = useState<number | undefined>(undefined);

  // Estado de ordenamiento
  const [sortConfig, setSortConfig] = useState<SortConfig | undefined>(undefined);

  // Hook para obtener tipos de persona
  const { data: personTypes = [] } = usePersonTypes();

  const { data: people, isLoading, refetch } = usePeople({ 
    page: currentPage, 
    size: pageSize,
    typePersonId: selectedTypeId,
    sort: sortConfig
  });
  const { hasPermission } = useAppContext();

  // Permisos de personas
  const canCreate = hasPermission(PeoplePermission.ADD);
  const canView = hasPermission(PeoplePermission.VIEW);
  const canEdit = hasPermission(PeoplePermission.EDIT);
  const canDelete = hasPermission(PeoplePermission.DELETE);
  
  // Permiso para crear usuarios (invitaciones)
  const canCreateUser = hasPermission(UserPermission.INVITATION);
  
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [selectedPerson, setSelectedPerson] = useState<Person | null>(null);
  const [personData, setPersonData] = useState<PersonFormData>(initialPersonFormData);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

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
        typePersonId: (personDetails as any).typePersonId || 1,
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

  const openCreateUserModal = (person: Person) => {
    setSelectedPerson(person);
    setModalMode('createUser');
  };

  const closeModal = () => {
    setModalMode(null);
    resetForm();
  };

  const handleUserCreated = () => {
    setSuccessMessage(`Usuario creado exitosamente para ${selectedPerson?.firstName} ${selectedPerson?.lastName}`);
    setTimeout(() => setSuccessMessage(''), 5000);
    closeModal();
    refetch();
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

  const columns = [
    {
      key: 'id' as const,
      label: 'Imagen',
      render: (_: unknown, person: Person) => (
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
      render: (_: unknown, person: Person) => `${person.firstName}`
    },
    { 
      key: 'lastName' as const, 
      label: 'Apellidos', 
      render: (_: unknown, person: Person) => `${person.lastName}` 
    },
    { key: 'firstName' as const, label: 'Cédula', render: (_: unknown, person: Person) => (person as any).cc || '-' },
    { key: 'typePerson' as const, label: 'Tipo de Persona', render: (_: unknown, person: Person) => person.typePerson?.name || '-' },
    { key: 'phone' as const, label: 'Teléfono' },
    { 
      key: 'birthDate' as const, 
      label: 'Fecha de Nacimiento', 
      render: (value: unknown, _item: Person) => value ? formatDate(value as string | Date) : '-',
    },
    { 
      key: 'state' as const, 
      label: 'Departamento', 
      render: (_value: unknown, item: Person) => item.state?.name || '-' 
    },
  ] as any;

  // Construir acciones de la tabla dinámicamente basadas en permisos
  const tableActions: Array<{
    icon: 'edit' | 'delete' | 'view' | 'user';
    label: string;
    onClick: (item: Person) => void;
    variant?: 'primary' | 'danger' | 'secondary';
    hidden?: (item: Person) => boolean;
  }> = [];

  // Ver detalles - requiere permiso de ver
  if (canView) {
    tableActions.push({
      icon: 'view',
      label: 'Ver detalles',
      onClick: openViewModal,
      variant: 'secondary',
    });
  }

  // Editar - requiere permiso de editar
  if (canEdit) {
    tableActions.push({
      icon: 'edit',
      label: 'Editar',
      onClick: openEditModal,
      variant: 'primary',
    });
  }

  // Agregar acción de crear usuario si tiene permiso
  if (canCreateUser) {
    tableActions.push({
      icon: 'user',
      label: 'Crear Usuario',
      onClick: openCreateUserModal,
      variant: 'secondary',
      // Solo mostrar si la persona no tiene usuario
      hidden: (person: Person) => person.hasUser === true,
    });
  }

  // Eliminar - requiere permiso de eliminar
  if (canDelete) {
    tableActions.push({
      icon: 'delete',
      label: 'Eliminar',
      onClick: openDeleteModal,
      variant: 'danger',
    });
  }

  const peopleData = Array.isArray(people) ? people : (people?.content ?? []);
  const isFormModalOpen = modalMode === 'create' || modalMode === 'edit';
  const isMutating = loading || updatePerson.isPending || deletePerson.isPending;

  // Información de paginación del servidor
  const paginationData = people && !Array.isArray(people) ? {
    totalPages: people.totalPages,
    totalElements: people.totalElements,
    currentPage: people.number,
    pageSize: people.size
  } : null;

  // Handlers de paginación
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Resetear a primera página
  };

  // Handler para cambio de tipo de persona
  const handleTypeChange = (value: string) => {
    const typeId = value ? Number(value) : undefined;
    setSelectedTypeId(typeId);
    setCurrentPage(0); // Resetear a primera página al filtrar
  };

  // Handler para cambio de ordenamiento
  const handleSortChange = (sort: SortConfig | undefined) => {
    setSortConfig(sort);
    setCurrentPage(0); // Resetear a primera página al ordenar
  };

  // Opciones para el dropdown de tipos con opción "Todos"
  const typeOptions = [
    { id: '', name: 'Todos los tipos' },
    ...personTypes
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="Personas"
        subtitle="Gestiona todos los miembros y contactos"
        actions={
          <div className="flex items-center gap-3">
            {/* Filtro por tipo de persona */}
            <div className="w-48">
              <DropDown
                options={typeOptions}
                value={selectedTypeId?.toString() ?? ''}
                onChangeValue={handleTypeChange}
                placeholder="Filtrar por tipo"
                labelKey="name"
                valueKey="id"
              />
            </div>
            {canCreate && <Button variant="primary" onClick={openCreateModal}>+ Nueva Persona</Button>}
          </div>
        }
      />

      {/* Mensaje de éxito */}
      {successMessage && (
        <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm flex items-center gap-2">
          <svg className="w-5 h-5 shrink-0" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
          </svg>
          {successMessage}
        </div>
      )}

      <Table<Person>
        data={peopleData}
        columns={columns}
        actions={tableActions}
        loading={isLoading}
        pagination={paginationData ? {
          mode: 'manual',
          currentPage: paginationData.currentPage,
          totalPages: paginationData.totalPages,
          totalElements: paginationData.totalElements,
          pageSize: paginationData.pageSize,
          onPageChange: handlePageChange,
          onPageSizeChange: handlePageSizeChange,
        } : { mode: 'auto', pageSize: DEFAULT_PAGE_SIZE }}
        sorting={{
          mode: 'manual',
          sortConfig: sortConfig,
          onSortChange: handleSortChange,
        }}
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
                    {personDetails?.typePerson?.name || selectedPerson.typePerson?.name || '-'}
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

      {/* Modal de Crear Usuario */}
      <CreateUserModal
        isOpen={modalMode === 'createUser'}
        person={selectedPerson}
        onClose={closeModal}
        onSuccess={handleUserCreated}
      />
    </div>
  );
}
