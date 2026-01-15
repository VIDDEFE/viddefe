import { apiService, type PageableRequest } from './api';
import { MeetingType } from './meetingService';
import type { 
  Worship, 
  WorshipDetail, 
  WorshipType, 
  CreateWorshipDto, 
  UpdateWorshipDto,
  WorshipAttendance 
} from '../models';
import type { Pageable } from './api';

// ============================================================================
// TYPES
// ============================================================================

export interface RegisterAttendanceDto {
  peopleId: string;
  eventId: string;
}

// Payload para crear/actualizar cultos según contrato del backend
interface CreateWorshipPayload {
  meetingType: 'WORSHIP';
  name: string;
  description?: string;
  scheduledDate: string; // ISO-8601 con offset obligatorio
  worshipTypeId: number;
}

interface UpdateWorshipPayload {
  meetingType: 'WORSHIP';
  name?: string;
  description?: string;
  scheduledDate?: string;
  worshipTypeId?: number;
}

// ============================================================================
// WORSHIP SERVICE
// ============================================================================

export const worshipService = {
  /**
   * Obtiene todos los cultos paginados
   * GET /meetings?type=TEMPLE_WORHSIP
   */
  getAll: async (params?: PageableRequest): Promise<Pageable<Worship>> => {
    const query = new URLSearchParams({ type: MeetingType.TEMPLE_WORSHIP });
    
    if (params?.page !== undefined) query.append('page', String(params.page));
    if (params?.size !== undefined) query.append('size', String(params.size));
    if (params?.sort) query.append('sort', `${params.sort.field},${params.sort.direction}`);
    
    const response = await apiService.get<Pageable<Worship>>(`/meetings?${query.toString()}`);
    return response;
  },

  /**
   * Obtiene un culto por ID con detalle
   * GET /meetings/{id}?type=TEMPLE_WORHSIP
   */
  getById: async (id: string): Promise<WorshipDetail> => {
    const query = new URLSearchParams({ type: MeetingType.TEMPLE_WORSHIP });
    const response = await apiService.get<WorshipDetail>(`/meetings/${id}?${query.toString()}`);
    return response;
  },

  /**
   * Crea un nuevo culto
   * POST /meetings?type=TEMPLE_WORHSIP
   * 
   * IMPORTANTE: meetingType debe ser "WORSHIP" (string constante, NO un ID)
   */
  create: async (data: CreateWorshipDto): Promise<Worship> => {
    const query = new URLSearchParams({ type: MeetingType.TEMPLE_WORSHIP });
    
    // Construir payload según contrato del backend
    const payload: CreateWorshipPayload = {
      meetingType: 'WORSHIP', // Constante del enum, NO un ID
      name: data.name,
      description: data.description,
      scheduledDate: data.scheduledDate, // Debe incluir offset de timezone
      worshipTypeId: data.worshipTypeId as number,
    };
    
    const response = await apiService.post<Worship>(`/meetings?${query.toString()}`, payload);
    return response;
  },

  /**
   * Actualiza un culto existente
   * PUT /meetings/{id}?type=TEMPLE_WORHSIP
   */
  update: async (id: string, data: UpdateWorshipDto): Promise<Worship> => {
    const query = new URLSearchParams({ type: MeetingType.TEMPLE_WORSHIP });
    
    const payload: UpdateWorshipPayload = {
      meetingType: 'WORSHIP',
      name: data.name,
      description: data.description,
      scheduledDate: data.scheduledDate,
      worshipTypeId: data.worshipTypeId,
    };
    
    const response = await apiService.put<Worship>(`/meetings/${id}?${query.toString()}`, payload);
    return response;
  },

  /**
   * Elimina un culto
   * DELETE /meetings/{id}?type=TEMPLE_WORHSIP
   */
  delete: async (id: string): Promise<void> => {
    const query = new URLSearchParams({ type: MeetingType.TEMPLE_WORSHIP });
    await apiService.delete(`/meetings/${id}?${query.toString()}`);
  },

  /**
   * Obtiene los tipos de culto disponibles
   * GET /meetings/worship/types
   */
  getTypes: async (): Promise<WorshipType[]> => {
    const response = await apiService.get<WorshipType[]>('/meetings/worship/types');
    return response;
  },

  /**
   * Obtiene la asistencia de un culto paginada
   * GET /meetings/{id}/attendance?type=TEMPLE_WORHSIP
   */
  getAttendance: async (id: string, params?: PageableRequest): Promise<Pageable<WorshipAttendance>> => {
    const query = new URLSearchParams({ type: MeetingType.TEMPLE_WORSHIP });
    
    if (params?.page !== undefined) query.append('page', String(params.page));
    if (params?.size !== undefined) query.append('size', String(params.size));
    if (params?.sort) query.append('sort', `${params.sort.field},${params.sort.direction}`);
    
    const response = await apiService.get<Pageable<WorshipAttendance>>(`/meetings/${id}/attendance?${query.toString()}`);
    return response;
  },

  /**
   * Registra o cambia asistencia de una persona
   * PUT /meetings/attendance?type=TEMPLE_WORHSIP
   */
  registerAttendance: async (data: RegisterAttendanceDto): Promise<WorshipAttendance> => {
    const query = new URLSearchParams({ type: MeetingType.TEMPLE_WORSHIP });
    const response = await apiService.put<WorshipAttendance>(`/meetings/attendance?${query.toString()}`, data);
    return response;
  },
};
