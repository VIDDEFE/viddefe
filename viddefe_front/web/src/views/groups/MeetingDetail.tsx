import { useCallback, useMemo, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, PageHeader, Avatar, Switch, Table } from '../../components/shared';
import { 
  useMyHomeGroup, 
  useMeeting, 
  useMeetingAttendance, 
  useRegisterMeetingAttendance,
  useEventOfferings,
  useOfferingTypes,
  useCreateOffering,
  useUpdateOffering,
  useDeleteOffering
} from '../../hooks';
import type { MeetingAttendance, Offering, CreateOfferingDto, UpdateOfferingDto } from '../../models';
import { FiArrowLeft, FiCheck, FiX, FiFileText, FiCalendar, FiClock, FiUsers } from 'react-icons/fi';
import {
  OfferingsSection,
  OfferingModal,
  DeleteOfferingModal,
  type OfferingTableItem,
  type OfferingFormData,
} from '../worships/components';
import { formatDateForDisplay } from '../../utils/helpers';

interface AttendanceTableItem {
  id: string;
  fullName: string;
  phone: string;
  avatar?: string;
  typePerson: string;
  status: string;
  isPresent: boolean;
  peopleId: string;
}

export default function MeetingDetail() {
  const { id: meetingId } = useParams<{ id: string }>();
  const navigate = useNavigate();

  // Obtener mi grupo para recuperar groupId
  const { data: myGroup } = useMyHomeGroup();
  const groupId = myGroup?.homeGroup?.id;

  // Datos base de la reunión
  const { data: meeting, isLoading, error } = useMeeting(groupId, meetingId);

  // Paginación asistencia
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Paginación ofrendas
  const [offeringPage, setOfferingPage] = useState(0);
  const [offeringPageSize, setOfferingPageSize] = useState(10);
  const [offeringViewMode, setOfferingViewMode] = useState<'table' | 'cards'>('table');

  // Asistencia
  const { data: attendanceData, isLoading: loadingAttendance } = useMeetingAttendance(
    groupId,
    meetingId,
    { page, size: pageSize }
  );
  const registerAttendance = useRegisterMeetingAttendance(groupId, meetingId);

  // Ofrendas
  const { data: offeringsData, isLoading: loadingOfferings } = useEventOfferings(
    meetingId,
    { page: offeringPage, size: offeringPageSize }
  );
  const { data: offeringTypes = [] } = useOfferingTypes();
  const createOffering = useCreateOffering(meetingId);
  const updateOffering = useUpdateOffering(meetingId);
  const deleteOffering = useDeleteOffering(meetingId);

  // Modal de ofrendas
  const [offeringModalOpen, setOfferingModalOpen] = useState(false);
  const [editingOffering, setEditingOffering] = useState<OfferingTableItem | null>(null);
  const [offeringFormData, setOfferingFormData] = useState<OfferingFormData>({
    amount: '',
    peopleId: '',
    offeringTypeId: '',
  });

  // Modal de eliminación
  const [deletingOfferingId, setDeletingOfferingId] = useState<string | null>(null);

  const attendanceTableData: AttendanceTableItem[] = useMemo(() =>
    (attendanceData?.content ?? []).map((record: MeetingAttendance) => ({
      id: record.people.id,
      fullName: `${record.people.firstName} ${record.people.lastName}`,
      phone: record.people.phone || '-',
      avatar: record.people.avatar,
      typePerson: record.people.typePerson?.name || '-',
      status: record.status,
      isPresent: record.status === 'PRESENT',
      peopleId: record.people.id,
    })), [attendanceData?.content]);

  // Usar conteos del meeting (vienen del endpoint /meetings/{id})
  // totalAttendance = total de personas en la asistencia
  // presentCount/absentCount = conteos específicos
  const totalElements = meeting?.totalAttendance ?? attendanceData?.totalElements ?? 0;
  const totalPages = attendanceData?.totalPages ?? 0;
  const presentCount = meeting?.presentCount ?? 0;
  const absentCount = meeting?.absentCount ?? 0;

  // Transformar ofrendas para la tabla
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

  const handleGoBack = useCallback(() => navigate('/my-group'), [navigate]);

  const handleToggleAttendance = useCallback((personId: string) => {
    if (!meetingId) return;
    registerAttendance.mutate({ peopleId: personId, eventId: meetingId });
  }, [meetingId, registerAttendance]);

  // ========== OFFERING HANDLERS ==========
  const handleOpenOfferingModal = useCallback((offering?: OfferingTableItem) => {
    if (offering) {
      setEditingOffering(offering);
      setOfferingFormData({
        amount: offering.amount.toString(),
        peopleId: offering.peopleId || '',
        offeringTypeId: offering.offeringTypeId.toString(),
      });
    } else {
      setEditingOffering(null);
      setOfferingFormData({
        amount: '',
        peopleId: '',
        offeringTypeId: offeringTypes[0]?.id.toString() || '',
      });
    }
    setOfferingModalOpen(true);
  }, [offeringTypes]);

  const handleCloseOfferingModal = useCallback(() => {
    setOfferingModalOpen(false);
    setEditingOffering(null);
    setOfferingFormData({ amount: '', peopleId: '', offeringTypeId: '' });
  }, []);

  const handleSaveOffering = useCallback(() => {
    if (!meetingId || !offeringFormData.amount || !offeringFormData.offeringTypeId) return;

    const amount = Number.parseFloat(offeringFormData.amount);
    if (Number.isNaN(amount) || amount <= 0) return;

    const baseData = {
      eventId: meetingId,
      amount,
      offeringTypeId: Number.parseInt(offeringFormData.offeringTypeId, 10),
      ...(offeringFormData.peopleId && { peopleId: offeringFormData.peopleId }),
    };

    if (editingOffering) {
      const data: UpdateOfferingDto = { id: editingOffering.id, ...baseData };
      updateOffering.mutate(data, { onSuccess: handleCloseOfferingModal });
    } else {
      const data: CreateOfferingDto = baseData;
      createOffering.mutate(data, { onSuccess: handleCloseOfferingModal });
    }
  }, [meetingId, offeringFormData, editingOffering, createOffering, updateOffering, handleCloseOfferingModal]);

  const handleDeleteOffering = useCallback((offeringId: string) => {
    setDeletingOfferingId(offeringId);
  }, []);

  const handleConfirmDeleteOffering = useCallback(() => {
    if (deletingOfferingId) {
      deleteOffering.mutate(deletingOfferingId, {
        onSuccess: () => setDeletingOfferingId(null),
      });
    }
  }, [deletingOfferingId, deleteOffering]);

  const columns = [
    {
      key: 'fullName' as const,
      label: 'Persona',
      priority: 1,
      render: (_: AttendanceTableItem[keyof AttendanceTableItem], item: AttendanceTableItem) => (
        <div className="flex items-center gap-3">
          <Avatar src={item.avatar} name={item.fullName} size="sm" />
          <div>
            <p className="font-medium text-neutral-800">{item.fullName}</p>
            <p className="text-xs text-neutral-500 md:hidden">{item.phone}</p>
          </div>
        </div>
      ),
    },
    { key: 'phone' as const, label: 'Teléfono', priority: 3, hideOnMobile: true },
    {
      key: 'typePerson' as const,
      label: 'Tipo',
      priority: 4,
      hideOnMobile: true,
      render: (value: AttendanceTableItem[keyof AttendanceTableItem]) => {
        const str = String(value ?? '-');
        if (str === '-') return <span className="text-neutral-400">-</span>;
        return (
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-violet-100 text-violet-700">{str}</span>
        );
      },
    },
    {
      key: 'status' as const,
      label: 'Estado',
      priority: 2,
      render: (_: AttendanceTableItem[keyof AttendanceTableItem], item: AttendanceTableItem) => {
        const isPresent = item.status === 'PRESENT';
        return (
          <div className="flex items-center justify-center gap-2">
            <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium ${isPresent ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
              {isPresent ? <FiCheck size={12} /> : <FiX size={12} />}
              {isPresent ? 'Presente' : 'Ausente'}
            </span>
            <Switch checked={isPresent} onChange={() => handleToggleAttendance(item.peopleId)} size="sm" />
          </div>
        );
      },
    },
  ];

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando reunión...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !meeting) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-red-600 text-2xl">!</span>
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">Error al cargar reunión</h2>
            <p className="text-neutral-600 mb-4">{(error as Error)?.message || 'No se pudo encontrar la reunión solicitada'}</p>
            <Button variant="secondary" onClick={handleGoBack}>
              <span className="flex items-center gap-2"><FiArrowLeft size={16}/>Volver</span>
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title={meeting.name}
        subtitle={`Tipo: ${meeting.type?.name ?? '-'}`}
        actions={
          <Button variant="secondary" onClick={handleGoBack}>
            <span className="flex items-center gap-2"><FiArrowLeft size={16}/>Volver</span>
          </Button>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fadeIn">
        {/* Columna izquierda: información y resumen */}
        <div className="space-y-6">
          {/* Información General */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiFileText className="text-primary-600" />
              Información General
            </h3>
            <div className="space-y-4">
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Nombre
                </span>
                <p className="text-neutral-800 font-medium mt-1">{meeting.name}</p>
              </div>
              {meeting.description && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                    Descripción
                  </span>
                  <p className="text-neutral-700 mt-1 whitespace-pre-wrap">{meeting.description}</p>
                </div>
              )}
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Tipo de Reunión
                </span>
                <p className="mt-1">
                  <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-violet-100 text-violet-800">
                    {meeting.type?.name ?? '-'}
                  </span>
                </p>
              </div>
            </div>
          </Card>

          {/* Fecha y Hora */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiCalendar className="text-primary-600" />
              Fecha y Hora
            </h3>
            <div className="space-y-4">
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Fecha Programada
                </span>
                <p className="text-neutral-800 mt-1 capitalize">
                  {formatDateForDisplay(meeting.date, 'date')}
                </p>
                <div className="flex items-center gap-2 mt-1 text-neutral-600">
                  <FiClock size={14} />
                  <span className="text-sm">{formatDateForDisplay(meeting.date, 'time')}</span>
                </div>
              </div>
            </div>
          </Card>

          {/* Resumen de Asistencia */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiUsers className="text-primary-600" />
              Resumen de Asistencia
            </h3>
            <div className="mb-4">
              <div className="flex justify-between text-sm mb-1">
                <span className="text-neutral-600">Asistencia</span>
                <span className="font-medium text-neutral-800">
                  {totalElements === 0 ? '0%' : Math.round((presentCount / totalElements) * 100)}%
                </span>
              </div>
              <div className="w-full bg-neutral-200 rounded-full h-2.5">
                <div className="bg-green-500 h-2.5 rounded-full transition-all duration-300" style={{ width: `${totalElements === 0 ? 0 : Math.round((presentCount / totalElements) * 100)}%` }} />
              </div>
            </div>
            <div className="grid grid-cols-3 gap-3">
              <StatCard value={totalElements} label="Total" variant="neutral" />
              <StatCard value={presentCount} label="Presentes" variant="success" />
              <StatCard value={absentCount} label="Ausentes" variant="danger" />
            </div>
          </Card>
        </div>

        {/* Columna derecha: Lista */}
        <div className="lg:col-span-2">
          <Card className="h-full flex flex-col">
            <Table<AttendanceTableItem>
              data={attendanceTableData}
              columns={columns}
              loading={loadingAttendance && attendanceTableData.length === 0}
              title={`Lista de Asistencia (${totalElements})`}
              viewMode={'table'}
              pagination={{
                mode: 'manual',
                currentPage: page,
                totalPages,
                totalElements,
                pageSize,
                onPageChange: setPage,
                onPageSizeChange: setPageSize,
              }}
            />
          </Card>
        </div>
      </div>

      {/* ========== SECCIÓN DE OFRENDAS ========== */}
      <div className="mt-6 animate-fadeIn">
        <OfferingsSection
          offeringTableData={offeringTableData}
          offeringTotalElements={offeringsData?.offerings?.totalElements ?? 0}
          offeringTotalPages={offeringsData?.offerings?.totalPages ?? 0}
          totalOfferingsAmount={totalOfferingsAmount}
          analytics={offeringsData?.analitycs ?? null}
          isLoading={loadingOfferings}
          viewMode={offeringViewMode}
          onViewModeChange={setOfferingViewMode}
          currentPage={offeringPage}
          pageSize={offeringPageSize}
          onPageChange={setOfferingPage}
          onPageSizeChange={setOfferingPageSize}
          onAddOffering={() => handleOpenOfferingModal()}
          onEditOffering={handleOpenOfferingModal}
          onDeleteOffering={handleDeleteOffering}
        />
      </div>

      {/* ========== MODALES ========== */}
      <OfferingModal
        isOpen={offeringModalOpen}
        onClose={handleCloseOfferingModal}
        formData={offeringFormData}
        onFormChange={setOfferingFormData}
        onSave={handleSaveOffering}
        isEditing={!!editingOffering}
        isSaving={createOffering.isPending || updateOffering.isPending}
        offeringTypes={offeringTypes}
      />

      <DeleteOfferingModal
        isOpen={!!deletingOfferingId}
        onClose={() => setDeletingOfferingId(null)}
        onConfirm={handleConfirmDeleteOffering}
        isDeleting={deleteOffering.isPending}
      />
    </div>
  );
}

interface StatCardProps {
  readonly value: number;
  readonly label: string;
  readonly variant: 'neutral' | 'success' | 'danger';
}

function StatCard({ value, label, variant }: StatCardProps) {
  const variantStyles = {
    neutral: 'bg-neutral-50 text-neutral-800',
    success: 'bg-green-50 text-green-600',
    danger: 'bg-red-50 text-red-600',
  } as const;
  return (
    <div className={`rounded-lg p-3 text-center ${variantStyles[variant]}`}>
      <p className="text-2xl font-bold">{value}</p>
      <p className="text-xs">{label}</p>
    </div>
  );
}
