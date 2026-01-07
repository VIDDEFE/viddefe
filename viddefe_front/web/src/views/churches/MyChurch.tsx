import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMyChurch, useChurchChildren, useStates, useCities, useCreateChildrenChurch, useUpdateChurch, useDeleteChurch, useChurch } from '../../hooks';
import { useAppContext } from '../../context/AppContext';
import { Card, Button, PageHeader, Table } from '../../components/shared';
import ChurchFormModal from '../../components/churches/ChurchFormModal';
import ChurchViewModal from '../../components/churches/ChurchViewModal';
import ChurchDeleteModal from '../../components/churches/ChurchDeleteModal';
import ChurchesMap from '../../components/churches/ChurchesMap';
import { type ChurchFormData, initialChurchFormData } from '../../components/churches/ChurchForm';
import { FiMapPin, FiPhone, FiMail, FiUser, FiCalendar, FiMap, FiList, FiUsers, FiHome } from 'react-icons/fi';
import { ChurchPermission } from '../../services/userService';
import type { ChurchSummary, PersonSummary } from '../../models';
import type { SortConfig } from '../../services/api';
import { formatDateForDisplay } from '../../utils/helpers';

type ModalMode = 'create' | 'edit' | 'view' | 'delete' | null;
type ViewMode = 'table' | 'map';

const DEFAULT_PAGE_SIZE = 10;

export default function MyChurch() {
  const navigate = useNavigate();
  const { data: myChurch, isLoading: isLoadingMyChurch, error } = useMyChurch();
  const { hasPermission } = useAppContext();

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

  // Mapeo de columnas
  const columnToJpaPath: Record<string, string> = {
    name: 'name',
    pastor: 'pastor.lastName',
    states: 'city.states.name',
    city: 'city.name',
  };

  const sortConfigForBackend = useMemo(() => {
    if (!sortConfig) return undefined;
    const jpaPath = columnToJpaPath[sortConfig.field] || sortConfig.field;
    return { field: jpaPath, direction: sortConfig.direction };
  }, [sortConfig]);

  // Obtener iglesias hijas
  const { data: childrenChurches, isLoading: isLoadingChildren } = useChurchChildren(
    myChurch?.id, 
    { 
      page: currentPage, 
      size: pageSize,
      sort: sortConfigForBackend 
    }
  );
  const { data: states } = useStates();

  // Modal state
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [selectedChurch, setSelectedChurch] = useState<ChurchSummary | null>(null);
  const [formData, setFormData] = useState<ChurchFormData>(initialChurchFormData);

  // Get details for selected church
  const { data: churchDetails, isLoading: isLoadingDetails } = useChurch(selectedChurch?.id);
  const { data: cities } = useCities(formData.stateId);

  // Mutations
  const createChurch = useCreateChildrenChurch(myChurch?.id);
  const updateChurch = useUpdateChurch();
  const deleteChurch = useDeleteChurch();

  // Track if form was already populated
  const [formPopulated, setFormPopulated] = useState(false);

  // Load church details when editing/viewing
  useEffect(() => {
    if (!churchDetails || !selectedChurch || !(modalMode === 'edit' || modalMode === 'view')) return;
    if (churchDetails.id !== selectedChurch.id) return;
    if (formPopulated && modalMode === 'edit') return;
    
    setFormData({
      name: churchDetails.name ?? '',
      email: churchDetails.email ?? '',
      phone: churchDetails.phone ?? '',
      foundationDate: churchDetails.foundationDate ?? '',
      latitude: churchDetails.latitude ?? undefined,
      longitude: churchDetails.longitude ?? undefined,
      pastorId: churchDetails.pastor?.id ?? '',
      pastor: churchDetails?.pastor ?? {} as PersonSummary,
      stateId: churchDetails.states?.id ?? undefined,
      cityId: churchDetails.city?.cityId ?? undefined,
    });
    
    if (modalMode === 'edit') {
      setFormPopulated(true);
    }
  }, [churchDetails, selectedChurch, modalMode, formPopulated]);

  // Handlers for modal operations
  const openModal = (mode: ModalMode, church?: ChurchSummary) => {
    if (church) {
      setSelectedChurch(church);
      setFormPopulated(false);
    } else {
      resetForm();
    }
    setModalMode(mode);
  };

  const resetForm = () => {
    setFormData(initialChurchFormData);
    setSelectedChurch(null);
    setFormPopulated(false);
  };

  const closeModal = () => {
    setModalMode(null);
    resetForm();
  };

  // Handle form changes
  const handleFormChange = (patch: Partial<ChurchFormData>) => {
    setFormData(prev => ({ ...prev, ...patch }));
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

  // Handler para ordenamiento
  const handleSortChange = (sort: SortConfig | undefined) => {
    setSortConfig(sort);
    setCurrentPage(0);
  };

  // Estado de carga inicial
  if (isLoadingMyChurch) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando tu iglesia...</p>
          </div>
        </div>
      </div>
    );
  }

  // Estado de error
  if (error || !myChurch) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FiHome className="text-amber-600 text-2xl" />
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">
              No se encontró tu iglesia
            </h2>
            <p className="text-neutral-600 mb-4">
              {error?.message || 'No se pudo cargar la información de tu iglesia'}
            </p>
            <Button variant="secondary" onClick={() => navigate('/churches')}>
              Ver todas las iglesias
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Table columns
  const columns = [
    { key: 'name' as const, label: 'Nombre', sortable: true },
    {
      key: 'pastor' as const,
      label: 'Pastor',
      sortable: true,
      render: (_: unknown, item: ChurchSummary) =>
        item.pastor && typeof item.pastor === 'object'
          ? `${item.pastor.firstName} ${item.pastor.lastName}`
          : '-',
    },
    {
      key: 'states' as const,
      label: 'Departamento',
      sortable: true,
      render: (_: unknown, item: ChurchSummary) => item.states?.name || '-',
    },
    {
      key: 'city' as const,
      label: 'Ciudad',
      sortable: true,
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

  // Datos para tabla
  const churchesArray = Array.isArray(childrenChurches) ? childrenChurches : (childrenChurches?.content ?? []);
  
  // Información de paginación del servidor
  const paginationData = childrenChurches && !Array.isArray(childrenChurches) ? {
    totalPages: childrenChurches.totalPages,
    totalElements: childrenChurches.totalElements,
    currentPage: childrenChurches.number,
    pageSize: childrenChurches.size
  } : null;

  // Handlers de paginación
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0);
  };

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title="Mi Iglesia"
        subtitle={myChurch.name}
        actions={
          <Button variant="secondary" onClick={() => navigate('/churches')}>
            Ver todas las iglesias
          </Button>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fadeIn mb-8">
        {/* Información de Mi Iglesia */}
        <div className="lg:col-span-1 space-y-6">
          {/* Info General */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiHome className="text-primary-600" />
              Información General
            </h3>

            <div className="space-y-4">
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                  Nombre
                </span>
                <p className="text-neutral-800 font-medium mt-1">{myChurch.name}</p>
              </div>

              {myChurch.phone && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                    Teléfono
                  </span>
                  <div className="flex items-center gap-2 mt-1">
                    <FiPhone className="text-neutral-400" size={16} />
                    <span className="text-neutral-700">{myChurch.phone}</span>
                  </div>
                </div>
              )}

              {myChurch.email && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                    Email
                  </span>
                  <div className="flex items-center gap-2 mt-1">
                    <FiMail className="text-neutral-400" size={16} />
                    <a href={`mailto:${myChurch.email}`} className="text-primary-600 hover:text-primary-700">
                      {myChurch.email}
                    </a>
                  </div>
                </div>
              )}

              {myChurch.foundationDate && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                    Fecha de Fundación
                  </span>
                  <div className="flex items-center gap-2 mt-1">
                    <FiCalendar className="text-neutral-400" size={16} />
                    <span className="text-neutral-700">
                      {formatDateForDisplay(myChurch.foundationDate, 'date')}
                    </span>
                  </div>
                </div>
              )}

              {/* Ubicación */}
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                  Ubicación
                </span>
                <div className="flex items-center gap-2 mt-1">
                  <FiMapPin className="text-neutral-400" size={16} />
                  <span className="text-neutral-700">
                    {myChurch.city?.name && myChurch.states?.name 
                      ? `${myChurch.city.name}, ${myChurch.states.name}` 
                      : 'Sin ubicación'}
                  </span>
                </div>
                {Boolean(myChurch.latitude) && Boolean(myChurch.longitude) && (
                  <a
                    href={`https://www.google.com/maps?q=${myChurch.latitude},${myChurch.longitude}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-primary-600 hover:text-primary-700 text-sm mt-1 inline-block"
                  >
                    Ver en Google Maps →
                  </a>
                )}
              </div>
            </div>
          </Card>

          {/* Pastor */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiUser className="text-primary-600" />
              Pastor
            </h3>

            {myChurch.pastor ? (
              <div className="flex items-center gap-3">
                {myChurch.pastor.avatar ? (
                  <img
                    src={myChurch.pastor.avatar}
                    alt={`${myChurch.pastor.firstName} ${myChurch.pastor.lastName}`}
                    className="w-12 h-12 rounded-full object-cover"
                  />
                ) : (
                  <div className="w-12 h-12 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-semibold text-lg">
                    {myChurch.pastor.firstName?.[0]}
                    {myChurch.pastor.lastName?.[0]}
                  </div>
                )}
                <div>
                  <p className="font-medium text-neutral-800">
                    {myChurch.pastor.firstName} {myChurch.pastor.lastName}
                  </p>
                  {myChurch.pastor.phone && (
                    <p className="text-sm text-neutral-500">{myChurch.pastor.phone}</p>
                  )}
                </div>
              </div>
            ) : (
              <p className="text-neutral-500 text-center py-4">
                Sin pastor asignado
              </p>
            )}
          </Card>

          {/* Estadísticas */}
          {myChurch.memberCount !== undefined && (
            <Card>
              <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
                <FiUsers className="text-primary-600" />
                Estadísticas
              </h3>
              <div className="text-center">
                <p className="text-3xl font-bold text-primary-600">{myChurch.memberCount}</p>
                <p className="text-sm text-neutral-500">Miembros registrados</p>
              </div>
            </Card>
          )}
        </div>

        {/* Iglesias Hijas */}
        <div className="lg:col-span-2">
          <Card className="h-full">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-800 flex items-center gap-2">
                <FiHome className="text-primary-600" />
                Iglesias Hijas
              </h3>
              <div className="flex items-center gap-2">
                {/* Toggle vista tabla/mapa */}
                <div className="flex border border-neutral-200 rounded-lg overflow-hidden">
                  <button
                    onClick={() => setViewMode('table')}
                    className={`px-3 py-2 text-sm flex items-center gap-1 transition-colors ${
                      viewMode === 'table'
                        ? 'bg-primary-50 text-primary-700'
                        : 'bg-white text-neutral-600 hover:bg-neutral-50'
                    }`}
                  >
                    <FiList size={16} />
                    <span className="hidden sm:inline">Lista</span>
                  </button>
                  <button
                    onClick={() => setViewMode('map')}
                    className={`px-3 py-2 text-sm flex items-center gap-1 transition-colors ${
                      viewMode === 'map'
                        ? 'bg-primary-50 text-primary-700'
                        : 'bg-white text-neutral-600 hover:bg-neutral-50'
                    }`}
                  >
                    <FiMap size={16} />
                    <span className="hidden sm:inline">Mapa</span>
                  </button>
                </div>
                {canCreate && (
                  <Button onClick={() => openModal('create')}>
                    Nueva Iglesia
                  </Button>
                )}
              </div>
            </div>

            {viewMode === 'table' ? (
              <Table<ChurchSummary>
                data={churchesArray}
                columns={columns}
                actions={tableActions}
                loading={isLoadingChildren}
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
            ) : (
              <div className="h-96 rounded-lg overflow-hidden border border-neutral-200">
                <ChurchesMap
                  churchId={myChurch.id}
                  height={384}
                  onChurchSelect={(church) => openModal('view', church)}
                />
              </div>
            )}
          </Card>
        </div>
      </div>

      {/* ========== MODALES ========== */}

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
        church={selectedChurch!}
        churchDetails={churchDetails!}
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
