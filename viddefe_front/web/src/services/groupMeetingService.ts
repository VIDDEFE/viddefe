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

// Payload para crear/actualizar reuniones según contrato unificado del backend
interface CreateMeetingPayload {
  meetingType: 'GROUP_MEETING' | 'WORSHIP';
  name: string;
  description?: string;
  scheduledDate: string; // ISO-8601 con offset obligatorio (ej: "2026-01-15T10:00:00-05:00")
  meetingTypeId: number;
}

interface UpdateMeetingPayload {
  meetingType: 'GROUP_MEETING' | 'WORSHIP';
  name?: string;
  description?: string;
  scheduledDate?: string; // ISO-8601 con offset obligatorio
  meetingTypeId?: number;
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
    
    // Usar scheduledDate (campo principal del nuevo contrato)
    const dateValue = data.scheduledDate;
    if (!dateValue) {
      throw new Error('scheduledDate es requerido');
    }
    
    // Validar que scheduledDate tenga timezone (requerido por el backend)
    validateDatePayload(dateValue, 'scheduledDate');
    
    // Construir payload según contrato unificado del backend
    const payload: CreateMeetingPayload = {
      meetingType: 'GROUP_MEETING', // Constante del enum
      name: data.name,
      description: data.description,
      scheduledDate: dateValue, // Debe incluir offset de timezone
      meetingTypeId: data.meetingTypeId,
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
    
    // Usar scheduledDate (campo principal del nuevo contrato)
    const dateValue = data.scheduledDate;
    
    // Validar que scheduledDate tenga timezone si se proporciona
    if (dateValue) {
      validateDatePayload(dateValue, 'scheduledDate');
    }
    
    const payload: UpdateMeetingPayload = {
      meetingType: 'GROUP_MEETING',
      name: data.name,
      description: data.description,
      scheduledDate: dateValue,
      meetingTypeId: data.meetingTypeId,
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
   * GET /meetings/{id}/attendance?type=GROUP_MEETING&groupId={groupId}
   */
  getAttendance: async (groupId: string, id: string, params?: PageableRequest): Promise<Pageable<MeetingAttendance>> => {
    if (!groupId) throw new Error("El parámetro 'groupId' es obligatorio para reuniones de tipo GROUP_MEETING");
    const query = new URLSearchParams({ type: MeetingType.GROUP_MEETING, groupId: groupId });
    if (params?.page !== undefined) query.append('page', String(params.page));
    if (params?.size !== undefined) query.append('size', String(params.size));
    if (params?.sort) query.append('sort', `${params.sort.field},${params.sort.direction}`);
    const response = await apiService.get<Pageable<MeetingAttendance>>(`/meetings/${id}/attendance?${query.toString()}`);
    return response;
  },

  /**
   * Registra o cambia asistencia de una persona
   * PUT /meetings/attendance?type=GROUP_MEETING&contextId={groupId}
   */
  registerAttendance: async (groupId: string, meetingId: string, data: RegisterMeetingAttendanceDto): Promise<MeetingAttendance> => {
    if (!groupId) throw new Error("El parámetro 'groupId' es obligatorio para reuniones de tipo GROUP_MEETING");
    const query = new URLSearchParams({ type: MeetingType.GROUP_MEETING, contextId: groupId });
    const response = await apiService.put<MeetingAttendance>(`/meetings/attendance?${query.toString()}`, { ...data, eventId: meetingId });
    return response;
  },
};
