import { Card } from '../shared';
import { useMyHomeGroup } from '../../hooks/useHomeGroups';
import { useMeetings } from '../../hooks/useMeetings';
import { FiCalendar, FiUsers } from 'react-icons/fi';
import { FiHome } from 'react-icons/fi';
import { formatDateForDisplay } from '../../utils/helpers';
import { useNavigate } from 'react-router-dom';

/**
 * Widget que muestra las reuniones recientes del grupo al que pertenece el usuario
 * Hace una petición paginada pidiendo 5 reuniones ordenadas por fecha descendente
 */
export function RecentGroupMeetingsWidget() {
  const navigate = useNavigate();

  // Obtener el grupo del usuario
  const { data: myGroupData, isLoading: isLoadingGroup } = useMyHomeGroup();
  const groupId = myGroupData?.homeGroup?.id;

  // Petición paginada: 5 reuniones, ordenadas por fecha descendente
  const { data: meetingsData, isLoading: isLoadingMeetings } = useMeetings(groupId, {
    page: 0,
    size: 5,
    sort: { field: 'scheduledDate', direction: 'desc' }
  });

  const meetings = meetingsData?.content || [];
  const isLoading = isLoadingGroup || isLoadingMeetings;

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

  // Si no hay grupo del usuario
  if (!groupId) {
    return (
      <Card className="p-5 sm:p-6 flex flex-col gap-4">
        <div className="flex items-center gap-2">
          <FiHome size={20} className="text-violet-600" />
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Reuniones de Grupo Recientes
          </h2>
        </div>
        <p className="text-neutral-500 text-center py-4 text-sm">
          No perteneces a ningún grupo
        </p>
      </Card>
    );
  }

  return (
    <Card className="p-5 sm:p-6 flex flex-col gap-4">
      <div className="flex items-center gap-2">
        <FiHome size={20} className="text-violet-600" />
        <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
          Reuniones de Grupo Recientes
        </h2>
      </div>

      {meetings.length === 0 ? (
        <p className="text-neutral-500 text-center py-4 text-sm">
          No hay reuniones registradas en tu grupo
        </p>
      ) : (
        <ul className="space-y-3">
          {meetings.map((meeting) => (
            <li
              key={meeting.id}
              onClick={() => navigate(`/group-meetings/${meeting.id}`)}
              className="flex items-start gap-3 p-3 rounded-lg hover:bg-neutral-50 transition-colors cursor-pointer border border-neutral-100"
            >
              <div className="flex-shrink-0 w-10 h-10 bg-violet-100 rounded-lg flex items-center justify-center">
                <FiHome size={18} className="text-violet-600" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-medium text-neutral-800 text-sm">
                  {myGroupData?.homeGroup?.name || 'Reunión de Grupo'}
                </p>
                <div className="flex items-center gap-1 text-xs text-neutral-500 mt-1">
                  <FiCalendar size={12} />
                  <span>{formatDateForDisplay(meeting.scheduledDate, 'date')}</span>
                  <span className="mx-1">•</span>
                  <span>{formatDateForDisplay(meeting.scheduledDate, 'time')}</span>
                </div>
                {meeting.type && (
                  <span className="inline-block text-[10px] px-2 py-0.5 rounded-full font-medium bg-violet-50 text-violet-600 mt-1">
                    {meeting.type.name}
                  </span>
                )}
                {(meeting.presentCount !== undefined || meeting.absentCount !== undefined) && (
                  <div className="flex items-center gap-1 text-xs text-neutral-400 mt-1">
                    <FiUsers size={12} />
                    <span>
                      {meeting.presentCount || 0} presente{(meeting.presentCount || 0) !== 1 ? 's' : ''}
                      {meeting.absentCount !== undefined && ` • ${meeting.absentCount} ausente${meeting.absentCount !== 1 ? 's' : ''}`}
                    </span>
                  </div>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}
