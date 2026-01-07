import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ministryFunctionService } from '../services/ministryFunctionService';
import type { 
  MinistryFunction, 
  CreateMinistryFunctionDto, 
  UpdateMinistryFunctionDto,
  EventType,
  MinistryRole
} from '../models';

/**
 * Hook para obtener las funciones ministeriales de una reunión
 */
export function useMinistryFunctions(meetingId?: string, eventType?: EventType) {
  return useQuery<MinistryFunction[], Error>({
    queryKey: ['ministryFunctions', meetingId, eventType],
    queryFn: () => ministryFunctionService.getAll(meetingId!, eventType!),
    enabled: !!meetingId && !!eventType,
  });
}

/**
 * Hook para obtener los roles disponibles para funciones ministeriales
 */
export function useMinistryRoles() {
  return useQuery<MinistryRole[], Error>({
    queryKey: ['ministryRoles'],
    queryFn: () => ministryFunctionService.getRoles(),
    staleTime: 5 * 60 * 1000, // 5 minutos - los roles no cambian frecuentemente
  });
}

/**
 * Hook para crear una función ministerial
 */
export function useCreateMinistryFunction(meetingId?: string, eventType?: EventType) {
  const qc = useQueryClient();

  return useMutation<MinistryFunction, Error, CreateMinistryFunctionDto>({
    mutationFn: (data) => ministryFunctionService.create(meetingId!, eventType!, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['ministryFunctions', meetingId, eventType] });
    },
  });
}

/**
 * Hook para actualizar una función ministerial
 */
export function useUpdateMinistryFunction(meetingId?: string, eventType?: EventType) {
  const qc = useQueryClient();

  return useMutation<MinistryFunction, Error, { id: string; data: UpdateMinistryFunctionDto }>({
    mutationFn: ({ id, data }) => ministryFunctionService.update(meetingId!, id, eventType!, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['ministryFunctions', meetingId, eventType] });
    },
  });
}

/**
 * Hook para eliminar una función ministerial
 */
export function useDeleteMinistryFunction(meetingId?: string, eventType?: EventType) {
  const qc = useQueryClient();

  return useMutation<unknown, Error, string>({
    mutationFn: (id) => ministryFunctionService.delete(meetingId!, id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['ministryFunctions', meetingId, eventType] });
    },
  });
}
