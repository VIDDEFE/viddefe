import { Card } from '../shared';
import { useWorshipMeetings } from '../../hooks';
import { FiCalendar, FiUsers } from 'react-icons/fi';
import { MdChurch } from 'react-icons/md';
import { formatDateForDisplay } from '../../utils/helpers';
import { useNavigate } from 'react-router-dom';

/**
 * Widget que muestra los cultos/servicios recientes
 * Hace una petición paginada pidiendo 3 cultos ordenados por fecha descendente
 */
export function RecentWorshipsWidget() {
  const navigate = useNavigate();

  // Petición paginada: 3 cultos, ordenados por fecha descendente
  const { data: worshipsData, isLoading } = useWorshipMeetings({
    page: 0,
    size: 3,
    sort: { field: 'scheduledDate', direction: 'desc' }
  });

  const worships = worshipsData?.content || [];

  if (isLoading) {
    return (
      <Card className="p-5 sm:p-6">
        <div className="animate-pulse space-y-3">
          <div className="h-6 bg-neutral-200 rounded w-1/2" />
          <div className="h-4 bg-neutral-200 rounded" />
          <div className="h-4 bg-neutral-200 rounded" />
        </div>
      </Card>
    );
  }

  return (
    <Card className="p-5 sm:p-6 flex flex-col gap-4">
      <div className="flex items-center gap-2">
        <MdChurch size={20} className="text-blue-600" />
        <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
          Cultos Recientes
        </h2>
      </div>

      {worships.length === 0 ? (
        <p className="text-neutral-500 text-center py-4 text-sm">
          No hay cultos registrados
        </p>
      ) : (
        <ul className="space-y-3">
          {worships.map((worship) => (
            <li
              key={worship.id}
              onClick={() => navigate(`/worships/${worship.id}`)}
              className="flex items-start gap-3 p-3 rounded-lg hover:bg-neutral-50 transition-colors cursor-pointer border border-neutral-100"
            >
              <div className="flex-shrink-0 w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                <MdChurch size={18} className="text-blue-600" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-neutral-800 text-sm">
                </p>
                <div className="flex items-center gap-1 text-xs text-neutral-500 mt-1">
                  <FiCalendar size={12} />
                  <span>{formatDateForDisplay(worship.scheduledDate, 'date')}</span>
                  <span className="mx-1">•</span>
                  <span>{formatDateForDisplay(worship.scheduledDate, 'time')}</span>
                </div>
                {worship.type && (
                  <span className="inline-block text-[10px] px-2 py-0.5 rounded-full font-medium bg-blue-50 text-blue-600 mt-1">
                    {worship.type.name}
                  </span>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}
