import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { HomeGroup, Strategy, CreateHomeGroupDto, UpdateHomeGroupDto } from '../models';

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
   * Obtiene un grupo por ID
   */
  getById: (id: string) => apiService.get<HomeGroup>(`/groups/${id}`),

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
};
