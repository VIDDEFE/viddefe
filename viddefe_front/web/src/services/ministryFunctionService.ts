import { apiService } from './api';
import type { 
  MinistryFunction, 
  CreateMinistryFunctionDto, 
  UpdateMinistryFunctionDto,
  EventType,
  MinistryRole
} from '../models';

export const ministryFunctionService = {
  /**
   * Obtiene todas las funciones ministeriales de una reunión
   * GET /meetings/{meetingId}/ministry-functions?eventType={eventType}
   */
  getAll: (meetingId: string, eventType: EventType) =>
    apiService.get<MinistryFunction[]>(`/meetings/${meetingId}/ministry-functions?eventType=${eventType}`),

  /**
   * Crea una nueva función ministerial
   * POST /meetings/{meetingId}/ministry-functions?eventType={eventType}
   */
  create: (meetingId: string, eventType: EventType, data: CreateMinistryFunctionDto) =>
    apiService.post<MinistryFunction>(`/meetings/${meetingId}/ministry-functions?eventType=${eventType}`, data),

  /**
   * Actualiza una función ministerial existente
   * PUT /meetings/{meetingId}/ministry-functions/{id}?eventType={eventType}
   */
  update: (meetingId: string, id: string, eventType: EventType, data: UpdateMinistryFunctionDto) =>
    apiService.put<MinistryFunction>(`/meetings/${meetingId}/ministry-functions/${id}?eventType=${eventType}`, data),

  /**
   * Elimina una función ministerial
   * DELETE /meetings/{meetingId}/ministry-functions/{id}
   */
  delete: (meetingId: string, id: string) =>
    apiService.delete(`/meetings/${meetingId}/ministry-functions/${id}`),

  /**
   * Obtiene los roles disponibles para funciones ministeriales
   * GET /meetings/ministry-functions/roles
   */
  getRoles: () =>
    apiService.get<MinistryRole[]>('/meetings/ministry-functions/roles'),
};
