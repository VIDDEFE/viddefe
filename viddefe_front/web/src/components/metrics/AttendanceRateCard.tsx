import { Card } from '../shared';

interface AttendanceRateCardProps {
  attendanceRate: number;
  totalPeople: number;
  totalMeetings: number;
  title?: string;
  subtitle?: string;
  className?: string;
}

export default function AttendanceRateCard({
  attendanceRate,
  totalPeople,
  totalMeetings,
  title = 'Tasa de Asistencia',
  subtitle,
  className = ''
}: AttendanceRateCardProps) {
  return (
    <Card className={`p-5 sm:p-6 ${className}`}>
      <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
        {title}
      </h3>
      <div className="flex flex-col items-center gap-4">
        <div className="relative w-24 h-24">
          <div className="w-24 h-24 rounded-full bg-gray-100 flex items-center justify-center">
            <div className="text-center">
              <p className="text-2xl font-bold text-primary-800">
                {Math.round(attendanceRate)}%
              </p>
            </div>
          </div>
        </div>
        <div className="w-full">
          <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
            <div
              className="h-full bg-green-500 transition-all"
              style={{ width: `${attendanceRate}%` }}
            ></div>
          </div>
        </div>
        <p className="text-sm text-primary-600 text-center">
          {subtitle || `${totalPeople} personas | ${totalMeetings} reuniones${totalMeetings === 1 ? '' : ' totales'}`}
        </p>
      </div>
    </Card>
  );
}
