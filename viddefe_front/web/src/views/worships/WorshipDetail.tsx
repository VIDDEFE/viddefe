import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useWorshipMeeting, useWorshipAttendance, useRegisterAttendance } from '../../hooks';
import { Card, Button, PageHeader, Avatar, Switch, Table } from '../../components/shared';
import { 
  FiArrowLeft, 
  FiCalendar, 
  FiClock, 
  FiUsers, 
  FiCheck, 
  FiX, 
  FiFileText
} from 'react-icons/fi';
import type { WorshipAttendance } from '../../models';

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

export default function WorshipDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  // Estados de paginación
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  
  // Queries
  const { data: worship, isLoading, error } = useWorshipMeeting(id);
  const { 
    data: attendanceData, 
    isLoading: isLoadingAttendance,
    isFetching: isFetchingAttendance
  } = useWorshipAttendance(id, { page, size: pageSize });
  const registerAttendance = useRegisterAttendance(id);

  // Estados de UI
  const [viewMode, setViewMode] = useState<'table' | 'cards'>('table');
  const [loadingPersonId, setLoadingPersonId] = useState<string | null>(null);

  // Transformar datos para la tabla
  const tableData: AttendanceTableItem[] = (attendanceData?.content ?? []).map((record: WorshipAttendance) => ({
    id: record.people.id,
    fullName: `${record.people.firstName} ${record.people.lastName}`,
    phone: record.people.phone || '-',
    avatar: record.people.avatar,
    typePerson: record.people.typePerson?.name || '-',
    status: record.status,
    isPresent: record.status === 'PRESENT',
    peopleId: record.people.id,
  }));

  // Solo mostrar loading si no hay datos previos (primera carga)
  const showTableLoading = isLoadingAttendance && tableData.length === 0;

  const handleGoBack = () => {
    navigate('/worships');
  };

  // Manejar cambio de asistencia
  const handleToggleAttendance = async (personId: string) => {
    if (!id) return;
    
    setLoadingPersonId(personId);
    try {
      await registerAttendance.mutateAsync({
        peopleId: personId,
        eventId: id,
      });
    } catch (error) {
      console.error('Error al registrar asistencia:', error);
    } finally {
      setLoadingPersonId(null);
    }
  };

  // Paginación
  const totalPages = attendanceData?.totalPages ?? 0;
  const totalElements = attendanceData?.totalElements ?? 0;

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
        const isThisLoading = registerAttendance.isPending && loadingPersonId === item.peopleId;
        
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
              loading={isThisLoading}
              disabled={registerAttendance.isPending}
              size="sm"
            />
          </div>
        );
      },
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
          <Card className={`h-full flex flex-col ${isFetchingAttendance && tableData.length > 0 ? 'opacity-70 transition-opacity' : ''}`}>
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
    </div>
  );
}
