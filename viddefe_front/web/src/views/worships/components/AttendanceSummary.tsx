import { memo, useMemo } from 'react';
import { Card } from '../../../components/shared';
import { FiUsers } from 'react-icons/fi';
import { calculateAttendancePercentage } from './helpers';
import type { AttendanceSummaryProps } from './types';

function AttendanceSummary({ worship }: Readonly<AttendanceSummaryProps>) {
  const attendancePercentage = useMemo(
    () => calculateAttendancePercentage(worship.totalAttendance, worship.presentCount),
    [worship.totalAttendance, worship.presentCount]
  );

  return (
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
        <StatCard
          value={worship.totalAttendance ?? 0}
          label="Total"
          variant="neutral"
        />
        <StatCard
          value={worship.presentCount ?? 0}
          label="Presentes"
          variant="success"
        />
        <StatCard
          value={worship.absentCount ?? 0}
          label="Ausentes"
          variant="danger"
        />
      </div>
    </Card>
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

export default memo(AttendanceSummary);
