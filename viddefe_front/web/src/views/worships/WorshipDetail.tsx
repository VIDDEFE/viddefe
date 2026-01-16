import { useState, useMemo, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  useWorshipMeeting, 
  useWorshipAttendance, 
  useRegisterAttendance,
  useEventOfferings,
  useOfferingTypes,
  useCreateOffering,
  useUpdateOffering,
  useDeleteOffering,
  useMinistryFunctions
} from '../../hooks';
import { Card, Button, PageHeader, Avatar, Switch, Table } from '../../components/shared';
import { MinistryFunctionsModal } from '../../components/groups';
import { FiArrowLeft, FiCheck, FiX } from 'react-icons/fi';
import type { WorshipAttendance, Offering, CreateOfferingDto, UpdateOfferingDto } from '../../models';
import {
  WorshipInfo,
  WorshipSchedule,
  AttendanceSummary,
  OfferingsSection,
  OfferingModal,
  DeleteOfferingModal,
  MinistryFunctionsSection,
  type AttendanceTableItem,
  type OfferingTableItem,
  type OfferingFormData,
} from './components';

// ============================================================================
// CUSTOM HOOKS
// ============================================================================

/**
 * Hook para manejar la paginación
 */
function usePagination(initialPage = 0, initialSize = 10) {
  const [page, setPage] = useState(initialPage);
  const [pageSize, setPageSize] = useState(initialSize);
  return { page, setPage, pageSize, setPageSize };
}

/**
 * Hook para manejar el estado del modal de ofrendas
 */
function useOfferingModal(offeringTypes: { id: number }[] = []) {
  const [isOpen, setIsOpen] = useState(false);
  const [editingOffering, setEditingOffering] = useState<OfferingTableItem | null>(null);
  const [formData, setFormData] = useState<OfferingFormData>({
    amount: '',
    peopleId: '',
    offeringTypeId: '',
  });

  const open = useCallback((offering?: OfferingTableItem) => {
    if (offering) {
      setEditingOffering(offering);
      setFormData({
        amount: offering.amount.toString(),
        peopleId: offering.peopleId || '',
        offeringTypeId: offering.offeringTypeId.toString(),
      });
    } else {
      setEditingOffering(null);
      setFormData({
        amount: '',
        peopleId: '',
        offeringTypeId: offeringTypes[0]?.id.toString() || '',
      });
    }
    setIsOpen(true);
  }, [offeringTypes]);

  const close = useCallback(() => {
    setIsOpen(false);
    setEditingOffering(null);
    setFormData({ amount: '', peopleId: '', offeringTypeId: '' });
  }, []);

  return {
    isOpen,
    editingOffering,
    formData,
    setFormData,
    open,
    close,
  };
}

// ============================================================================
// MAIN COMPONENT
// ============================================================================

export default function WorshipDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  // Paginación
  const attendancePagination = usePagination(0, 10);
  const offeringPagination = usePagination(0, 10);
  
  // Queries
  const { data: worship, isLoading, error } = useWorshipMeeting(id);
  const { data: attendanceData, isLoading: isLoadingAttendance } = useWorshipAttendance(
    id, 
    { page: attendancePagination.page, size: attendancePagination.pageSize }
  );
  const registerAttendance = useRegisterAttendance(id);
  
  // Queries de ofrendas
  const { data: offeringsData, isLoading: isLoadingOfferings } = useEventOfferings(
    id, 
    { page: offeringPagination.page, size: offeringPagination.pageSize }
  );
  const { data: offeringTypes = [] } = useOfferingTypes();
  const createOffering = useCreateOffering(id);
  const updateOffering = useUpdateOffering(id);
  const deleteOffering = useDeleteOffering(id);

  // Funciones ministeriales
  const { data: ministryFunctions = [], isLoading: loadingMinistryFunctions } = useMinistryFunctions(
    id,
    'TEMPLE_WORHSIP'
  );

  // Modal de ofrendas
  const offeringModal = useOfferingModal(offeringTypes);
  
  // Modal de eliminación
  const [deletingOfferingId, setDeletingOfferingId] = useState<string | null>(null);

  // Modal de funciones ministeriales
  const [ministryFunctionsModalOpen, setMinistryFunctionsModalOpen] = useState(false);

  // View modes
  const [attendanceViewMode, setAttendanceViewMode] = useState<'table' | 'cards'>('table');
  const [offeringViewMode, setOfferingViewMode] = useState<'table' | 'cards'>('table');

  // ============================================================================
  // TRANSFORMERS
  // ============================================================================

  const attendanceTableData: AttendanceTableItem[] = useMemo(() => 
    (attendanceData?.content ?? []).map((record: WorshipAttendance) => ({
      id: record.people.id,
      fullName: `${record.people.firstName} ${record.people.lastName}`,
      phone: record.people.phone || '-',
      avatar: record.people.avatar,
      typePerson: record.people.typePerson?.name || '-',
      status: record.status,
      isPresent: record.status === 'PRESENT',
      peopleId: record.people.id,
    })), [attendanceData?.content]);

  const offeringTableData: OfferingTableItem[] = useMemo(() => 
    (offeringsData?.offerings?.content ?? []).map((offering: Offering, index: number) => ({
      id: offering.id || `${offering.eventId}-${index}`,
      personName: offering.people 
        ? `${offering.people.firstName} ${offering.people.lastName}` 
        : 'Anónimo',
      avatar: offering.people?.avatar,
      typeName: offering.type?.name || '-',
      amount: offering.amount,
      peopleId: offering.people?.id || null,
      offeringTypeId: offering.type?.id || 0,
    })), [offeringsData?.offerings]);

  const totalOfferingsAmount = useMemo(() => 
    offeringTableData.reduce((sum, o) => sum + o.amount, 0), 
    [offeringTableData]
  );

  // ============================================================================
  // HANDLERS
  // ============================================================================

  const handleGoBack = useCallback(() => {
    navigate('/worships');
  }, [navigate]);

  const handleToggleAttendance = useCallback((personId: string) => {
    if (!id) return;
    registerAttendance.mutate({ peopleId: personId, eventId: id });
  }, [id, registerAttendance]);

  const handleSaveOffering = useCallback(() => {
    if (!id || !offeringModal.formData.amount || !offeringModal.formData.offeringTypeId) return;
    
    const amount = Number.parseFloat(offeringModal.formData.amount);
    if (Number.isNaN(amount) || amount <= 0) return;
    
    const baseData = {
      eventId: id,
      amount,
      offeringTypeId: Number.parseInt(offeringModal.formData.offeringTypeId, 10),
      ...(offeringModal.formData.peopleId && { peopleId: offeringModal.formData.peopleId }),
    };

    if (offeringModal.editingOffering) {
      const data: UpdateOfferingDto = { id: offeringModal.editingOffering.id, ...baseData };
      updateOffering.mutate(data, { onSuccess: offeringModal.close });
    } else {
      const data: CreateOfferingDto = baseData;
      createOffering.mutate(data, { onSuccess: offeringModal.close });
    }
  }, [id, offeringModal, createOffering, updateOffering]);

  const handleDeleteOffering = useCallback((offeringId: string) => {
    setDeletingOfferingId(offeringId);
  }, []);
  
  const confirmDeleteOffering = useCallback(() => {
    if (!deletingOfferingId) return;
    deleteOffering.mutate(deletingOfferingId, {
      onSuccess: () => setDeletingOfferingId(null),
    });
  }, [deletingOfferingId, deleteOffering]);

  // ============================================================================
  // RENDER STATES
  // ============================================================================

  if (isLoading) {
    return <LoadingState />;
  }

  if (error || !worship) {
    return <ErrorState error={error} onGoBack={handleGoBack} />;
  }

  // ============================================================================
  // COLUMNS
  // ============================================================================

  const attendanceColumns = [
    {
      key: 'fullName' as const,
      label: 'Persona',
      priority: 1,
      render: (_value: AttendanceTableItem[keyof AttendanceTableItem], item: AttendanceTableItem) => (
        <div className="flex items-center gap-3">
          <Avatar src={item.avatar} name={item.fullName} size="sm" />
          <div>
            <p className="font-medium text-neutral-800">{item.fullName}</p>
            <p className="text-xs text-neutral-500 md:hidden">{item.phone}</p>
          </div>
        </div>
      ),
    },
    {
      key: 'phone' as const,
      label: 'Teléfono',
      priority: 3,
      hideOnMobile: true,
    },
    {
      key: 'typePerson' as const,
      label: 'Tipo',
      priority: 4,
      hideOnMobile: true,
      render: (value: AttendanceTableItem[keyof AttendanceTableItem]) => {
        const strValue = String(value ?? '-');
        if (strValue === '-') return <span className="text-neutral-400">-</span>;
        return (
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-violet-100 text-violet-700">
            {strValue}
          </span>
        );
      },
    },
    {
      key: 'status' as const,
      label: 'Estado',
      priority: 2,
      render: (_value: AttendanceTableItem[keyof AttendanceTableItem], item: AttendanceTableItem) => {
        const isPresent = item.status === 'PRESENT';
        return (
          <div className="flex items-center justify-center gap-2">
            <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium ${
              isPresent ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
            }`}>
              {isPresent ? <FiCheck size={12} /> : <FiX size={12} />}
              {isPresent ? 'Presente' : 'Ausente'}
            </span>
            <Switch
              checked={isPresent}
              onChange={() => handleToggleAttendance(item.peopleId)}
              size="sm"
            />
          </div>
        );
      },
    },
  ];

  // ============================================================================
  // RENDER
  // ============================================================================

  const totalPages = attendanceData?.totalPages ?? 0;
  const totalElements = attendanceData?.totalElements ?? 0;
  const showTableLoading = isLoadingAttendance && attendanceTableData.length === 0;
  const showOfferingLoading = isLoadingOfferings && offeringTableData.length === 0;

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title={worship.name}
        subtitle="Detalle del culto"
        actions={
          <Button variant="secondary" onClick={handleGoBack}>
            <span className="flex items-center gap-2">
              <FiArrowLeft size={16} />
              Volver
            </span>
          </Button>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fadeIn">
        {/* Columna izquierda */}
        <div className="space-y-6">
          <WorshipInfo worship={worship} />
          <WorshipSchedule worship={worship} />
          <AttendanceSummary worship={worship} />
        </div>

        {/* Columna derecha: Lista de asistencia */}
        <div className="lg:col-span-2">
          <Card className="h-full flex flex-col">
            <Table<AttendanceTableItem>
              data={attendanceTableData}
              columns={attendanceColumns}
              loading={showTableLoading}
              title={`Lista de Asistencia (${totalElements})`}
              viewMode={attendanceViewMode}
              onViewModeChange={setAttendanceViewMode}
              pagination={{
                mode: 'manual',
                currentPage: attendancePagination.page,
                totalPages,
                totalElements,
                pageSize: attendancePagination.pageSize,
                onPageChange: attendancePagination.setPage,
                onPageSizeChange: attendancePagination.setPageSize,
              }}
            />
          </Card>
        </div>
      </div>

      {/* Sección de Funciones Ministeriales */}
      <div className="mt-6 animate-fadeIn">
        <MinistryFunctionsSection
          ministryFunctions={ministryFunctions}
          isLoading={loadingMinistryFunctions}
          onManage={() => setMinistryFunctionsModalOpen(true)}
        />
      </div>
      
      {/* Sección de Ofrendas */}
      <div className="mt-6 animate-fadeIn">
        <OfferingsSection
          offeringTableData={offeringTableData}
          offeringTotalElements={offeringsData?.offerings?.totalElements ?? 0}
          offeringTotalPages={offeringsData?.offerings?.totalPages ?? 0}
          totalOfferingsAmount={totalOfferingsAmount}
          analytics={offeringsData?.analitycs ?? null}
          isLoading={showOfferingLoading}
          viewMode={offeringViewMode}
          onViewModeChange={setOfferingViewMode}
          currentPage={offeringPagination.page}
          pageSize={offeringPagination.pageSize}
          onPageChange={offeringPagination.setPage}
          onPageSizeChange={offeringPagination.setPageSize}
          onAddOffering={() => offeringModal.open()}
          onEditOffering={offeringModal.open}
          onDeleteOffering={handleDeleteOffering}
        />
      </div>
      
      {/* Modals */}
      <OfferingModal
        isOpen={offeringModal.isOpen}
        onClose={offeringModal.close}
        formData={offeringModal.formData}
        onFormChange={offeringModal.setFormData}
        onSave={handleSaveOffering}
        isEditing={!!offeringModal.editingOffering}
        isSaving={createOffering.isPending || updateOffering.isPending}
        offeringTypes={offeringTypes}
      />
      
      <DeleteOfferingModal
        isOpen={!!deletingOfferingId}
        onClose={() => setDeletingOfferingId(null)}
        onConfirm={confirmDeleteOffering}
        isDeleting={deleteOffering.isPending}
      />

      {id && (
        <MinistryFunctionsModal
          isOpen={ministryFunctionsModalOpen}
          meetingId={id}
          eventType="TEMPLE_WORHSIP"
          onClose={() => setMinistryFunctionsModalOpen(false)}
        />
      )}
    </div>
  );
}

// ============================================================================
// SUB-COMPONENTS
// ============================================================================

function LoadingState() {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-center min-h-75">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
          <p className="mt-4 text-neutral-600">Cargando detalles del culto...</p>
        </div>
      </div>
    </div>
  );
}

interface ErrorStateProps {
  error: Error | null;
  onGoBack: () => void;
}

function ErrorState({ error, onGoBack }: Readonly<ErrorStateProps>) {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex flex-col items-center justify-center min-h-75">
        <div className="text-center">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-red-600 text-2xl">!</span>
          </div>
          <h2 className="text-xl font-semibold text-neutral-800 mb-2">
            Error al cargar el culto
          </h2>
          <p className="text-neutral-600 mb-4">
            {error?.message || 'No se pudo encontrar el culto solicitado'}
          </p>
          <Button variant="secondary" onClick={onGoBack}>
            <span className="flex items-center gap-2">
              <FiArrowLeft size={16} />
              Volver a Cultos
            </span>
          </Button>
        </div>
      </div>
    </div>
  );
}
