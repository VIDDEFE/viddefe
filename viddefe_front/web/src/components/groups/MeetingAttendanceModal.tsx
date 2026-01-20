import { memo, useState, useMemo, useCallback } from 'react';
import { Modal, Button, Avatar, Switch, Table } from '../shared';
import { FiArrowLeft, FiCheck, FiX } from 'react-icons/fi';
import type { Meeting, MeetingAttendance } from '../../models';
import type { Pageable } from '../../services/api';
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

interface MeetingAttendanceModalProps {
  readonly isOpen: boolean;
  readonly meeting: Meeting | null;
  readonly attendanceData?: Pageable<MeetingAttendance>;
  readonly isLoading: boolean;
  readonly onClose: () => void;
  readonly onToggleAttendance: (personId: string) => void;
  readonly page: number;
  readonly pageSize: number;
  readonly onPageChange: (page: number) => void;
  readonly onPageSizeChange: (size: number) => void;
}

function MeetingAttendanceModal({
  isOpen,
  meeting,
  attendanceData,
  isLoading,
  onClose,
  onToggleAttendance,
  page,
  pageSize,
  onPageChange,
  onPageSizeChange,
}: MeetingAttendanceModalProps) {
  const [viewMode, setViewMode] = useState<'table' | 'cards'>('table');

  // Transformar datos para la tabla (Pageable directo)
  const attendanceTableData: AttendanceTableItem[] = useMemo(
    () =>
      (attendanceData?.content ?? []).map((record: MeetingAttendance) => ({
        id: record.people.id,
        fullName: `${record.people.firstName} ${record.people.lastName}`,
        phone: record.people.phone || '-',
        avatar: record.people.avatar,
        typePerson: record.people.typePerson?.name || '-',
        status: record.status,
        isPresent: record.status === 'PRESENT',
        peopleId: record.people.id,
      })),
    [attendanceData?.content]
  );

  // Calcular estadísticas basadas en el meeting (tiene conteos agregados)
  // o calcular desde el contenido si no hay meeting
  const stats = useMemo(() => {
    const total = meeting?.totalAttendance ?? attendanceData?.totalElements ?? 0;
    const present = meeting?.presentCount ?? 0;
    const absent = meeting?.absentCount ?? 0;
    return { total, present, absent };
  }, [meeting?.totalAttendance, meeting?.presentCount, meeting?.absentCount, attendanceData?.totalElements]);

  const handleToggle = useCallback(
    (personId: string) => {
      onToggleAttendance(personId);
    },
    [onToggleAttendance]
  );

  // Columnas de la tabla
  const columns = useMemo(
    () => [
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
              <span
                className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-medium ${
                  isPresent ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                }`}
              >
                {isPresent ? <FiCheck size={12} /> : <FiX size={12} />}
                {isPresent ? 'Presente' : 'Ausente'}
              </span>
              <Switch checked={isPresent} onChange={() => handleToggle(item.peopleId)} size="sm" />
            </div>
          );
        },
      },
    ],
    [handleToggle]
  );

  if (!meeting) return null;

  const totalPages = attendanceData?.totalPages ?? 0;
  const totalElements = attendanceData?.totalElements ?? 0;
  const showTableLoading = isLoading && attendanceTableData.length === 0;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Asistencia">
      <div className="space-y-4">
        {/* Header con info de la reunión */}
        <div className="flex items-center justify-between pb-4 border-b border-neutral-200">
          <div>
            <h3 className="text-lg font-semibold text-neutral-800">{meeting.name}</h3>
            <p className="text-sm text-neutral-500 capitalize">
              {formatDateForDisplay(meeting.scheduledDate, 'date')}
            </p>
          </div>
          <Button variant="secondary" onClick={onClose}>
            <span className="flex items-center gap-2">
              <FiArrowLeft size={16} />
              Volver
            </span>
          </Button>
        </div>

        {/* Estadísticas */}
        <div className="grid grid-cols-3 gap-3">
          <StatCard value={stats.total} label="Total" variant="neutral" />
          <StatCard value={stats.present} label="Presentes" variant="success" />
          <StatCard value={stats.absent} label="Ausentes" variant="danger" />
        </div>

        {/* Tabla de asistencia */}
        <Table<AttendanceTableItem>
          data={attendanceTableData}
          columns={columns}
          loading={showTableLoading}
          title={`Lista de Asistencia (${totalElements})`}
          viewMode={viewMode}
          onViewModeChange={setViewMode}
          pagination={{
            mode: 'manual',
            currentPage: page,
            totalPages,
            totalElements,
            pageSize,
            onPageChange,
            onPageSizeChange,
          }}
        />
      </div>
    </Modal>
  );
}

interface StatCardProps {
  readonly value: number;
  readonly label: string;
  readonly variant: 'neutral' | 'success' | 'danger';
}

const variantStyles = {
  neutral: 'bg-neutral-50 text-neutral-800',
  success: 'bg-green-50 text-green-600',
  danger: 'bg-red-50 text-red-600',
};

function StatCard({ value, label, variant }: Readonly<StatCardProps>) {
  return (
    <div className={`rounded-lg p-3 text-center ${variantStyles[variant]}`}>
      <p className="text-2xl font-bold">{value}</p>
      <p className="text-xs">{label}</p>
    </div>
  );
}

export default memo(MeetingAttendanceModal);
