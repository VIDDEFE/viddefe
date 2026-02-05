import { useQuery, keepPreviousData } from '@tanstack/react-query';
import { metricsService, type BaseMetrics, type WorshipMetrics, type MetricsQueryParams } from '../services/metricsService';

/**
 * Hook para obtener métricas de asistencia para reuniones
 * 
 * @param params - Parámetros de consulta (type, contextId, startTime, endTime)
 * @returns Query result con datos de métricas
 * 
 * @example
 * // Para obtener métricas de un grupo
 * const { data: metrics, isLoading } = useMetrics({
 *   type: 'GROUP_MEETING',
 *   contextId: 'group-id',
 *   startTime: '2026-01-01T00:00:00-05:00',
 *   endTime: '2026-01-31T23:59:59-05:00'
 * });
 * 
 * @example
 * // Para obtener métricas de una iglesia (incluye desglose de grupos y iglesia)
 * const { data: metrics, isLoading } = useMetrics({
 *   type: 'TEMPLE_WORHSIP',
 *   contextId: 'church-id',
 *   startTime: '2026-01-01T00:00:00-05:00',
 *   endTime: '2026-01-31T23:59:59-05:00'
 * });
 */
export function useMetrics(params?: MetricsQueryParams) {
  return useQuery<BaseMetrics | WorshipMetrics, Error>({
    queryKey: ['metrics', params?.type, params?.contextId, params?.startTime, params?.endTime],
    queryFn: () => metricsService.getMetrics(params!),
    enabled: !!params?.type && !!params?.startTime && !!params?.endTime,
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook especializado para obtener métricas de GROUP_MEETING
 * 
 * @param groupId - ID del grupo
 * @param startTime - Fecha de inicio (ISO-8601 con timezone)
 * @param endTime - Fecha de fin (ISO-8601 con timezone)
 * @param includeContextId - Si false, no envía contextId (para MyGroup)
 * @returns Query result con datos de BaseMetrics
 * 
 * @example
 * const { data: metrics, isLoading } = useGroupMetrics(
 *   'group-123',
 *   '2026-01-01T00:00:00-05:00',
 *   '2026-01-31T23:59:59-05:00'
 * );
 * 
 * @example
 * // Para MyGroup (sin contextId)
 * const { data: metrics, isLoading } = useGroupMetrics(
 *   groupId,
 *   startTime,
 *   endTime,
 *   false
 * );
 */
export function useGroupMetrics(groupId?: string, startTime?: string, endTime?: string, includeContextId = true) {
  return useQuery<BaseMetrics, Error>({
    queryKey: ['metrics', 'GROUP_MEETING', includeContextId ? groupId : undefined, startTime, endTime],
    queryFn: () =>
      metricsService.getMetrics({
        type: 'GROUP_MEETING',
        contextId: includeContextId ? groupId! : undefined,
        startTime: startTime!,
        endTime: endTime!,
      }) as Promise<BaseMetrics>,
    enabled: !!groupId && !!startTime && !!endTime,
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook especializado para obtener métricas de TEMPLE_WORHSIP
 * Incluye desglose entre métricas de grupos y métricas de iglesia
 * 
 * @param churchId - ID de la iglesia
 * @param startTime - Fecha de inicio (ISO-8601 con timezone)
 * @param endTime - Fecha de fin (ISO-8601 con timezone)
 * @param includeContextId - Si false, no envía contextId (para MyChurch)
 * @returns Query result con datos de WorshipMetrics
 * 
 * @example
 * const { data: metrics, isLoading } = useWorshipMetrics(
 *   'church-123',
 *   '2026-01-01T00:00:00-05:00',
 *   '2026-01-31T23:59:59-05:00'
 * );
 * 
 * @example
 * // Para MyChurch (sin contextId)
 * const { data: metrics, isLoading } = useWorshipMetrics(
 *   churchId,
 *   startTime,
 *   endTime,
 *   false
 * );
 * 
 * // Acceso a las métricas desglosadas:
 * if (metrics) {
 *   console.log(metrics.attendanceRate);        // Tasa general
 *   console.log(metrics.groupMetrics.attendanceRate);  // Tasa de grupos
 *   console.log(metrics.churchMetrics.attendanceRate); // Tasa de iglesia
 * }
 */
export function useWorshipMetrics(churchId?: string, startTime?: string, endTime?: string, includeContextId = true) {
  return useQuery<WorshipMetrics, Error>({
    queryKey: ['metrics', 'TEMPLE_WORHSIP', includeContextId ? churchId : undefined, startTime, endTime],
    queryFn: () =>
      metricsService.getMetrics({
        type: 'TEMPLE_WORHSIP',
        contextId: includeContextId ? churchId! : undefined,
        startTime: startTime!,
        endTime: endTime!,
      }) as Promise<WorshipMetrics>,
    enabled: !!churchId && !!startTime && !!endTime,
    placeholderData: keepPreviousData,
  });
}
