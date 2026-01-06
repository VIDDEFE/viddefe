import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import type { HomeGroup } from '../../models';
import { Button, PageHeader, Table } from '../../components/shared';
import {
  HomeGroupFormModal,
  HomeGroupDeleteModal,
  StrategyManager,
  GroupsMap,
  initialHomeGroupFormData,
  type HomeGroupFormData,
} from '../../components/groups';
import {
  useHomeGroups,
  useHomeGroup,
  useStrategies,
  useCreateHomeGroup,
  useUpdateHomeGroup,
  useDeleteHomeGroup,
} from '../../hooks';
import { useAppContext } from '../../context/AppContext';
import type { SortConfig } from '../../services/api';
import { GroupPermission } from '../../services/userService';
import { FiSettings, FiList, FiMap } from 'react-icons/fi';

type ModalMode = 'create' | 'edit' | 'delete' | 'strategies' | null;
type ViewMode = 'table' | 'map';

const DEFAULT_PAGE_SIZE = 10;

export default function Groups() {
  const { hasPermission } = useAppContext();
  const navigate = useNavigate();

  // Permisos de grupos
  const canCreate = hasPermission(GroupPermission.CREATE);
  const canView = hasPermission(GroupPermission.VIEW);
  const canEdit = hasPermission(GroupPermission.EDIT);
  const canDelete = hasPermission(GroupPermission.DELETE);

  // Navegar al detalle del grupo
  const handleViewDetail = (group: HomeGroup) => {
    navigate(`/groups/${group.id}`);
  };

  // Estado de vista (tabla/mapa)
  const [viewMode, setViewMode] = useState<ViewMode>('table');

  // Estado de paginación
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);

  // Estado de ordenamiento
  const [sortConfig, setSortConfig] = useState<SortConfig | undefined>(undefined);

  // Data fetching
  // Para el mapa, traemos más registros para visualizar todos
  const { data: groupsData, isLoading } = useHomeGroups({
    page: viewMode === 'map' ? 0 : currentPage,
    size: viewMode === 'map' ? 1000 : pageSize,
    sort: viewMode === 'map' ? undefined : sortConfig,
  });
  const { data: strategies, isLoading: isLoadingStrategies } = useStrategies();

  // Modal state
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [selectedGroup, setSelectedGroup] = useState<HomeGroup | null>(null);
  const [formData, setFormData] = useState<HomeGroupFormData>(initialHomeGroupFormData);
  const [formErrors, setFormErrors] = useState<Partial<Record<keyof HomeGroupFormData, string>>>({});

  // Get details for selected group
  const { data: groupDetails, isLoading: isLoadingDetails } = useHomeGroup(selectedGroup?.id);

  // Mutations
  const createGroup = useCreateHomeGroup();
  const updateGroup = useUpdateHomeGroup();
  const deleteGroup = useDeleteHomeGroup();

  // Track if form was already populated
  const [formPopulated, setFormPopulated] = useState(false);

  // Load group details when editing
  useEffect(() => {
    if (!groupDetails || !selectedGroup || modalMode !== 'edit')
      return;
    // Verificar que los datos correspondan al grupo seleccionado
    if (groupDetails.id !== selectedGroup.id) return;
    // Si ya se pobló el form, no sobreescribir
    if (formPopulated) return;

    setFormData({
      name: groupDetails.name ?? '',
      description: groupDetails.description ?? '',
      latitude: groupDetails.latitude ?? '',
      longitude: groupDetails.longitude ?? '',
      leaderId: groupDetails.leader?.id ?? '',
      strategyId: groupDetails.strategy?.id ?? '',
    });

    setFormPopulated(true);
  }, [groupDetails, modalMode, formPopulated, selectedGroup]);

  // Modal handlers
  const resetForm = () => {
    setFormData(initialHomeGroupFormData);
    setSelectedGroup(null);
    setFormPopulated(false);
    setFormErrors({});
  };

  const openModal = (mode: ModalMode, group?: HomeGroup) => {
    if (group) {
      setSelectedGroup(group);
      setFormPopulated(false); // Siempre resetear para permitir cargar nuevos datos
    } else {
      resetForm();
    }
    setModalMode(mode);
  };

  const closeModal = () => {
    setModalMode(null);
    resetForm();
  };

  // Handle form changes
  const handleFormChange = (patch: Partial<HomeGroupFormData>) => {
    setFormData((prev) => ({ ...prev, ...patch }));
    // Clear errors when user starts typing
    const errorKeys = Object.keys(patch) as (keyof HomeGroupFormData)[];
    if (errorKeys.some((key) => formErrors[key])) {
      setFormErrors((prev) => {
        const newErrors = { ...prev };
        errorKeys.forEach((key) => delete newErrors[key]);
        return newErrors;
      });
    }
  };

  // Validation
  const validateForm = (): boolean => {
    const errors: Partial<Record<keyof HomeGroupFormData, string>> = {};

    if (!formData.name.trim()) {
      errors.name = 'El nombre es requerido';
    } else if (formData.name.length < 3) {
      errors.name = 'El nombre debe tener al menos 3 caracteres';
    } else if (formData.name.length > 100) {
      errors.name = 'El nombre no puede exceder 100 caracteres';
    }

    if (formData.latitude === '') {
      errors.latitude = 'La latitud es requerida';
    } else if (typeof formData.latitude === 'number' && (formData.latitude < -90 || formData.latitude > 90)) {
      errors.latitude = 'La latitud debe estar entre -90 y 90';
    }

    if (formData.longitude === '') {
      errors.longitude = 'La longitud es requerida';
    } else if (typeof formData.longitude === 'number' && (formData.longitude < -180 || formData.longitude > 180)) {
      errors.longitude = 'La longitud debe estar entre -180 y 180';
    }

    if (!formData.strategyId) {
      errors.strategyId = 'La estrategia es requerida';
    }

    if (!formData.leaderId) {
      errors.leaderId = 'El líder es requerido';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // CRUD handlers
  const handleCreate = () => {
    if (!validateForm()) return;

    createGroup.mutate(
      {
        name: formData.name.trim(),
        description: formData.description?.trim() || undefined,
        latitude: formData.latitude as number,
        longitude: formData.longitude as number,
        leaderId: formData.leaderId,
        strategyId: formData.strategyId,
      },
      { onSuccess: closeModal }
    );
  };

  const handleUpdate = () => {
    if (!selectedGroup?.id || !validateForm()) return;

    updateGroup.mutate(
      {
        id: selectedGroup.id,
        data: {
          name: formData.name.trim(),
          description: formData.description?.trim() || undefined,
          latitude: formData.latitude as number,
          longitude: formData.longitude as number,
          leaderId: formData.leaderId,
          strategyId: formData.strategyId,
        },
      },
      { onSuccess: closeModal }
    );
  };

  const handleDelete = () => {
    if (!selectedGroup?.id) return;
    deleteGroup.mutate(selectedGroup.id, { onSuccess: closeModal });
  };

  // Table config
  const columns = [
    { key: 'name' as const, label: 'Nombre', sortable: true },
    {
      key: 'strategy' as const,
      label: 'Estrategia',
      render: (_: unknown, item: HomeGroup) => (
        item.strategy ? (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
            {item.strategy.name}
          </span>
        ) : (
          <span className="text-neutral-400">-</span>
        )
      ),
    },
    {
      key: 'leader' as const,
      label: 'Líder',
      render: (_: unknown, item: HomeGroup) => (
        item.leader ? (
          <span className="inline-flex items-center gap-2">
            <span className="w-7 h-7 rounded-full bg-primary-100 flex items-center justify-center text-primary-800 font-medium text-xs">
              {item.leader.firstName?.[0]}{item.leader.lastName?.[0]}
            </span>
            <span className="text-sm">
              {item.leader.firstName} {item.leader.lastName}
            </span>
          </span>
        ) : (
          <span className="text-neutral-400">-</span>
        )
      ),
    },
    {
      key: 'latitude' as const,
      label: 'Coordenadas',
      hideOnMobile: true,
      render: (_: unknown, item: HomeGroup) => (
        <span className="text-sm text-neutral-600">
          {item.latitude?.toFixed(4)}, {item.longitude?.toFixed(4)}
        </span>
      ),
    },
  ];

  // Build actions based on permissions
  const tableActions = [
    ...(canView
      ? [
          {
            icon: 'view' as const,
            label: 'Ver Detalle',
            onClick: (g: HomeGroup) => handleViewDetail(g),
            variant: 'secondary' as const,
          },
        ]
      : []),
    ...(canEdit
      ? [
          {
            icon: 'edit' as const,
            label: 'Editar',
            onClick: (g: HomeGroup) => openModal('edit', g),
            variant: 'primary' as const,
          },
        ]
      : []),
    ...(canDelete
      ? [
          {
            icon: 'delete' as const,
            label: 'Eliminar',
            onClick: (g: HomeGroup) => openModal('delete', g),
            variant: 'danger' as const,
          },
        ]
      : []),
  ];

  const isMutating = createGroup.isPending || updateGroup.isPending;

  // Datos para la tabla
  const groupsArray = Array.isArray(groupsData)
    ? groupsData
    : (groupsData?.content ?? []);

  // Información de paginación del servidor
  const paginationData =
    groupsData && !Array.isArray(groupsData)
      ? {
          totalPages: groupsData.totalPages,
          totalElements: groupsData.totalElements,
          currentPage: groupsData.number,
          pageSize: groupsData.size,
        }
      : null;

  // Handlers de paginación
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0);
  };

  // Handler para cambio de ordenamiento
  const handleSortChange = (sort: SortConfig | undefined) => {
    setSortConfig(sort);
    setCurrentPage(0);
  };

  return (
    <div className="container mx-auto px-2">
      <PageHeader
        title="Grupos"
        subtitle="Gestiona los grupos de hogar y sus estrategias."
        actions={
          <div className="flex items-center gap-2">
            {/* Toggle de vista */}
            <div className="flex items-center bg-neutral-100 rounded-lg p-1">
              <button
                onClick={() => setViewMode('table')}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                  viewMode === 'table'
                    ? 'bg-white text-primary-700 shadow-sm'
                    : 'text-neutral-600 hover:text-neutral-800'
                }`}
              >
                <FiList size={16} />
                <span className="hidden sm:inline">Tabla</span>
              </button>
              <button
                onClick={() => setViewMode('map')}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                  viewMode === 'map'
                    ? 'bg-white text-primary-700 shadow-sm'
                    : 'text-neutral-600 hover:text-neutral-800'
                }`}
              >
                <FiMap size={16} />
                <span className="hidden sm:inline">Mapa</span>
              </button>
            </div>

            <Button
              variant="secondary"
              onClick={() => setModalMode('strategies')}
            >
              <span className="flex items-center gap-2">
                <FiSettings size={16} />
                <span className="hidden sm:inline">Estrategias</span>
              </span>
            </Button>
            {canCreate && (
              <Button variant="primary" onClick={() => openModal('create')}>
                + Nuevo Grupo
              </Button>
            )}
          </div>
        }
      />

      <div className="animate-fadeIn">
        {viewMode === 'table' ? (
          <Table<HomeGroup>
            data={groupsArray}
            columns={columns}
            actions={tableActions}
            loading={isLoading}
            pagination={
              paginationData
                ? {
                    mode: 'manual',
                    currentPage: paginationData.currentPage,
                    totalPages: paginationData.totalPages,
                    totalElements: paginationData.totalElements,
                    pageSize: paginationData.pageSize,
                    onPageChange: handlePageChange,
                    onPageSizeChange: handlePageSizeChange,
                  }
                : { mode: 'auto', pageSize: DEFAULT_PAGE_SIZE }
            }
            sorting={{
              mode: 'manual',
              sortConfig: sortConfig,
              onSortChange: handleSortChange,
            }}
          />
        ) : (
          <GroupsMap
            height={600}
            onGroupSelect={() => {
              // La navegación al detalle se hace desde el mapa con el botón "Ver más"
            }}
            onEditGroup={canEdit ? (group) => openModal('edit', group) : undefined}
            onDeleteGroup={canDelete ? (group) => openModal('delete', group) : undefined}
          />
        )}
      </div>

      {/* Modal de Crear/Editar */}
      <HomeGroupFormModal
        isOpen={modalMode === 'create' || modalMode === 'edit'}
        mode={modalMode === 'edit' ? 'edit' : 'create'}
        formData={formData}
        onFormChange={handleFormChange}
        onSave={modalMode === 'create' ? handleCreate : handleUpdate}
        onClose={closeModal}
        isLoading={modalMode === 'edit' && isLoadingDetails}
        isSaving={isMutating}
        strategies={strategies}
        isLoadingStrategies={isLoadingStrategies}
        errors={formErrors}
      />

      {/* Modal de Eliminar */}
      <HomeGroupDeleteModal
        isOpen={modalMode === 'delete'}
        groupName={selectedGroup?.name || ''}
        onConfirm={handleDelete}
        onClose={closeModal}
        isDeleting={deleteGroup.isPending}
      />

      {/* Modal de Gestión de Estrategias */}
      <StrategyManager
        isOpen={modalMode === 'strategies'}
        onClose={closeModal}
      />
    </div>
  );
}
