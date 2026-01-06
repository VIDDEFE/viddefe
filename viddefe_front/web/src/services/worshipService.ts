import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { Worship, WorshipDetail, WorshipType, WorshipAttendance, CreateWorshipDto, UpdateWorshipDto } from '../models';

// DTO para registrar asistencia
export interface RegisterAttendanceDto {
  peopleId: string;
  eventId: string;
}

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
    return apiService.get<Pageable<Worship>>(`/worship/meeting${queryParams}`);
  },

  /**
   * Obtiene un culto por ID con detalle de asistencia
   */
  getById: (id: string) => apiService.get<WorshipDetail>(`/worship/meeting/${id}`),

  /**
   * Crea un nuevo culto
   */
  create: (worship: CreateWorshipDto) =>
    apiService.post<Worship>('/worship/meeting', worship),

  /**
   * Actualiza un culto existente
   */
  update: (id: string, worship: UpdateWorshipDto) =>
    apiService.put<Worship>(`/worship/meeting/${id}`, worship),

  /**
   * Elimina un culto
   */
  delete: (id: string) => apiService.delete(`/worship/meeting/${id}`),

  /**
   * Obtiene todos los tipos de culto disponibles
   * GET /meetings/worship/types
   */
  getTypes: () => apiService.get<WorshipType[]>('/meetings/worship/types'),

  /**
   * Obtiene la asistencia de un culto paginada
   * Devuelve TODAS las personas relacionadas con el meeting
   * Si una persona no tiene registro de asistencia, su status es "ABSENT"
   */
  getAttendance: (id: string, params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<WorshipAttendance>>(`/worship/meeting/${id}/attendance${queryParams}`);
  },

  /**
   * Registra o cambia la asistencia de una persona a un culto
   * Si no existe registro → crea con PRESENT
   * Si existe → alterna el estado (PRESENT ↔ ABSENT)
   */
  registerAttendance: (data: RegisterAttendanceDto) =>
    apiService.put<WorshipAttendance>('/worship/meeting/attendance', data),
};
