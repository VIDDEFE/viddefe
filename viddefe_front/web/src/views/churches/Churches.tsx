import { useState, useEffect } from 'react';
import type { ChurchSummary } from '../../models';
import { Button, PageHeader, Table } from '../../components/shared';
import { type ChurchFormData, initialChurchFormData } from '../../components/churches/ChurchForm';
import { useChurchChildren, useStates, useCities, useCreateChildrenChurch, useUpdateChurch, useDeleteChurch, useChurch } from '../../hooks';
import { useAppContext } from '../../context/AppContext';
import ChurchFormModal from '../../components/churches/ChurchFormModal';
import ChurchViewModal from '../../components/churches/ChurchViewModal';
import ChurchDeleteModal from '../../components/churches/ChurchDeleteModal';

type ModalMode = 'create' | 'edit' | 'view' | 'delete' | null;

export default function Churches() {
  const { user } = useAppContext();
  const churchId = user?.church.id;

  // Data fetching
  const { data: churches, isLoading } = useChurchChildren(churchId);
  const { data: states } = useStates();

  // Modal state
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [selectedChurch, setSelectedChurch] = useState<ChurchSummary | null>(null);
  const [formData, setFormData] = useState<ChurchFormData>(initialChurchFormData);

  // Get details for selected church
  const { data: churchDetails, isLoading: isLoadingDetails } = useChurch(selectedChurch?.id);
  const { data: cities } = useCities(formData.stateId);

  // Mutations
  const createChurch = useCreateChildrenChurch(churchId);
  const updateChurch = useUpdateChurch();
  const deleteChurch = useDeleteChurch();

  // Track if form was already populated to prevent overwriting user changes
  const [formPopulated, setFormPopulated] = useState(false);

  // Load church details when editing/viewing
  // En Churches.tsx, reemplazar el useEffect problemático:
  useEffect(() => {
    if (!churchDetails || !selectedChurch || !(modalMode === 'edit' || modalMode === 'view')) return;
    if (formPopulated && modalMode === 'edit') return;
    
    setFormData(prev => ({
      name: churchDetails.name ?? prev.name ?? '',
      email: churchDetails.email ?? prev.email ?? '',
      phone: churchDetails.phone ?? prev.phone ?? '',
      foundationDate: churchDetails.foundationDate ?? prev.foundationDate ?? '',
      latitude: churchDetails.latitude !== undefined ? Number(churchDetails.latitude) : prev.latitude,
      longitude: churchDetails.longitude !== undefined ? Number(churchDetails.longitude) : prev.longitude,
      pastorId: churchDetails.pastor?.id ?? prev.pastorId ?? '',
      stateId: churchDetails.states?.id ?? prev.stateId,
      cityId: churchDetails.city?.cityId ?? prev.cityId,
    }));

    
    if (modalMode === 'edit') {
      setFormPopulated(true);
    }
  }, [churchDetails, modalMode]);

// Modificar openModal para manejar mejor el estado:
const openModal = (mode: ModalMode, church?: ChurchSummary) => {
  if (church) {
    setSelectedChurch(church);
    setFormPopulated(mode !== 'edit'); // Solo resetear si no es edición
  } else {
    resetForm();
  }
  setModalMode(mode);
};

  // Modal handlers
  const resetForm = () => {
    setFormData(initialChurchFormData);
    setSelectedChurch(null);
    setFormPopulated(false);
  };

  const closeModal = () => {
    setModalMode(null);
    resetForm();
  };

  // Handle form changes from ChurchFormModal (receives partial updates)
  const handleFormChange = (patch: Partial<ChurchFormData>) => {
    setFormData(prev => {
      const updated = { ...prev, ...patch };
      return updated;
    });
  };

  // CRUD handlers
  const handleCreate = () => {
    if (!formData.name) return;
    createChurch.mutate(
      {
        name: formData.name,
        cityId: formData.cityId || 0,
        phone: formData.phone,
        email: formData.email,
        pastor: '',
        pastorId: formData.pastorId || undefined,
        foundationDate: formData.foundationDate || undefined,
        memberCount: 0,
        longitude: formData.longitude || 0,
        latitude: formData.latitude || 0,
      },
      { onSuccess: closeModal }
    );
  };

  const handleUpdate = () => {
    if (!selectedChurch?.id || !formData.name) return;
    updateChurch.mutate(
      {
        id: selectedChurch.id,
        data: {
          name: formData.name,
          cityId: formData.cityId,
          phone: formData.phone,
          email: formData.email,
          pastorId: formData.pastorId || undefined,
          foundationDate: formData.foundationDate,
          latitude: formData.latitude,
          longitude: formData.longitude,
        },
      },
      { onSuccess: closeModal }
    );
  };

  const handleDelete = () => {
    if (!selectedChurch?.id) return;
    deleteChurch.mutate(selectedChurch.id, { onSuccess: closeModal });
  };

  // Table config
  const columns = [
    { key: 'name' as const, label: 'Nombre' },
    {
      key: 'pastor' as const,
      label: 'Pastor',
      render: (_: unknown, item: ChurchSummary) =>
        item.pastor && typeof item.pastor === 'object'
          ? `${item.pastor.firstName} ${item.pastor.lastName}`
          : '-',
    },
    {
      key: 'states' as const,
      label: 'Departamento',
      render: (_: unknown, item: ChurchSummary) => item.states?.name || '-',
    },
    {
      key: 'city' as const,
      label: 'Ciudad',
      render: (_: unknown, item: ChurchSummary) => item.city?.name || '-',
    },
  ];

  const tableActions = [
    { icon: 'view' as const, label: 'Ver', onClick: (c: ChurchSummary) => openModal('view', c), variant: 'secondary' as const },
    { icon: 'edit' as const, label: 'Editar', onClick: (c: ChurchSummary) => openModal('edit', c), variant: 'primary' as const },
    { icon: 'delete' as const, label: 'Eliminar', onClick: (c: ChurchSummary) => openModal('delete', c), variant: 'danger' as const },
  ];

  const isMutating = createChurch.isPending || updateChurch.isPending;

  return (
    <div className="container mx-auto px-2">
      <PageHeader
        title="Iglesias Hijas"
        subtitle="Gestiona todas las iglesias hijas de tu organización desde este panel."
        actions={<Button variant="primary" onClick={() => openModal('create')}>+ Nueva Iglesia</Button>}
      />

      <Table<ChurchSummary>
        data={Array.isArray(churches) ? churches : (churches?.content ?? [])}
        columns={columns}
        actions={tableActions}
        loading={isLoading}
        pagination={{ mode: 'auto', pageSize: 10 }}
      />

      <ChurchFormModal
        isOpen={modalMode === 'create' || modalMode === 'edit'}
        mode={modalMode === 'edit' ? 'edit' : 'create'}
        formData={formData}
        onFormChange={handleFormChange}
        onSave={modalMode === 'create' ? handleCreate : handleUpdate}
        onClose={closeModal}
        isLoading={modalMode === 'edit' && isLoadingDetails}
        isSaving={isMutating}
        states={states}
        cities={cities}
      />

      <ChurchViewModal
        isOpen={modalMode === 'view'}
        church={selectedChurch!!}
        churchDetails={churchDetails!!}
        isLoading={isLoadingDetails}
        onEdit={() => selectedChurch && openModal('edit', selectedChurch)}
        onClose={closeModal}
      />

      <ChurchDeleteModal
        isOpen={modalMode === 'delete'}
        churchName={selectedChurch?.name || ''}
        onConfirm={handleDelete}
        onClose={closeModal}
        isDeleting={deleteChurch.isPending}
      />
    </div>
  );
}