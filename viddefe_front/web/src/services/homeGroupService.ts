import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { 
  HomeGroup, 
  Strategy, 
  CreateHomeGroupDto, 
  UpdateHomeGroupDto, 
  HomeGroupDetailResponse,
  RoleStrategyNode,
  CreateRoleDto,
  UpdateRoleDto,
  AssignPersonToRoleDto,
  AssignPeopleToRoleDto
} from '../models';

// Helper para construir query string de sort para Spring Boot
const buildSortParam = (sort?: SortConfig): string => {
  if (!sort) return '';
  return `sort=${sort.field},${sort.direction}`;
};

export const homeGroupService = {
  /**
   * Obtiene todos los grupos paginados (churchId se obtiene del token)
   */
  getAll: (params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<HomeGroup>>(`/groups${queryParams}`);
  },

  /**
   * Obtiene un grupo por ID (respuesta básica)
   */
  getById: (id: string) => apiService.get<HomeGroup>(`/groups/${id}`),

  /**
   * Obtiene el detalle completo de un grupo incluyendo jerarquía de roles
   */
  getDetail: (id: string) => apiService.get<HomeGroupDetailResponse>(`/groups/${id}`),

  /**
   * Crea un nuevo grupo
   */
  create: (group: CreateHomeGroupDto) =>
    apiService.post<HomeGroup>('/groups', group),

  /**
   * Actualiza un grupo existente
   */
  update: (id: string, group: UpdateHomeGroupDto) =>
    apiService.put<HomeGroup>(`/groups/${id}`, group),

  /**
   * Elimina un grupo
   */
  delete: (id: string) => apiService.delete(`/groups/${id}`),
};

export const strategyService = {
  /**
   * Obtiene todas las estrategias
   */
  getAll: () => apiService.get<Strategy[]>('/groups/strategies'),

  /**
   * Crea una nueva estrategia
   */
  create: (strategy: { name: string }) =>
    apiService.post<Strategy>('/groups/strategies', strategy),

  /**
   * Actualiza una estrategia existente
   */
  update: (id: string, strategy: { name: string }) =>
    apiService.put<Strategy>(`/groups/strategies/${id}`, strategy),

  /**
   * Elimina una estrategia
   */
  delete: (id: string) => apiService.delete(`/groups/strategies/${id}`),

  /**
   * Obtiene la jerarquía de roles de una estrategia
   */
  getRoles: (strategyId: string) =>
    apiService.get<RoleStrategyNode[]>(`/groups/strategies/${strategyId}/roles`),
};

/**
 * Servicio para gestionar roles dentro de una estrategia (estructura abstracta)
 * Los roles pertenecen a la estrategia, NO al grupo
 */
export const roleService = {
  /**
   * Crea un nuevo rol en una estrategia (solo estructura)
   */
  create: (strategyId: string, role: CreateRoleDto) =>
    apiService.post<RoleStrategyNode>(`/groups/strategies/${strategyId}/roles`, role),

  /**
   * Actualiza un rol existente (solo estructura: nombre y padre)
   */
  update: (strategyId: string, roleId: string, role: UpdateRoleDto) =>
    apiService.put<RoleStrategyNode>(`/groups/strategies/${strategyId}/roles/${roleId}`, role),

  /**
   * Elimina un rol (solo si no tiene hijos)
   */
  delete: (strategyId: string, roleId: string): Promise<void> =>
    apiService.delete(`/groups/strategies/${strategyId}/roles/${roleId}`),
};

/**
 * Servicio para gestionar la asignación de personas a roles dentro de un grupo
 * Las personas se asignan a roles en el contexto de un grupo específico
 */
export const roleAssignmentService = {
  /**
   * Asigna una persona a un rol
   */
  assignPerson: (groupId: string, roleId: string, data: AssignPersonToRoleDto): Promise<void> =>
    apiService.post(`/groups/${groupId}/strategies/roles/${roleId}/people`, data),

  /**
   * Asigna múltiples personas a un rol
   */
  assignPeople: (groupId: string, roleId: string, data: AssignPeopleToRoleDto): Promise<void> =>
    apiService.post(`/groups/${groupId}/strategies/roles/${roleId}/people/batch`, data),

  /**
   * Remueve una persona de un rol
   */
  removePerson: (groupId: string, roleId: string, personId: string): Promise<void> =>
    apiService.delete(`/groups/${groupId}/strategies/roles/${roleId}/people/${personId}`),
};
