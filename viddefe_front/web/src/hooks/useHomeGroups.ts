import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { homeGroupService, strategyService } from '../services/homeGroupService';
import type { HomeGroup, Strategy, CreateHomeGroupDto, UpdateHomeGroupDto } from '../models';
import type { Pageable, PageableRequest } from '../services/api';

// ============================================================================
// HOME GROUPS HOOKS
// ============================================================================

/**
 * Hook para obtener todos los grupos con paginación
 */
export function useHomeGroups(params?: PageableRequest) {
  return useQuery<Pageable<HomeGroup>, Error>({
    queryKey: ['homeGroups', params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => homeGroupService.getAll(params),
  });
}

/**
 * Hook para obtener un grupo por ID
 */
export function useHomeGroup(id?: string) {
  return useQuery<HomeGroup, Error>({
    queryKey: ['homeGroup', id],
    queryFn: () => homeGroupService.getById(id!),
    enabled: !!id,
  });
}

/**
 * Hook para crear un nuevo grupo
 */
export function useCreateHomeGroup() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateHomeGroupDto) => homeGroupService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['homeGroups'] });
    },
  });
}

/**
 * Hook para actualizar un grupo existente
 */
export function useUpdateHomeGroup() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateHomeGroupDto }) =>
      homeGroupService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['homeGroups'] });
    },
  });
}

/**
 * Hook para eliminar un grupo
 */
export function useDeleteHomeGroup() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => homeGroupService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['homeGroups'] });
    },
  });
}

// ============================================================================
// STRATEGIES HOOKS
// ============================================================================

/**
 * Hook para obtener todas las estrategias
 */
export function useStrategies() {
  return useQuery<Strategy[], Error>({
    queryKey: ['strategies'],
    queryFn: () => strategyService.getAll(),
    staleTime: 5 * 60 * 1000, // 5 minutos - las estrategias no cambian frecuentemente
  });
}

/**
 * Hook para crear una nueva estrategia
 */
export function useCreateStrategy() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: { name: string }) => strategyService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['strategies'] });
    },
  });
}

/**
 * Hook para actualizar una estrategia existente
 */
export function useUpdateStrategy() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: { name: string } }) =>
      strategyService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['strategies'] });
    },
  });
}

/**
 * Hook para eliminar una estrategia
 */
export function useDeleteStrategy() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => strategyService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['strategies'] });
    },
  });
}
