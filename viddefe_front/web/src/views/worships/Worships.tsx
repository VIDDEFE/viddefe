import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Worship } from '../../models';
import { Button, PageHeader, Table } from '../../components/shared';
import {
  WorshipFormModal,
  WorshipDeleteModal,
  initialWorshipFormData,
  type WorshipFormData,
} from '../../components/worships';
import {
  useWorshipMeetings,
  useWorshipMeeting,
  useWorshipMeetingTypes,
  useCreateWorship,
  useUpdateWorship,
  useDeleteWorship,
} from '../../hooks';
import { useAppContext } from '../../context/AppContext';
import { WorshipPermission } from '../../services/userService';
import type { SortConfig } from '../../services/api';
import { toISOStringWithOffset, toDatetimeLocal, formatDateForDisplay } from '../../utils/helpers';

type ModalMode = 'create' | 'edit' | 'delete' | null;

const DEFAULT_PAGE_SIZE = 10;

// Helper para formatear fecha para mostrar en tabla
function formatDateTime(isoDate: string): string {
  return formatDateForDisplay(isoDate, 'short');
}

export default function Worships() {
  const { hasPermission } = useAppContext();
  const navigate = useNavigate();

  // Permisos de cultos
  const canCreate = hasPermission(WorshipPermission.ADD);
  const canView = hasPermission(WorshipPermission.VIEW);
  const canEdit = hasPermission(WorshipPermission.EDIT);
  const canDelete = hasPermission(WorshipPermission.DELETE);

  // Estado de paginación
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);

  // Estado de ordenamiento
  const [sortConfig, setSortConfig] = useState<SortConfig | undefined>(undefined);

  // Data fetching con paginación y ordenamiento
  const { data: worshipsData, isLoading } = useWorshipMeetings({
    page: currentPage,
    size: pageSize,
    sort: sortConfig,
  });
  const { data: worshipTypes } = useWorshipMeetingTypes();

  // Modal state
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [selectedWorship, setSelectedWorship] = useState<Worship | null>(null);
  const [formData, setFormData] = useState<WorshipFormData>(initialWorshipFormData);
  const [formErrors, setFormErrors] = useState<Partial<Record<keyof WorshipFormData, string>>>({});

  // Get details for selected worship
  const { data: worshipDetails, isLoading: isLoadingDetails } = useWorshipMeeting(
    selectedWorship?.id
  );

  // Mutations
  const createWorship = useCreateWorship();
  const updateWorship = useUpdateWorship();
  const deleteWorship = useDeleteWorship();

  // Track if form was already populated to prevent overwriting user changes
  const [formPopulated, setFormPopulated] = useState(false);

  // Load worship details when editing
  useEffect(() => {
    if (!worshipDetails || !selectedWorship || modalMode !== 'edit')
      return;
    if (formPopulated) return;

    setFormData({
      name: worshipDetails.name ?? '',
      description: worshipDetails.description ?? '',  
      scheduledDate: toDatetimeLocal(worshipDetails.scheduledDate),
      worshipTypeId: worshipDetails.worshipType?.id ?? '',
    });

    setFormPopulated(true);
  }, [worshipDetails, modalMode, formPopulated, selectedWorship]);

  // Modal handlers
  const resetForm = () => {
    setFormData(initialWorshipFormData);
    setSelectedWorship(null);
    setFormPopulated(false);
    setFormErrors({});
  };

  const openModal = (mode: ModalMode, worship?: Worship) => {
    if (worship) {
      setSelectedWorship(worship);
      setFormPopulated(mode !== 'edit');
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
  const handleFormChange = (patch: Partial<WorshipFormData>) => {
    setFormData((prev) => ({ ...prev, ...patch }));
    // Clear errors when user starts typing
    const errorKeys = Object.keys(patch) as (keyof WorshipFormData)[];
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
    const errors: Partial<Record<keyof WorshipFormData, string>> = {};

    if (!formData.name.trim()) {
      errors.name = 'El nombre es requerido';
    } else if (formData.name.length > 120) {
      errors.name = 'El nombre no puede exceder 120 caracteres';
    }

    if (formData.description && formData.description.length > 500) {
      errors.description = 'La descripción no puede exceder 500 caracteres';
    }

    if (!formData.scheduledDate) {
      errors.scheduledDate = 'La fecha programada es requerida';
    }

    if (!formData.worshipTypeId) {
      errors.worshipTypeId = 'El tipo de culto es requerido';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // CRUD handlers
  const handleCreate = () => {
    if (!validateForm()) return;

    // Convertir a ISO-8601 con offset de timezone para el backend
    // El backend REQUIERE el offset (ej: "2026-01-15T10:00:00-05:00")
    createWorship.mutate(
      {
        name: formData.name.trim(),
        description: formData.description?.trim() || undefined,
        scheduledDate: toISOStringWithOffset(formData.scheduledDate),
        worshipTypeId: formData.worshipTypeId as number,
      },
      { onSuccess: closeModal }
    );
  };

  const handleUpdate = () => {
    if (!selectedWorship?.id || !validateForm()) return;

    // Convertir a ISO-8601 con offset de timezone para el backend
    updateWorship.mutate(
      {
        id: selectedWorship.id,
        data: {
          name: formData.name.trim(),
          description: formData.description?.trim() || undefined,
          scheduledDate: toISOStringWithOffset(formData.scheduledDate),
          worshipTypeId: formData.worshipTypeId as number,
        },
      },
      { onSuccess: closeModal }
    );
  };

  const handleDelete = () => {
    if (!selectedWorship?.id) return;
    deleteWorship.mutate(selectedWorship.id, { onSuccess: closeModal });
  };

  // Table config
  const columns = [
    { key: 'name' as const, label: 'Nombre', sortable: true },
    {
      key: 'worshipType' as const,
      label: 'Tipo',
      render: (_: unknown, item: Worship) => (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
          {item.worshipType?.name || '-'}
        </span>
      ),
    },
    {
      key: 'scheduledDate' as const,
      label: 'Fecha Programada',
      sortable: true,
      render: (value: unknown) => formatDateTime(value as string),
    },
    {
      key: 'creationDate' as const,
      label: 'Creado',
      sortable: true,
      render: (value: unknown) => formatDateTime(value as string),
      hideOnMobile: true,
    },
  ];

  // Build actions based on permissions
  const tableActions = [
    ...(canView
      ? [
          {
            icon: 'view' as const,
            label: 'Ver Detalle',
            onClick: (w: Worship) => navigate(`/worships/${w.id}`),
            variant: 'secondary' as const,
          },
        ]
      : []),
    ...(canEdit
      ? [
          {
            icon: 'edit' as const,
            label: 'Editar',
            onClick: (w: Worship) => openModal('edit', w),
            variant: 'primary' as const,
          },
        ]
      : []),
    ...(canDelete
      ? [
          {
            icon: 'delete' as const,
            label: 'Eliminar',
            onClick: (w: Worship) => openModal('delete', w),
            variant: 'danger' as const,
          },
        ]
      : []),
  ];

  const isMutating = createWorship.isPending || updateWorship.isPending;

  // Datos para la tabla
  const worshipsArray = Array.isArray(worshipsData)
    ? worshipsData
    : (worshipsData?.content ?? []);

  // Información de paginación del servidor
  const paginationData =
    worshipsData && !Array.isArray(worshipsData)
      ? {
          totalPages: worshipsData.totalPages,
          totalElements: worshipsData.totalElements,
          currentPage: worshipsData.number,
          pageSize: worshipsData.size,
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
        title="Cultos"
        subtitle="Gestiona los cultos y servicios de adoración de tu iglesia."
        actions={
          canCreate && (
            <Button variant="primary" onClick={() => openModal('create')}>
              + Nuevo Culto
            </Button>
          )
        }
      />

      <div className="animate-fadeIn">
        <Table<Worship>
          data={worshipsArray}
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
      </div>

      {/* Modal de Crear/Editar */}
      <WorshipFormModal
        isOpen={modalMode === 'create' || modalMode === 'edit'}
        mode={modalMode === 'edit' ? 'edit' : 'create'}
        formData={formData}
        onFormChange={handleFormChange}
        onSave={modalMode === 'create' ? handleCreate : handleUpdate}
        onClose={closeModal}
        isLoading={modalMode === 'edit' && isLoadingDetails}
        isSaving={isMutating}
        worshipTypes={worshipTypes}
        errors={formErrors}
      />

      {/* Modal de Eliminar */}
      <WorshipDeleteModal
        isOpen={modalMode === 'delete'}
        worshipName={selectedWorship?.name || ''}
        onConfirm={handleDelete}
        onClose={closeModal}
        isDeleting={deleteWorship.isPending}
      />
    </div>
  );
}
