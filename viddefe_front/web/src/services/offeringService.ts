import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { Offering, OfferingType, CreateOfferingDto, UpdateOfferingDto } from '../models';

// Helper para construir query string de sort para Spring Boot
const buildSortParam = (sort?: SortConfig): string => {
  if (!sort) return '';
  return `sort=${sort.field},${sort.direction}`;
};

export const offeringService = {
  /**
   * Obtiene las ofrendas de un evento paginadas
   */
  getByEventId: (eventId: string, params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<Offering>>(`/offerings/event/${eventId}${queryParams}`);
  },

  /**
   * Crea una nueva ofrenda
   */
  create: (data: CreateOfferingDto) =>
    apiService.post<Offering>('/offerings', data),

  /**
   * Actualiza una ofrenda existente
   */
  update: (data: UpdateOfferingDto) =>
    apiService.put<Offering>('/offerings', data),

  /**
   * Elimina una ofrenda
   */
  delete: (id: string) => apiService.delete(`/offerings/${id}`),

  /**
   * Obtiene todos los tipos de ofrenda disponibles
   */
  getTypes: () => apiService.get<OfferingType[]>('/offerings/types'),
};
