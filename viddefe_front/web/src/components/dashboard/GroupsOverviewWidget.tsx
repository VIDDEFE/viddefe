import { Card } from '../shared';
import { useHomeGroups } from '../../hooks';
import { FiHome, FiUsers, FiMapPin } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';

/**
 * Widget que muestra resumen de grupos activos
 */
export function GroupsOverviewWidget() {
  const navigate = useNavigate();
  const { data: groupsData, isLoading } = useHomeGroups({ page: 0, size: 5 });

  const groups = groupsData?.content || [];
  const totalGroups = groupsData?.totalElements || 0;

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
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <FiHome size={20} className="text-violet-600" />
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Grupos Activos
          </h2>
        </div>
        <span className="text-sm font-medium text-violet-600 bg-violet-50 px-3 py-1 rounded-full">
          {totalGroups} {totalGroups === 1 ? 'grupo' : 'grupos'}
        </span>
      </div>

      {groups.length === 0 ? (
        <p className="text-neutral-500 text-center py-4 text-sm">
          No hay grupos registrados
        </p>
      ) : (
        <>
          <ul className="space-y-3">
            {groups.map((group) => (
              <li
                key={group.id}
                onClick={() => navigate(`/groups/${group.id}`)}
                className="flex items-start gap-3 p-3 rounded-lg hover:bg-neutral-50 transition-colors cursor-pointer border border-neutral-100"
              >
                <div className="flex-shrink-0 w-10 h-10 bg-violet-100 rounded-lg flex items-center justify-center">
                  <FiHome size={18} className="text-violet-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-neutral-800 text-sm truncate">
                    {group.name}
                  </p>
                  {group.manager && (
                    <div className="flex items-center gap-1 text-xs text-neutral-500 mt-1">
                      <FiUsers size={12} />
                      <span>
                        {group.manager.firstName} {group.manager.lastName}
                      </span>
                    </div>
                  )}
                  {group.latitude && group.longitude && (
                    <div className="flex items-center gap-1 text-xs text-neutral-400 mt-1">
                      <FiMapPin size={12} />
                      <span className="truncate">
                        {group.latitude.toFixed(4)}, {group.longitude.toFixed(4)}
                      </span>
                    </div>
                  )}
                </div>
              </li>
            ))}
          </ul>

          {totalGroups > 5 && (
            <button
              onClick={() => navigate('/groups')}
              className="text-sm text-primary-600 hover:text-primary-700 font-medium text-center py-2 hover:bg-primary-50 rounded transition-colors"
            >
              Ver todos los grupos ({totalGroups})
            </button>
          )}
        </>
      )}
    </Card>
  );
}
