import { apiService } from './api';

// ============================================================================
// TYPES - Métricas de Asistencia
// ============================================================================

/**
 * Métricas base que aplica a todos los tipos de reunión
 */
export interface BaseMetrics {
  newAttendees: number;
  totalPeopleAttended: number;
  totalPeople: number;
  attendanceRate: number;
  absenceRate: number;
  totalMeetings: number;
  averageAttendancePerMeeting: number;
}

/**
 * Métricas adicionales para worship/temple worship
 * Incluye desglose entre métricas de grupos y métricas de iglesia
 */
export interface WorshipMetrics extends BaseMetrics {
  totalGroups: number;
  groupMetrics: BaseMetrics;
  churchMetrics: BaseMetrics;
}

/**
 * Parámetros para consultar métricas
 */
export interface MetricsQueryParams {
  type: 'TEMPLE_WORHSIP' | 'GROUP_MEETING';
  contextId?: string; // Requerido para TEMPLE_WORHSIP
  startTime: string;  // ISO-8601 con timezone
  endTime: string;    // ISO-8601 con timezone
}

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

/**
 * Construye el query string para la solicitud de métricas
 */
function buildMetricsQueryParams(params: MetricsQueryParams): string {
  const searchParams = new URLSearchParams();
  
  searchParams.append('type', params.type);
  searchParams.append('startTime', params.startTime);
  searchParams.append('endTime', params.endTime);
  
  if (params.contextId) {
    searchParams.append('contextId', params.contextId);
  }
  
  return searchParams.toString();
}

// ============================================================================
// METRICS SERVICE
// ============================================================================

export const metricsService = {
  /**
   * Obtiene métricas de asistencia para un rango de fechas
   * 
   * @param params - Parámetros de la consulta (type, contextId, startTime, endTime)
   * @returns Métricas de asistencia (BaseMetrics para GROUP_MEETING, WorshipMetrics para TEMPLE_WORHSIP)
   * 
   * @example
   * // Para GROUP_MEETING
   * const metrics = await metricsService.getMetrics({
   *   type: 'GROUP_MEETING',
   *   contextId: 'group-id',
   *   startTime: '2026-01-01T00:00:00-05:00',
   *   endTime: '2026-01-31T23:59:59-05:00'
   * });
   * 
   * @example
   * // Para TEMPLE_WORHSIP
   * const metrics = await metricsService.getMetrics({
   *   type: 'TEMPLE_WORHSIP',
   *   contextId: 'church-id',
   *   startTime: '2026-01-01T00:00:00-05:00',
   *   endTime: '2026-01-31T23:59:59-05:00'
   * });
   */
  getMetrics: (params: MetricsQueryParams) => {
    const query = buildMetricsQueryParams(params);
    return apiService.get<BaseMetrics | WorshipMetrics>(`/meetings/metrics?${query}`);
  },
};
