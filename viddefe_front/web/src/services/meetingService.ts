import { apiService, type Pageable, type PageableRequest, type SortConfig } from './api';
import type { Meeting, MeetingAttendance, MeetingType, CreateMeetingDto, UpdateMeetingDto } from '../models';

// DTO para registrar asistencia
export interface RegisterMeetingAttendanceDto {
  peopleId: string;
  eventId: string;
}

// Helper para construir query string de sort para Spring Boot
const buildSortParam = (sort?: SortConfig): string => {
  if (!sort) return '';
  return `sort=${sort.field},${sort.direction}`;
};

export const meetingService = {
  /**
   * Obtiene todas las reuniones de un grupo paginadas
   * GET /groups/{groupId}/meetings
   */
  getAll: (groupId: string, params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<Meeting>>(`/groups/${groupId}/meetings${queryParams}`);
  },

  /**
   * Obtiene una reunión por ID
   * GET /groups/{groupId}/meetings/{meetingId}
   */
  getById: (groupId: string, meetingId: string) =>
    apiService.get<Meeting>(`/groups/${groupId}/meetings/${meetingId}`),

  /**
   * Crea una nueva reunión
   * POST /groups/{groupId}/meetings
   */
  create: (groupId: string, meeting: CreateMeetingDto) =>
    apiService.post<Meeting>(`/groups/${groupId}/meetings`, meeting),

  /**
   * Actualiza una reunión existente
   * PUT /groups/{groupId}/meetings/{meetingId}
   */
  update: (groupId: string, meetingId: string, meeting: UpdateMeetingDto) =>
    apiService.put<Meeting>(`/groups/${groupId}/meetings/${meetingId}`, meeting),

  /**
   * Elimina una reunión
   * DELETE /groups/{groupId}/meetings/{meetingId}
   */
  delete: (groupId: string, meetingId: string) =>
    apiService.delete(`/groups/${groupId}/meetings/${meetingId}`),

  /**
   * Obtiene los tipos de reunión de grupo disponibles
   * GET /meetings/group/types
   */
  getTypes: () => apiService.get<MeetingType[]>('/meetings/group/types'),

  /**
   * Obtiene la asistencia de una reunión paginada
   * GET /groups/{groupId}/meetings/{meetingId}/attendance
   */
  getAttendance: (groupId: string, meetingId: string, params?: PageableRequest) => {
    const queryParts: string[] = [];
    if (params?.page !== undefined) queryParts.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParts.push(`size=${params.size}`);
    if (params?.sort) queryParts.push(buildSortParam(params.sort));
    const queryParams = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return apiService.get<Pageable<MeetingAttendance>>(`/groups/${groupId}/meetings/${meetingId}/attendance${queryParams}`);
  },

  /**
   * Registra o cambia la asistencia de una persona a una reunión
   * Si no existe registro → crea con PRESENT
   * Si existe → alterna el estado (PRESENT ↔ ABSENT)
   * PUT /groups/meetings/attendance
   */
  registerAttendance: (groupId: string, data: RegisterMeetingAttendanceDto) =>
    apiService.put<MeetingAttendance>(`/groups/${groupId}/meetings/attendance`, data),
};
