import { useState, useEffect } from 'react';
import type { ChurchSummary } from '../../models';
import { Button, PageHeader, Table } from '../../components/shared';
import { type ChurchFormData, initialChurchFormData } from '../../components/churches/ChurchForm';
import { useChurchChildren, useStates, useCities, useCreateChildrenChurch, useUpdateChurch, useDeleteChurch, useChurch } from '../../hooks';
import { useAppContext } from '../../context/AppContext';
import ChurchFormModal from '../../components/churches/ChurchFormModal';
import ChurchViewModal from '../../components/churches/ChurchViewModal';
import ChurchDeleteModal from '../../components/churches/ChurchDeleteModal';
import ChurchesMap from '../../components/churches/ChurchesMap';
import { FiMap, FiList } from 'react-icons/fi';
import { ChurchPermission } from '../../services/userService';
import type { SortConfig } from '../../services/api';

type ModalMode = 'create' | 'edit' | 'view' | 'delete' | null;
type ViewMode = 'table' | 'map';

const DEFAULT_PAGE_SIZE = 10;

export default function Churches() {
  const { user, hasPermission } = useAppContext();
  const churchId = user?.church.id;

  // Permisos de iglesias
  const canCreate = hasPermission(ChurchPermission.ADD);
  const canView = hasPermission(ChurchPermission.VIEW);
  const canEdit = hasPermission(ChurchPermission.EDIT);
  const canDelete = hasPermission(ChurchPermission.DELETE);

  // View mode state
  const [viewMode, setViewMode] = useState<ViewMode>('table');

  // Estado de paginación
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);

  // Estado de ordenamiento
  const [sortConfig, setSortConfig] = useState<SortConfig | undefined>(undefined);

  // Data fetching con paginación y ordenamiento
  const { data: churches, isLoading } = useChurchChildren(churchId, { 
    page: currentPage, 
    size: pageSize,
    sort: sortConfig 
  });
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

  const createModal = () => {
    resetForm();
    setModalMode('create');
    setViewMode('table');
  }

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

  // Construir acciones basadas en permisos
  const tableActions = [
    ...(canView ? [{ icon: 'view' as const, label: 'Ver', onClick: (c: ChurchSummary) => openModal('view', c), variant: 'secondary' as const }] : []),
    ...(canEdit ? [{ icon: 'edit' as const, label: 'Editar', onClick: (c: ChurchSummary) => openModal('edit', c), variant: 'primary' as const }] : []),
    ...(canDelete ? [{ icon: 'delete' as const, label: 'Eliminar', onClick: (c: ChurchSummary) => openModal('delete', c), variant: 'danger' as const }] : []),
  ];

  const isMutating = createChurch.isPending || updateChurch.isPending;

  // Datos para el mapa y tabla
  const churchesArray = Array.isArray(churches) ? churches : (churches?.content ?? []);
  
  // Información de paginación del servidor
  const paginationData = churches && !Array.isArray(churches) ? {
    totalPages: churches.totalPages,
    totalElements: churches.totalElements,
    currentPage: churches.number,
    pageSize: churches.size
  } : null;

  // Handlers de paginación
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Resetear a primera página
  };

  // Handler para cambio de ordenamiento
  const handleSortChange = (sort: SortConfig | undefined) => {
    setSortConfig(sort);
    setCurrentPage(0); // Resetear a primera página al ordenar
  };

  return (
    <div className="container mx-auto px-2">
      <PageHeader
        title="Iglesias Hijas"
        subtitle="Gestiona todas las iglesias hijas de tu organización desde este panel."
        actions={
          <div className="flex items-center gap-2">
            {/* Toggle vista */}
            <div className="flex items-center bg-neutral-100 rounded-lg p-1">
              <button
                onClick={() => setViewMode('table')}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-all duration-200 ${
                  viewMode === 'table'
                    ? 'bg-white text-primary-700 shadow-sm'
                    : 'text-neutral-600 hover:text-neutral-800'
                }`}
              >
                <FiList className="w-4 h-4" />
                <span>Tabla</span>
              </button>
              <button
                onClick={() => setViewMode('map')}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-all duration-200 ${
                  viewMode === 'map'
                    ? 'bg-white text-primary-700 shadow-sm'
                    : 'text-neutral-600 hover:text-neutral-800'
                }`}
              >
                <FiMap className="w-4 h-4" />
                <span>Mapa</span>
              </button>
            </div>
            {canCreate && <Button variant="primary" onClick={createModal}>+ Nueva Iglesia</Button>}
          </div>
        }
      />

      {/* Vista de Mapa */}
      {viewMode === 'map' && (
        <div className="mb-6 animate-fadeIn">
          <ChurchesMap
            churches={churchesArray}
            height={600}
            onChurchSelect={(church) => openModal('view', church)}
          />
        </div>
      )}

      {/* Vista de Tabla */}
      {viewMode === 'table' && (
        <div className="animate-fadeIn">
          <Table<ChurchSummary>
            data={churchesArray}
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
        </div>
      )}

      {
        viewMode == 'table' &&
      (<><ChurchFormModal
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
      </>)}
    </div>
  );
}