import { useState, useMemo } from 'react';
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
  usePeople
} from '../../hooks';
import { Card, Button, PageHeader, Avatar, Switch, Table, Modal } from '../../components/shared';
import { 
  FiArrowLeft, 
  FiCalendar, 
  FiClock, 
  FiUsers, 
  FiCheck, 
  FiX, 
  FiFileText,
  FiDollarSign,
  FiPlus,
  FiEdit2,
  FiTrash2
} from 'react-icons/fi';
import type { WorshipAttendance, Offering, CreateOfferingDto, UpdateOfferingDto } from '../../models';

// Helper para formatear solo fecha
function formatDate(isoDate: string): string {
  try {
    const date = new Date(isoDate);
    return date.toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  } catch {
    return isoDate;
  }
}

// Helper para formatear solo hora
function formatTime(isoDate: string): string {
  try {
    const date = new Date(isoDate);
    return date.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return isoDate;
  }
}

// Tipo extendido para la tabla (necesita id)
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

// Tipo extendido para la tabla de ofrendas
interface OfferingTableItem {
  id: string;
  personName: string;
  avatar?: string;
  typeName: string;
  amount: number;
  peopleId: string | null;
  offeringTypeId: number;
}

export default function WorshipDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  // Estados de paginación para asistencia
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  
  // Estados de paginación para ofrendas
  const [offeringPage, setOfferingPage] = useState(0);
  const [offeringPageSize, setOfferingPageSize] = useState(10);
  
  // Queries de asistencia
  const { data: worship, isLoading, error } = useWorshipMeeting(id);
  const { 
    data: attendanceData, 
    isLoading: isLoadingAttendance
  } = useWorshipAttendance(id, { page, size: pageSize });
  const registerAttendance = useRegisterAttendance(id);
  
  // Queries de ofrendas
  const { 
    data: offeringsData, 
    isLoading: isLoadingOfferings 
  } = useEventOfferings(id, { page: offeringPage, size: offeringPageSize });
  const { data: offeringTypes } = useOfferingTypes();
  const { data: peopleData } = usePeople({ page: 0, size: 100 });
  const createOffering = useCreateOffering(id);
  const updateOffering = useUpdateOffering(id);
  const deleteOffering = useDeleteOffering(id);

  // Estados de UI
  const [viewMode, setViewMode] = useState<'table' | 'cards'>('table');
  const [offeringViewMode, setOfferingViewMode] = useState<'table' | 'cards'>('table');
  
  // Estados para modal de ofrendas
  const [isOfferingModalOpen, setIsOfferingModalOpen] = useState(false);
  const [editingOffering, setEditingOffering] = useState<OfferingTableItem | null>(null);
  const [offeringForm, setOfferingForm] = useState<{
    amount: string;
    peopleId: string;
    offeringTypeId: string;
  }>({
    amount: '',
    peopleId: '',
    offeringTypeId: '',
  });
  
  // Estado para modal de eliminación
  const [deletingOfferingId, setDeletingOfferingId] = useState<string | null>(null);

  // Transformar datos para la tabla con memoización para evitar re-renders
  const tableData: AttendanceTableItem[] = useMemo(() => 
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

  // Solo mostrar loading si no hay datos previos (primera carga)
  const showTableLoading = isLoadingAttendance && tableData.length === 0;

  const handleGoBack = () => {
    navigate('/worships');
  };

  // Manejar cambio de asistencia (actualización optimista, sin loading visual)
  const handleToggleAttendance = (personId: string) => {
    if (!id) return;
    
    registerAttendance.mutate({
      peopleId: personId,
      eventId: id,
    });
  };

  // Paginación de asistencia
  const totalPages = attendanceData?.totalPages ?? 0;
  const totalElements = attendanceData?.totalElements ?? 0;
  
  // Transformar datos de ofrendas para la tabla
  const offeringTableData: OfferingTableItem[] = useMemo(() => 
    (offeringsData?.content ?? []).map((offering: Offering) => ({
      id: offering.id,
      personName: offering.people 
        ? `${offering.people.firstName} ${offering.people.lastName}` 
        : 'Anónimo',
      avatar: offering.people?.avatar,
      typeName: offering.type?.name || '-',
      amount: offering.amount,
      peopleId: offering.people?.id || null,
      offeringTypeId: offering.type?.id || 0,
    })), [offeringsData?.content]);
  
  // Paginación de ofrendas
  const offeringTotalPages = offeringsData?.totalPages ?? 0;
  const offeringTotalElements = offeringsData?.totalElements ?? 0;
  const showOfferingLoading = isLoadingOfferings && offeringTableData.length === 0;
  
  // Calcular total de ofrendas
  const totalOfferingsAmount = useMemo(() => 
    offeringTableData.reduce((sum, o) => sum + o.amount, 0), 
    [offeringTableData]
  );
  
  // Handlers de ofrendas
  const handleOpenOfferingModal = (offering?: OfferingTableItem) => {
    if (offering) {
      setEditingOffering(offering);
      setOfferingForm({
        amount: offering.amount.toString(),
        peopleId: offering.peopleId || '',
        offeringTypeId: offering.offeringTypeId.toString(),
      });
    } else {
      setEditingOffering(null);
      setOfferingForm({
        amount: '',
        peopleId: '',
        offeringTypeId: offeringTypes?.[0]?.id.toString() || '',
      });
    }
    setIsOfferingModalOpen(true);
  };
  
  const handleCloseOfferingModal = () => {
    setIsOfferingModalOpen(false);
    setEditingOffering(null);
    setOfferingForm({ amount: '', peopleId: '', offeringTypeId: '' });
  };
  
  const handleSaveOffering = () => {
    if (!id || !offeringForm.amount || !offeringForm.offeringTypeId) return;
    
    const amount = parseFloat(offeringForm.amount);
    if (isNaN(amount) || amount <= 0) return;
    
    if (editingOffering) {
      const data: UpdateOfferingDto = {
        id: editingOffering.id,
        eventId: id,
        amount,
        offeringTypeId: parseInt(offeringForm.offeringTypeId),
        ...(offeringForm.peopleId && { peopleId: offeringForm.peopleId }),
      };
      updateOffering.mutate(data, {
        onSuccess: handleCloseOfferingModal,
      });
    } else {
      const data: CreateOfferingDto = {
        eventId: id,
        amount,
        offeringTypeId: parseInt(offeringForm.offeringTypeId),
        ...(offeringForm.peopleId && { peopleId: offeringForm.peopleId }),
      };
      createOffering.mutate(data, {
        onSuccess: handleCloseOfferingModal,
      });
    }
  };
  
  const handleDeleteOffering = (offeringId: string) => {
    setDeletingOfferingId(offeringId);
  };
  
  const confirmDeleteOffering = () => {
    if (!deletingOfferingId) return;
    deleteOffering.mutate(deletingOfferingId, {
      onSuccess: () => setDeletingOfferingId(null),
    });
  };

  // Estado de carga
  if (isLoading) {
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

  // Estado de error
  if (error || !worship) {
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
            <Button variant="secondary" onClick={handleGoBack}>
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

  // Calcular porcentaje de asistencia
  const attendancePercentage = worship.totalAttendance > 0 
    ? Math.round((worship.presentCount / worship.totalAttendance) * 100) 
    : 0;

  // Columnas para la tabla
  const columns = [
    {
      key: 'fullName' as const,
      label: 'Persona',
      priority: 1,
      render: (_value: AttendanceTableItem[keyof AttendanceTableItem], item: AttendanceTableItem) => (
        <div className="flex items-center gap-3">
          <Avatar
            src={item.avatar}
            name={item.fullName}
            size="sm"
          />
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
        return strValue !== '-' ? (
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-violet-100 text-violet-700">
            {strValue}
          </span>
        ) : <span className="text-neutral-400">-</span>;
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
              isPresent 
                ? 'bg-green-100 text-green-700' 
                : 'bg-red-100 text-red-700'
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
  
  // Columnas para la tabla de ofrendas
  const offeringColumns = [
    {
      key: 'personName' as const,
      label: 'Persona',
      priority: 1,
      render: (_value: OfferingTableItem[keyof OfferingTableItem], item: OfferingTableItem) => (
        <div className="flex items-center gap-3">
          <Avatar
            src={item.avatar}
            name={item.personName}
            size="sm"
          />
          <p className="font-medium text-neutral-800">{item.personName}</p>
        </div>
      ),
    },
    {
      key: 'typeName' as const,
      label: 'Tipo',
      priority: 3,
      render: (value: OfferingTableItem[keyof OfferingTableItem]) => {
        const strValue = String(value ?? '-');
        return strValue !== '-' ? (
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-700">
            {strValue}
          </span>
        ) : <span className="text-neutral-400">-</span>;
      },
    },
    {
      key: 'amount' as const,
      label: 'Monto',
      priority: 2,
      render: (value: OfferingTableItem[keyof OfferingTableItem]) => (
        <span className="font-semibold text-green-600">
          ${typeof value === 'number' ? value.toLocaleString('es-ES', { minimumFractionDigits: 2 }) : '0.00'}
        </span>
      ),
    },
    {
      key: 'id' as const,
      label: 'Acciones',
      priority: 4,
      render: (_value: OfferingTableItem[keyof OfferingTableItem], item: OfferingTableItem) => (
        <div className="flex items-center gap-2">
          <button
            onClick={() => handleOpenOfferingModal(item)}
            className="p-1.5 text-neutral-500 hover:text-primary-600 hover:bg-primary-50 rounded transition-colors"
            title="Editar"
          >
            <FiEdit2 size={14} />
          </button>
          <button
            onClick={() => handleDeleteOffering(item.id)}
            className="p-1.5 text-neutral-500 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
            title="Eliminar"
          >
            <FiTrash2 size={14} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title={worship.name}
        subtitle="Detalle del culto"
        actions={
          <div className="flex items-center gap-2">
            <Button variant="secondary" onClick={handleGoBack}>
              <span className="flex items-center gap-2">
                <FiArrowLeft size={16} />
                Volver
              </span>
            </Button>
          </div>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fadeIn">
        {/* Columna izquierda: Info básica */}
        <div className="space-y-6">
          {/* Información general */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiFileText className="text-primary-600" />
              Información General
            </h3>

            <div className="space-y-4">
              {/* Nombre */}
              <div>
                <label className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Nombre
                </label>
                <p className="text-neutral-800 font-medium mt-1">{worship.name}</p>
              </div>

              {/* Descripción */}
              {worship.description && (
                <div>
                  <label className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                    Descripción
                  </label>
                  <p className="text-neutral-700 mt-1 whitespace-pre-wrap">{worship.description}</p>
                </div>
              )}

              {/* Tipo de culto */}
              <div>
                <label className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Tipo de Culto
                </label>
                <p className="mt-1">
                  <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-primary-100 text-primary-800">
                    {worship.worshipType?.name}
                  </span>
                </p>
              </div>
            </div>
          </Card>

          {/* Fechas */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiCalendar className="text-primary-600" />
              Fecha y Hora
            </h3>

            <div className="space-y-4">
              {/* Fecha programada */}
              <div>
                <label className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Fecha Programada
                </label>
                <p className="text-neutral-800 mt-1 capitalize">
                  {formatDate(worship.scheduledDate)}
                </p>
                <div className="flex items-center gap-2 mt-1 text-neutral-600">
                  <FiClock size={14} />
                  <span className="text-sm">{formatTime(worship.scheduledDate)}</span>
                </div>
              </div>
            </div>
          </Card>

          {/* Resumen de asistencia */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiUsers className="text-primary-600" />
              Resumen de Asistencia
            </h3>

            {/* Barra de progreso */}
            <div className="mb-4">
              <div className="flex justify-between text-sm mb-1">
                <span className="text-neutral-600">Asistencia</span>
                <span className="font-medium text-neutral-800">{attendancePercentage}%</span>
              </div>
              <div className="w-full bg-neutral-200 rounded-full h-2.5">
                <div
                  className="bg-green-500 h-2.5 rounded-full transition-all duration-300"
                  style={{ width: `${attendancePercentage}%` }}
                />
              </div>
            </div>

            {/* Estadísticas */}
            <div className="grid grid-cols-3 gap-3">
              <div className="bg-neutral-50 rounded-lg p-3 text-center">
                <p className="text-2xl font-bold text-neutral-800">{worship.totalAttendance ?? 0}</p>
                <p className="text-xs text-neutral-500">Total</p>
              </div>
              <div className="bg-green-50 rounded-lg p-3 text-center">
                <p className="text-2xl font-bold text-green-600">{worship.presentCount ?? 0}</p>
                <p className="text-xs text-green-600">Presentes</p>
              </div>
              <div className="bg-red-50 rounded-lg p-3 text-center">
                <p className="text-2xl font-bold text-red-600">{worship.absentCount ?? 0}</p>
                <p className="text-xs text-red-600">Ausentes</p>
              </div>
            </div>
          </Card>
        </div>

        {/* Columna derecha: Lista de asistencia (2 columnas) */}
        <div className="lg:col-span-2">
          <Card className="h-full flex flex-col">
            <Table<AttendanceTableItem>
              data={tableData}
              columns={columns}
              loading={showTableLoading}
              title={`Lista de Asistencia (${totalElements})`}
              viewMode={viewMode}
              onViewModeChange={setViewMode}
              pagination={{
                mode: 'manual',
                currentPage: page,
                totalPages: totalPages,
                totalElements: totalElements,
                pageSize: pageSize,
                onPageChange: setPage,
                onPageSizeChange: setPageSize,
              }}
            />
          </Card>
        </div>
      </div>
      
      {/* Sección de Ofrendas - Full width abajo */}
      <div className="mt-6 animate-fadeIn">
        <Card>
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-4">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-green-100 rounded-lg">
                <FiDollarSign className="text-green-600" size={24} />
              </div>
              <div>
                <h3 className="text-lg font-semibold text-neutral-800">
                  Ofrendas
                </h3>
                <p className="text-sm text-neutral-500">
                  {offeringTotalElements} registros · Total: <span className="font-semibold text-green-600">${totalOfferingsAmount.toLocaleString('es-ES', { minimumFractionDigits: 2 })}</span>
                </p>
              </div>
            </div>
            <Button onClick={() => handleOpenOfferingModal()}>
              <span className="flex items-center gap-2">
                <FiPlus size={16} />
                Nueva Ofrenda
              </span>
            </Button>
          </div>
          
          <Table<OfferingTableItem>
            data={offeringTableData}
            columns={offeringColumns}
            loading={showOfferingLoading}
            viewMode={offeringViewMode}
            onViewModeChange={setOfferingViewMode}
            pagination={{
              mode: 'manual',
              currentPage: offeringPage,
              totalPages: offeringTotalPages,
              totalElements: offeringTotalElements,
              pageSize: offeringPageSize,
              onPageChange: setOfferingPage,
              onPageSizeChange: setOfferingPageSize,
            }}
          />
        </Card>
      </div>
      
      {/* Modal de Ofrenda */}
      <Modal
        isOpen={isOfferingModalOpen}
        onClose={handleCloseOfferingModal}
        title={editingOffering ? 'Editar Ofrenda' : 'Nueva Ofrenda'}
        size="md"
      >
        <div className="space-y-4">
          {/* Tipo de ofrenda */}
          <div>
            <label className="block text-sm font-medium text-neutral-700 mb-1">
              Tipo de Ofrenda *
            </label>
            <select
              value={offeringForm.offeringTypeId}
              onChange={(e) => setOfferingForm(prev => ({ ...prev, offeringTypeId: e.target.value }))}
              className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">Seleccionar tipo</option>
              {offeringTypes?.map((type) => (
                <option key={type.id} value={type.id}>
                  {type.name}
                </option>
              ))}
            </select>
          </div>
          
          {/* Monto */}
          <div>
            <label className="block text-sm font-medium text-neutral-700 mb-1">
              Monto *
            </label>
            <div className="relative">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-500">$</span>
              <input
                type="number"
                step="0.01"
                min="0.01"
                value={offeringForm.amount}
                onChange={(e) => setOfferingForm(prev => ({ ...prev, amount: e.target.value }))}
                placeholder="0.00"
                className="w-full pl-8 pr-3 py-2 border border-neutral-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              />
            </div>
          </div>
          
          {/* Persona (opcional) */}
          <div>
            <label className="block text-sm font-medium text-neutral-700 mb-1">
              Persona (opcional - dejar vacío para anónimo)
            </label>
            <select
              value={offeringForm.peopleId}
              onChange={(e) => setOfferingForm(prev => ({ ...prev, peopleId: e.target.value }))}
              className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">Anónimo</option>
              {peopleData?.content?.map((person) => (
                <option key={person.id} value={person.id}>
                  {person.firstName} {person.lastName}
                </option>
              ))}
            </select>
          </div>
          
          {/* Botones */}
          <div className="flex justify-end gap-3 pt-4 border-t border-neutral-200">
            <Button variant="secondary" onClick={handleCloseOfferingModal}>
              Cancelar
            </Button>
            <Button 
              onClick={handleSaveOffering}
              disabled={!offeringForm.amount || !offeringForm.offeringTypeId || createOffering.isPending || updateOffering.isPending}
            >
              {createOffering.isPending || updateOffering.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
          </div>
        </div>
      </Modal>
      
      {/* Modal de confirmación de eliminación */}
      <Modal
        isOpen={!!deletingOfferingId}
        onClose={() => setDeletingOfferingId(null)}
        title="Eliminar Ofrenda"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-neutral-600">
            ¿Estás seguro de que deseas eliminar esta ofrenda? Esta acción no se puede deshacer.
          </p>
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setDeletingOfferingId(null)}>
              Cancelar
            </Button>
            <Button 
              variant="danger" 
              onClick={confirmDeleteOffering}
              disabled={deleteOffering.isPending}
            >
              {deleteOffering.isPending ? 'Eliminando...' : 'Eliminar'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
