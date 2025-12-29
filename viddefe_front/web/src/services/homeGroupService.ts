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
  RolePeopleDto
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
  getAll: () => apiService.get<Strategy[]>('/strategies'),

  /**
   * Crea una nueva estrategia
   */
  create: (strategy: { name: string }) =>
    apiService.post<Strategy>('/strategies', strategy),

  /**
   * Actualiza una estrategia existente
   */
  update: (id: string, strategy: { name: string }) =>
    apiService.put<Strategy>(`/strategies/${id}`, strategy),

  /**
   * Elimina una estrategia
   */
  delete: (id: string) => apiService.delete(`/strategies/${id}`),

  /**
   * Obtiene la jerarquía de roles de una estrategia
   */
  getRoles: (strategyId: string) =>
    apiService.get<RoleStrategyNode[]>(`/strategies/${strategyId}/roles`),
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
    apiService.post<RoleStrategyNode>(`/strategies/${strategyId}/roles`, role),

  /**
   * Actualiza un rol existente (solo estructura: nombre y padre)
   */
  update: (strategyId: string, roleId: string, role: UpdateRoleDto) =>
    apiService.put<RoleStrategyNode>(`/strategies/${strategyId}/roles/${roleId}`, role),

  /**
   * Elimina un rol (solo si no tiene hijos)
   */
  delete: (strategyId: string, roleId: string): Promise<void> =>
    apiService.delete(`/strategies/${strategyId}/roles/${roleId}`),
};

/**
 * Servicio para gestionar la asignación de personas a roles
 */
export const roleAssignmentService = {
  /**
   * Asigna personas a un rol
   * POST /groups/strategy/role/{roleId}/assign
   */
  assign: (roleId: string, data: RolePeopleDto): Promise<void> =>
    apiService.post(`/groups/strategy/role/${roleId}/assign`, data),

  /**
   * Remueve personas de un rol
   * DELETE /groups/strategy/role/{roleId}/remove
   */
  remove: (roleId: string, data: RolePeopleDto): Promise<void> =>
    apiService.deleteWithBody(`/groups/strategy/role/${roleId}/remove`, data),
};
