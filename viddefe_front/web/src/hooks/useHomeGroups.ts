import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { homeGroupService, strategyService, roleService, roleAssignmentService } from '../services/homeGroupService';
import type { MapBounds } from '../services/churchService';
import type { 
  HomeGroup, Strategy, CreateHomeGroupDto, UpdateHomeGroupDto, 
  HomeGroupDetailResponse, CreateRoleDto, UpdateRoleDto, RoleStrategyNode
} from '../models';
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
 * Hook para obtener grupos cercanos según los bounds del mapa
 */
export function useNearbyHomeGroups(bounds?: MapBounds | null) {
  return useQuery<HomeGroup[], Error>({
    queryKey: ['homeGroups', 'nearby', bounds?.southLat, bounds?.westLng, bounds?.northLat, bounds?.eastLng],
    queryFn: () => homeGroupService.getNearby(bounds!),
    enabled: !!bounds,
    staleTime: 30000, // 30 segundos para evitar refetches excesivos al mover el mapa
  });
}

/**
 * Hook para obtener un grupo por ID (respuesta básica)
 */
export function useHomeGroup(id?: string) {
  return useQuery<HomeGroup, Error>({
    queryKey: ['homeGroup', id],
    queryFn: () => homeGroupService.getById(id!),
    enabled: !!id,
  });
}

/**
 * Hook para obtener el detalle completo de un grupo incluyendo jerarquía de roles
 */
export function useHomeGroupDetail(id?: string) {
  return useQuery<HomeGroupDetailResponse, Error>({
    queryKey: ['homeGroupDetail', id],
    queryFn: () => homeGroupService.getDetail(id!),
    enabled: !!id,
  });
}

/**
 * Hook para obtener el grupo al que pertenece el usuario actual
 */
export function useMyHomeGroup() {
  return useQuery<HomeGroupDetailResponse, Error>({
    queryKey: ['myHomeGroup'],
    queryFn: () => homeGroupService.getMine(),
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

/**
 * Hook para obtener la jerarquía de roles de una estrategia
 */
export function useStrategyRoles(strategyId?: string) {
  return useQuery<RoleStrategyNode[], Error>({
    queryKey: ['strategyRoles', strategyId],
    queryFn: () => strategyService.getRoles(strategyId!),
    enabled: !!strategyId,
  });
}

// ============================================================================
// ROLES HOOKS (Estructura de roles en una estrategia)
// ============================================================================

/**
 * Hook para crear un nuevo rol dentro de una estrategia
 */
export function useCreateRole(strategyId: string) {
  const qc = useQueryClient();

  return useMutation<RoleStrategyNode, Error, CreateRoleDto>({
    mutationFn: (data: CreateRoleDto) => roleService.create(strategyId, data),
    onSuccess() {
      // Invalidar roles de la estrategia y detalles de grupos que usen esta estrategia
      qc.invalidateQueries({ queryKey: ['strategyRoles', strategyId] });
      qc.invalidateQueries({ queryKey: ['homeGroupDetail'] });
    },
  });
}

/**
 * Hook para actualizar un rol existente
 */
export function useUpdateRole(strategyId: string) {
  const qc = useQueryClient();

  return useMutation<RoleStrategyNode, Error, { roleId: string; data: UpdateRoleDto }>({
    mutationFn: ({ roleId, data }) => roleService.update(strategyId, roleId, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['strategyRoles', strategyId] });
      qc.invalidateQueries({ queryKey: ['homeGroupDetail'] });
    },
  });
}

/**
 * Hook para eliminar un rol
 */
export function useDeleteRole(strategyId: string) {
  const qc = useQueryClient();

  return useMutation<void, Error, string>({
    mutationFn: (roleId: string) => roleService.delete(strategyId, roleId),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['strategyRoles', strategyId] });
      qc.invalidateQueries({ queryKey: ['homeGroupDetail'] });
    },
  });
}

// ============================================================================
// ROLE ASSIGNMENT HOOKS (Asignación de personas a roles)
// ============================================================================

/**
 * Hook para asignar personas a un rol
 */
export function useAssignPeopleToRole(groupId: string) {
  const qc = useQueryClient();

  return useMutation<void, Error, { roleId: string; peopleIds: string[] }>({
    mutationFn: ({ roleId, peopleIds }) => roleAssignmentService.assign(roleId, { peopleIds }),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['homeGroupDetail', groupId] });
    },
  });
}

/**
 * Hook para remover personas de un rol
 */
export function useRemovePeopleFromRole(groupId: string) {
  const qc = useQueryClient();

  return useMutation<void, Error, { roleId: string; peopleIds: string[] }>({
    mutationFn: ({ roleId, peopleIds }) => roleAssignmentService.remove(roleId, { peopleIds }),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['homeGroupDetail', groupId] });
    },
  });
}
