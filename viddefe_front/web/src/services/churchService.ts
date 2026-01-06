import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { Church, ChurchSummary, ChurchDetail } from '../models';

// Helper para construir query string de sort para Spring Boot
const buildSortParam = (sort?: SortConfig): string => {
  if (!sort) return '';
  return `sort=${sort.field},${sort.direction}`;
};

// Tipo para los bounds del mapa
export interface MapBounds {
  southLat: number;
  westLng: number;
  northLat: number;
  eastLng: number;
}

export const churchService = {
  getAll: (params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<ChurchSummary>>(`/churches${queryParams}`);
  },
  getById: (id: string) => apiService.get<ChurchDetail>(`/churches/${id}`),
  /**
   * Obtiene la iglesia del usuario actual
   */
  getMine: () => apiService.get<ChurchDetail>('/churches/mine'),
  getChildren: (churchId: string, params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<ChurchSummary>>(`/churches/${churchId}/childrens${queryParams}`);
  },
  /**
   * Obtiene iglesias hijas cercanas según los bounds del mapa
   */
  getChildrenNearby: (churchId: string, bounds: MapBounds) => {
    const queryParams = `?southLat=${bounds.southLat}&westLng=${bounds.westLng}&northLat=${bounds.northLat}&eastLng=${bounds.eastLng}`;
    return apiService.get<ChurchSummary[]>(`/churches/${churchId}/childrens/nearby${queryParams}`);
  },
  createChildren: (churchId: string, church: Omit<Church, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Church>(`/churches/${churchId}/childrens`, church),
  create: (church: Omit<Church, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Church>('/churches', church),
  update: (id: string, church: Partial<Church>) =>
    apiService.put<Church>(`/churches/${id}`, church),
  delete: (id: string) => apiService.delete(`/churches/${id}`),
};
