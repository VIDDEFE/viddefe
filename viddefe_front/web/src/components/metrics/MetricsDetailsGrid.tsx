import MetricCard from './MetricCard';
import AttendanceRateCard from './AttendanceRateCard';

interface GroupMetrics {
  totalPeople: number;
  totalMeetings: number;
  averageAttendancePerMeeting: number;
  attendanceRate: number;
}

interface ChurchMetrics {
  totalPeople: number;
  totalMeetings: number;
  newAttendees: number;
  attendanceRate: number;
}

interface WorshipMetrics {
  newAttendees: number;
  totalPeopleAttended: number;
  averageAttendancePerMeeting: number;
  absenceRate: number;
  attendanceRate: number;
  totalPeople: number;
  totalMeetings: number;
  groupMetrics?: GroupMetrics;
  churchMetrics?: ChurchMetrics;
}

export interface MetricsDetailsGridProps {
  metrics: WorshipMetrics;
  entityName: string;
  dateRange?: {
    start: string;
    end: string;
  };
  showGeneralMetrics?: boolean;
}

/**
 * Componente reutilizable para mostrar el grid de métricas detalladas
 * Incluye métricas generales, visualización de asistencia, métricas de grupos y métricas de cultos
 * Usado en Dashboard, MyChurch, ChurchDetail
 */
export function MetricsDetailsGrid({
  metrics,
  entityName,
  dateRange,
  showGeneralMetrics = true,
}: MetricsDetailsGridProps) {
  const dateRangeText = dateRange
    ? `(${dateRange.start} a ${dateRange.end})`
    : '';

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
      {/* General Metrics */}
      {showGeneralMetrics && (
        <MetricCard
          title={`Métricas de ${entityName}`}
          metrics={[
            {
              label: 'Nuevos Asistentes',
              value: metrics.newAttendees,
            },
            {
              label: 'Total Asistentes',
              value: metrics.totalPeopleAttended,
            },
            {
              label: 'Asistencia Promedio',
              value: (metrics.averageAttendancePerMeeting || 0).toFixed(1),
            },
            {
              label: 'Tasa de Inasistencia',
              value: `${Math.round(metrics.absenceRate)}%`,
            },
          ]}
        />
      )}

      {/* Attendance Rate Visualization */}
      <AttendanceRateCard
        attendanceRate={metrics.attendanceRate}
        totalPeople={metrics.totalPeople}
        totalMeetings={metrics.totalMeetings}
      />

      {/* Group Metrics */}
      {metrics.groupMetrics && (
        <MetricCard
          title={`Métricas de Grupos ${dateRangeText}`}
          metrics={[
            {
              label: 'Personas en Grupos',
              value: metrics.groupMetrics.totalPeople,
            },
            {
              label: 'Reuniones de Grupos',
              value: metrics.groupMetrics.totalMeetings,
            },
            {
              label: 'Asist. Promedio Grupos',
              value: (
                metrics.groupMetrics.averageAttendancePerMeeting || 0
              ).toFixed(1),
            },
            {
              label: 'Asistencia Grupos',
              value: `${Math.round(metrics.groupMetrics.attendanceRate)}%`,
              colorClass: 'text-green-600',
            },
          ]}
        />
      )}

      {/* Church Metrics */}
      {metrics.churchMetrics && (
        <MetricCard
          title={`Métricas de Cultos ${dateRangeText}`}
          metrics={[
            {
              label: 'Personas en Cultos',
              value: metrics.churchMetrics.totalPeople,
            },
            {
              label: 'Total de Cultos',
              value: metrics.churchMetrics.totalMeetings,
            },
            {
              label: 'Nuevos Asistentes Cultos',
              value: metrics.churchMetrics.newAttendees,
            },
            {
              label: 'Asistencia Cultos',
              value: `${Math.round(metrics.churchMetrics.attendanceRate)}%`,
              colorClass: 'text-green-600',
            },
          ]}
        />
      )}
    </div>
  );
}
