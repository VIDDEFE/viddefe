import { apiService } from "./api";

// ============================================================================
// ENUMS - Query Parameter Values (para el parámetro ?type= en la URL)
// ============================================================================

export const MeetingType = {
  TEMPLE_WORSHIP: 'TEMPLE_WORHSIP',  // Valor para query param
  GROUP_MEETING: 'GROUP_MEETING',     // Valor para query param
} as const;

export type MeetingType = typeof MeetingType[keyof typeof MeetingType];

// ============================================================================
// ENUMS - Payload meetingType Values (para el body del request)
// Estos son los valores que el backend usa para deserialización polimórfica
// ============================================================================

export const MeetingTypePayload = {
  WORSHIP: 'WORSHIP',           // Para crear/actualizar cultos
  GROUP_MEETING: 'GROUP_MEETING', // Para crear/actualizar reuniones de grupo
} as const;

export type MeetingTypePayload = typeof MeetingTypePayload[keyof typeof MeetingTypePayload];

// ============================================================================
// TYPES - Common
// ============================================================================

export interface TypePerson {
  id: number;
  name: string;
}

export interface PersonState {
  id: number;
  name: string;
}

export interface Person {
  id: string;
  cc: string;
  firstName: string;
  lastName: string;
  phone: string;
  avatar: string;
  birthDate: string;
  typePerson: TypePerson;
  state: PersonState;
}

export interface SortInfo {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface PageableInfo {
  offset: number;
  sort: SortInfo;
  paged: boolean;
  pageNumber: number;
  pageSize: number;
  unpaged: boolean;
}

export interface PaginatedResponse<T> {
  totalElements: number;
  totalPages: number;
  size: number;
  content: T[];
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  sort: SortInfo;
  pageable: PageableInfo;
  empty: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  status: number;
  message: string;
  errorCode?: string;
  data: T;
  metadata?: Record<string, string>;
  timestamp: string;
}

// ============================================================================
// TYPES - Meeting Request/Response
// ============================================================================

export interface MeetingTypeResponse {
  id: number;
  name: string;
}

export interface MeetingResponse {
  id: string;
  name: string;
  description?: string;
  scheduledDate: string;
  creationDate: string;
  type: MeetingTypeResponse;
}

// ============================================================================
// TYPES - Attendance
// ============================================================================

export interface AttendanceRequest {
  peopleId: string;
  eventId: string;
}

export interface AttendanceRecord {
  people: Person;
  status: string;
}

export interface AttendanceResponse {
  people: Person;
  status: string;
}

export type PaginatedAttendanceResponse = PaginatedResponse<AttendanceRecord>;

// ============================================================================
// TYPES - Pageable params
// ============================================================================

export interface PageableParams {
  page?: number;
  size?: number;
  sort?: string;
}

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

function buildQueryParams(
  type: MeetingType,
  contextId?: string,
  pageable?: PageableParams
): string {
  const params = new URLSearchParams({ type });
  
  if (type === MeetingType.GROUP_MEETING && contextId) {
    params.append('contextId', contextId);
  }
  
  if (pageable) {
    if (pageable.page !== undefined) params.append('page', String(pageable.page));
    if (pageable.size !== undefined) params.append('size', String(pageable.size));
    if (pageable.sort) params.append('sort', pageable.sort);
  }
  
  return params.toString();
}

function buildAttendanceQueryParams(
  type: MeetingType,
  pageable?: PageableParams
): string {
  const params = new URLSearchParams({ type });
  
  if (pageable) {
    if (pageable.page !== undefined) params.append('page', String(pageable.page));
    if (pageable.size !== undefined) params.append('size', String(pageable.size));
    if (pageable.sort) params.append('sort', pageable.sort);
  }
  
  return params.toString();
}

/**
 * Convierte una fecha local a ISO-8601 con offset de timezone
 * El backend REQUIERE el offset, no acepta fechas sin él
 */
function formatDateWithOffset(date: Date): string {
  return date.toISOString();
}

// ============================================================================
// WORSHIP (TEMPLE_WORSHIP) API - Low level (usar worshipService en su lugar)
// ============================================================================

export const worshipApi = {
  getAll: async (pageable?: PageableParams): Promise<MeetingResponse[]> => {
    const query = buildQueryParams(MeetingType.TEMPLE_WORSHIP, undefined, pageable);
    const response = await apiService.get<ApiResponse<MeetingResponse[]>>(`/meetings?${query}`);
    return response.data;
  },

  getById: async (id: string): Promise<MeetingResponse> => {
    const query = buildQueryParams(MeetingType.TEMPLE_WORSHIP);
    const response = await apiService.get<ApiResponse<MeetingResponse>>(`/meetings/${id}?${query}`);
    return response.data;
  },

  getAttendance: async (id: string, pageable?: PageableParams): Promise<PaginatedAttendanceResponse> => {
    const query = buildAttendanceQueryParams(MeetingType.TEMPLE_WORSHIP, pageable);
    const response = await apiService.get<ApiResponse<PaginatedAttendanceResponse>>(`/meetings/${id}/attendance?${query}`);
    return response.data;
  },

  registerAttendance: async (data: AttendanceRequest): Promise<AttendanceResponse> => {
    const query = buildAttendanceQueryParams(MeetingType.TEMPLE_WORSHIP);
    const response = await apiService.put<ApiResponse<AttendanceResponse>>(`/meetings/attendance?${query}`, data);
    return response.data;
  },
};

// ============================================================================
// GROUP MEETING API - Low level (usar groupMeetingService en su lugar)
// ============================================================================

export const groupMeetingApi = {
  getAll: async (groupId: string, pageable?: PageableParams): Promise<MeetingResponse[]> => {
    const query = buildQueryParams(MeetingType.GROUP_MEETING, groupId, pageable);
    const response = await apiService.get<ApiResponse<MeetingResponse[]>>(`/meetings?${query}`);
    return response.data;
  },

  getById: async (id: string, groupId: string): Promise<MeetingResponse> => {
    const query = buildQueryParams(MeetingType.GROUP_MEETING, groupId);
    const response = await apiService.get<ApiResponse<MeetingResponse>>(`/meetings/${id}?${query}`);
    return response.data;
  },

  getAttendance: async (id: string, pageable?: PageableParams): Promise<PaginatedAttendanceResponse> => {
    const query = buildAttendanceQueryParams(MeetingType.GROUP_MEETING, pageable);
    const response = await apiService.get<ApiResponse<PaginatedAttendanceResponse>>(`/meetings/${id}/attendance?${query}`);
    return response.data;
  },

  registerAttendance: async (data: AttendanceRequest): Promise<AttendanceResponse> => {
    const query = buildAttendanceQueryParams(MeetingType.GROUP_MEETING);
    const response = await apiService.put<ApiResponse<AttendanceResponse>>(`/meetings/attendance?${query}`, data);
    return response.data;
  },
};

// ============================================================================
// EXPORTS
// ============================================================================

export const meetingService = {
  worship: worshipApi,
  groupMeeting: groupMeetingApi,
};

export { formatDateWithOffset };
