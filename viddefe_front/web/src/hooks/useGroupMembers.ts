import { useQuery, keepPreviousData } from '@tanstack/react-query';
import { groupService } from '../services/groupService';
import type { PageableRequest } from '../services/api';

/**
 * Hook para obtener miembros de un grupo con paginación
 */
export function useGroupMembers(groupId?: string, params?: PageableRequest) {
  return useQuery({
    queryKey: ['groupMembers', groupId, params?.page, params?.size, params?.sort],
    queryFn: () => groupService.getMembers(groupId!, params),
    enabled: !!groupId,
    placeholderData: keepPreviousData,
  });
}
