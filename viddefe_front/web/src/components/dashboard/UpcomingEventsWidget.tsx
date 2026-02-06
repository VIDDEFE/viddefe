import { Card } from '../shared';
import { useEvents } from '../../hooks';
import { FiCalendar, FiClock } from 'react-icons/fi';
import { formatDateForDisplay } from '../../utils/helpers';

/**
 * Widget que muestra los próximos eventos programados
 */
export function UpcomingEventsWidget() {
  const { data: events, isLoading } = useEvents();

  // Filtrar eventos futuros y ordenar por fecha
  const upcomingEvents = events
    ?.filter(event => {
      const eventDate = new Date(event.date);
      return eventDate >= new Date() && (event.status === 'PLANNED' || event.status === 'IN_PROGRESS');
    })
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
    .slice(0, 5) || [];

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
        <FiCalendar size={20} className="text-primary-600" />
        <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
          Próximos Eventos
        </h2>
      </div>

      {upcomingEvents.length === 0 ? (
        <p className="text-neutral-500 text-center py-4 text-sm">
          No hay eventos próximos programados
        </p>
      ) : (
        <ul className="space-y-3">
          {upcomingEvents.map((event) => (
            <li
              key={event.id}
              className="flex items-start gap-3 p-3 rounded-lg hover:bg-neutral-50 transition-colors border border-neutral-100"
            >
              <div className="flex-shrink-0 w-12 h-12 bg-primary-100 rounded-lg flex flex-col items-center justify-center">
                <span className="text-xs font-medium text-primary-600">
                  {formatDateForDisplay(event.date, 'date').split('-')[2] || 'DD'}
                </span>
                <span className="text-[10px] text-primary-500">
                  {new Date(event.date).toLocaleDateString('es-ES', { month: 'short' }).toUpperCase()}
                </span>
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-neutral-800 text-sm truncate">
                  {event.name}
                </p>
                <div className="flex items-center gap-1 text-xs text-neutral-500 mt-1">
                  <FiClock size={12} />
                  <span>{formatDateForDisplay(event.date, 'time')}</span>
                </div>
                {event.description && (
                  <p className="text-xs text-neutral-500 mt-1 line-clamp-2">
                    {event.description}
                  </p>
                )}
              </div>
              <span
                className={`flex-shrink-0 text-[10px] px-2 py-1 rounded-full font-medium ${
                  event.status === 'PLANNED'
                    ? 'bg-blue-100 text-blue-700'
                    : event.status === 'IN_PROGRESS'
                    ? 'bg-green-100 text-green-700'
                    : 'bg-neutral-100 text-neutral-600'
                }`}
              >
                {event.status === 'PLANNED'
                  ? 'Programado'
                  : event.status === 'IN_PROGRESS'
                  ? 'En Progreso'
                  : event.status}
              </span>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}
