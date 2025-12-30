import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { Worship, WorshipType, CreateWorshipDto, UpdateWorshipDto } from '../models';

// Helper para construir query string de sort para Spring Boot
const buildSortParam = (sort?: SortConfig): string => {
  if (!sort) return '';
  return `sort=${sort.field},${sort.direction}`;
};

export const worshipService = {
  /**
   * Obtiene todos los cultos paginados
   */
  getAll: (params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<Worship>>(`/meetings/worships${queryParams}`);
  },

  /**
   * Obtiene un culto por ID
   */
  getById: (id: string) => apiService.get<Worship>(`/meetings/worships/${id}`),

  /**
   * Crea un nuevo culto
   */
  create: (worship: CreateWorshipDto) =>
    apiService.post<Worship>('/meetings/worships', worship),

  /**
   * Actualiza un culto existente
   */
  update: (id: string, worship: UpdateWorshipDto) =>
    apiService.put<Worship>(`/meetings/worships/${id}`, worship),

  /**
   * Elimina un culto
   */
  delete: (id: string) => apiService.delete(`/meetings/worships/${id}`),

  /**
   * Obtiene todos los tipos de culto disponibles
   */
  getTypes: () => apiService.get<WorshipType[]>('/meetings/worships/types'),
};
