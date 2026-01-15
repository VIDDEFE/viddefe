import { apiService, type PageableRequest, type Pageable } from './api';
import { MeetingType } from './meetingService';
import type { 
  Meeting, 
  MeetingType as MeetingTypeModel, 
  CreateMeetingDto, 
  UpdateMeetingDto,
  MeetingAttendance 
} from '../models';

// ============================================================================
// TYPES
// ============================================================================

export interface RegisterMeetingAttendanceDto {
  peopleId: string;
  eventId: string;
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
   */
  create: async (groupId: string, data: CreateMeetingDto): Promise<Meeting> => {
    const query = new URLSearchParams({ 
      type: MeetingType.GROUP_MEETING,
      contextId: groupId,
    });
    const payload = {
      name: data.name,
      description: data.description,
      scheduledDate: data.date,
      meetingType: String(data.groupMeetingTypeId),
      groupMeetingTypeId: data.groupMeetingTypeId,
      date: data.date,
    };
    const response = await apiService.post<Meeting>(`/meetings?${query.toString()}`, payload);
    return response;
  },

  /**
   * Actualiza una reunión existente
   * PUT /meetings/{id}?type=GROUP_MEETING&contextId={groupId}
   */
  update: async (groupId: string, id: string, data: UpdateMeetingDto): Promise<Meeting> => {
    const query = new URLSearchParams({ 
      type: MeetingType.GROUP_MEETING,
      contextId: groupId,
    });
    const payload = {
      name: data.name,
      description: data.description,
      scheduledDate: data.date,
      meetingType: data.groupMeetingTypeId ? String(data.groupMeetingTypeId) : undefined,
      groupMeetingTypeId: data.groupMeetingTypeId,
      date: data.date,
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
