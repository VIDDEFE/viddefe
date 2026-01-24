import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { Person } from '../models';

// Tipo para los tipos de persona
export interface PersonType {
  id: number;
  name: string;
}

// Parámetros extendidos para búsqueda de personas
/**
 * Parámetros extendidos para búsqueda de personas
 * attendanceQuality: Filtra por calidad de asistencia ("HIGH", "MEDIUM", "LOW", "NO_YET")
 */
export interface PeopleSearchParams extends PageableRequest {
  typePersonId?: number;
  search?: string; // Búsqueda por nombre, teléfono, etc.
  /**
   * Filtra por calidad de asistencia. Valores: "HIGH", "MEDIUM", "LOW", "NO_YET"
   */
  attendanceQuality?: 'HIGH' | 'MEDIUM' | 'LOW' | 'NO_YET';
}

// Helper para construir query string de sort para Spring Boot
const buildSortParam = (sort?: SortConfig): string => {
  if (!sort) return '';
  return `sort=${sort.field},${sort.direction}`;
};

export const personService = {
  getAll: (params?: PeopleSearchParams) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.typePersonId !== undefined) queryParts.push(`typePersonId=${params.typePersonId}`);
    if (params?.search) queryParts.push(`search=${encodeURIComponent(params.search)}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    if (params?.attendanceQuality) queryParts.push(`attendanceQuality=${params.attendanceQuality}`);
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<Person>>(`/people${queryParams}`);
  },
  getById: (id: string) => apiService.get<Person>(`/people/${id}`),
  create: (person: Omit<Person, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Person>('/people', person),
  update: (id: string, person: Partial<Person>) =>
    apiService.put<Person>(`/people/${id}`, person),
  delete: (id: string) => apiService.delete(`/people/${id}`),
  getByChurch: (churchId: string, params?: PageableRequest) => {
    const queryParams = params ? `&page=${params.page}&size=${params.size}` : '';
    return apiService.get<Person[]>(`/people?churchId=${churchId}${queryParams}`);
  },
  // Obtener tipos de persona
  getTypes: () => apiService.get<PersonType[]>('/people/types'),
};
