import { ReactNode } from 'react';
import StatCard from './StatCard';
import { FiUsers, FiHome, FiCalendar, FiBarChart2 } from 'react-icons/fi';
import { MdChurch } from 'react-icons/md';

export interface MetricsStatsGridProps {
  totalPeople: number;
  totalGroups?: number;
  totalMeetings: number;
  attendanceRate: number;
  averageAttendance?: number;
  labels?: {
    totalPeople?: string;
    totalGroups?: string;
    totalMeetings?: string;
    attendanceRate?: string;
    averageAttendance?: string;
  };
  icons?: {
    totalPeople?: ReactNode;
    totalGroups?: ReactNode;
    totalMeetings?: ReactNode;
    attendanceRate?: ReactNode;
    averageAttendance?: ReactNode;
  };
}

/**
 * Componente reutilizable para mostrar el grid de estadísticas principales
 * Usado en Dashboard, MyChurch, ChurchDetail y HomeGroupDetail
 */
export function MetricsStatsGrid({
  totalPeople,
  totalGroups,
  totalMeetings,
  attendanceRate,
  averageAttendance,
  labels,
  icons,
}: MetricsStatsGridProps) {
  const defaultLabels = {
    totalPeople: 'Total Miembros',
    totalGroups: 'Grupos Activos',
    totalMeetings: 'Cultos Este Período',
    attendanceRate: 'Tasa de Asistencia',
    averageAttendance: 'Asist. Promedio',
  };

  const defaultIcons = {
    totalPeople: <FiUsers size={28} />,
    totalGroups: <FiHome size={28} />,
    totalMeetings: <FiCalendar size={28} />,
    attendanceRate: <MdChurch size={28} />,
    averageAttendance: <FiBarChart2 size={28} />,
  };

  const finalLabels = { ...defaultLabels, ...labels };
  const finalIcons = { ...defaultIcons, ...icons };

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-6">
      <StatCard
        icon={finalIcons.totalPeople}
        title={finalLabels.totalPeople}
        value={totalPeople}
        bgColor="bg-green-500"
      />

      {totalGroups !== undefined && (
        <StatCard
          icon={finalIcons.totalGroups}
          title={finalLabels.totalGroups}
          value={totalGroups}
          bgColor="bg-violet-500"
        />
      )}

      <StatCard
        icon={finalIcons.totalMeetings}
        title={finalLabels.totalMeetings}
        value={totalMeetings}
        bgColor="bg-blue-500"
      />

      {averageAttendance !== undefined && (
        <StatCard
          icon={finalIcons.averageAttendance}
          title={finalLabels.averageAttendance}
          value={averageAttendance.toFixed(1)}
          bgColor="bg-amber-500"
        />
      )}

      <StatCard
        icon={finalIcons.attendanceRate}
        title={finalLabels.attendanceRate}
        value={`${Math.round(attendanceRate)}%`}
        bgColor="bg-yellow-500"
      />
    </div>
  );
}
