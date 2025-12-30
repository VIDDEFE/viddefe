import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { worshipService } from '../services/worshipService';
import type { Worship, WorshipType, CreateWorshipDto, UpdateWorshipDto } from '../models';
import type { Pageable, PageableRequest } from '../services/api';

/**
 * Hook para obtener todos los cultos con paginación
 */
export function useWorshipMeetings(params?: PageableRequest) {
  return useQuery<Pageable<Worship>, Error>({
    queryKey: ['worships', params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => worshipService.getAll(params),
  });
}

/**
 * Hook para obtener un culto por ID
 */
export function useWorshipMeeting(id?: string) {
  return useQuery<Worship, Error>({
    queryKey: ['worship', id],
    queryFn: () => worshipService.getById(id!),
    enabled: !!id,
  });
}

/**
 * Hook para obtener todos los tipos de culto
 */
export function useWorshipMeetingTypes() {
  return useQuery<WorshipType[], Error>({
    queryKey: ['worshipTypes'],
    queryFn: () => worshipService.getTypes(),
    staleTime: 5 * 60 * 1000, // 5 minutos - los tipos no cambian frecuentemente
  });
}

/**
 * Hook para crear un nuevo culto
 */
export function useCreateWorship() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateWorshipDto) => worshipService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['worships'] });
    },
  });
}

/**
 * Hook para actualizar un culto existente
 */
export function useUpdateWorship() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateWorshipDto }) =>
      worshipService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['worships'] });
    },
  });
}

/**
 * Hook para eliminar un culto
 */
export function useDeleteWorship() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => worshipService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['worships'] });
    },
  });
}
