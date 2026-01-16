import { apiService, type PageableRequest, type Pageable } from './api';
import { MeetingType } from './meetingService';
import type { 
  Meeting, 
  MeetingType as MeetingTypeModel, 
  CreateMeetingDto, 
  UpdateMeetingDto,
  MeetingAttendance 
} from '../models';
import { validateDatePayload } from '../utils/helpers';

// ============================================================================
// TYPES
// ============================================================================

export interface RegisterMeetingAttendanceDto {
  peopleId: string;
  eventId: string;
}

// Payload para crear/actualizar reuniones de grupo según contrato del backend
interface CreateGroupMeetingPayload {
  meetingType: 'GROUP_MEETING';
  name: string;
  description?: string;
  scheduledDate: string; // ISO-8601 con offset obligatorio (ej: "2026-01-15T10:00:00-05:00")
  groupMeetingTypeId: number;
}

interface UpdateGroupMeetingPayload {
  meetingType: 'GROUP_MEETING';
  name?: string;
  description?: string;
  scheduledDate?: string; // ISO-8601 con offset obligatorio
  groupMeetingTypeId?: number;
}

// ============================================================================
// GROUP MEETING SERVICE
// ============================================================================

export const groupMeetingService = {
  /**
   * Obtiene todas las reuniones de un grupo paginadas
   * GET /meetings?type=GROUP_MEETING&contextId={groupId}
   */
  getAll: async (groupId: string, params?: PageableRequest): Promise<Pageable<Meeting>> => {
    const query = new URLSearchParams({ 
      type: MeetingType.GROUP_MEETING,
      contextId: groupId,
    });
    
    if (params?.page !== undefined) query.append('page', String(params.page));
    if (params?.size !== undefined) query.append('size', String(params.size));
    if (params?.sort) query.append('sort', `${params.sort.field},${params.sort.direction}`);
    
    const response = await apiService.get<Pageable<Meeting>>(`/meetings?${query.toString()}`);
    return response;
  },

  /**
   * Obtiene una reunión por ID
   * GET /meetings/{id}?type=GROUP_MEETING&contextId={groupId}
   */
  getById: async (groupId: string, id: string): Promise<Meeting> => {
    const query = new URLSearchParams({ 
      type: MeetingType.GROUP_MEETING,
      contextId: groupId,
    });
    const response = await apiService.get<Meeting>(`/meetings/${id}?${query.toString()}`);
    return response;
  },

  /**
   * Crea una nueva reunión de grupo
   * POST /meetings?type=GROUP_MEETING&contextId={groupId}
   * 
   * IMPORTANTE: meetingType debe ser "GROUP_MEETING" (string constante, NO un ID)
   * IMPORTANTE: scheduledDate DEBE incluir timezone (ej: "2026-01-15T10:00:00-05:00")
   */
  create: async (groupId: string, data: CreateMeetingDto): Promise<Meeting> => {
    const query = new URLSearchParams({ 
      type: MeetingType.GROUP_MEETING,
      contextId: groupId,
    });
    
    // Validar que date tenga timezone (requerido por el backend)
    validateDatePayload(data.date, 'scheduledDate');
    
    // Construir payload según contrato del backend
    const payload: CreateGroupMeetingPayload = {
      meetingType: 'GROUP_MEETING', // Constante del enum, NO un ID
      name: data.name,
      description: data.description,
      scheduledDate: data.date, // Debe incluir offset de timezone
      groupMeetingTypeId: data.groupMeetingTypeId,
    };
    
    const response = await apiService.post<Meeting>(`/meetings?${query.toString()}`, payload);
    return response;
  },

  /**
   * Actualiza una reunión existente
   * PUT /meetings/{id}?type=GROUP_MEETING&contextId={groupId}
   * 
   * IMPORTANTE: scheduledDate DEBE incluir timezone si se provee
   */
  update: async (groupId: string, id: string, data: UpdateMeetingDto): Promise<Meeting> => {
    const query = new URLSearchParams({ 
      type: MeetingType.GROUP_MEETING,
      contextId: groupId,
    });
    
    // Validar que date tenga timezone si se proporciona
    if (data.date) {
      validateDatePayload(data.date, 'scheduledDate');
    }
    
    const payload: UpdateGroupMeetingPayload = {
      meetingType: 'GROUP_MEETING',
      name: data.name,
      description: data.description,
      scheduledDate: data.date,
      groupMeetingTypeId: data.groupMeetingTypeId,
    };
    
    const response = await apiService.put<Meeting>(`/meetings/${id}?${query.toString()}`, payload);
    return response;
  },

  /**
   * Elimina una reunión
   * DELETE /meetings/{id}?type=GROUP_MEETING&contextId={groupId}
   */
  delete: async (groupId: string, id: string): Promise<void> => {
    const query = new URLSearchParams({ 
      type: MeetingType.GROUP_MEETING,
      contextId: groupId,
    });
    await apiService.delete(`/meetings/${id}?${query.toString()}`);
  },

  /**
   * Obtiene los tipos de reunión de grupo disponibles
   * GET /meetings/group/types
   */
  getTypes: async (): Promise<MeetingTypeModel[]> => {
    const response = await apiService.get<MeetingTypeModel[]>('/meetings/group/types');
    return response;
  },

  /**
   * Obtiene la asistencia de una reunión paginada
   * GET /meetings/{id}/attendance?type=GROUP_MEETING
   */
  getAttendance: async (id: string, params?: PageableRequest): Promise<Pageable<MeetingAttendance>> => {
    const query = new URLSearchParams({ type: MeetingType.GROUP_MEETING });
    
    if (params?.page !== undefined) query.append('page', String(params.page));
    if (params?.size !== undefined) query.append('size', String(params.size));
    if (params?.sort) query.append('sort', `${params.sort.field},${params.sort.direction}`);
    
    const response = await apiService.get<Pageable<MeetingAttendance>>(`/meetings/${id}/attendance?${query.toString()}`);
    return response;
  },

  /**
   * Registra o cambia asistencia de una persona
   * PUT /meetings/attendance?type=GROUP_MEETING
   */
  registerAttendance: async (data: RegisterMeetingAttendanceDto): Promise<MeetingAttendance> => {
    const query = new URLSearchParams({ type: MeetingType.GROUP_MEETING });
    const response = await apiService.put<MeetingAttendance>(`/meetings/attendance?${query.toString()}`, data);
    return response;
  },
};
