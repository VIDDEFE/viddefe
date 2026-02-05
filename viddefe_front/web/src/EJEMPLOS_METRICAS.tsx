/**
 * EJEMPLO DE USO - Métricas de Asistencia
 * 
 * Este archivo muestra cómo usar el servicio y hooks de métricas
 * de asistencia para consultar datos de reuniones.
 */

import { useMetrics, useGroupMetrics, useWorshipMetrics } from './hooks';

export function MetricsDashboard() {
  // Parámetros de fecha
  const startTime = '2026-01-01T00:00:00-05:00';
  const endTime = '2026-01-31T23:59:59-05:00';
  
  // Opción 1: Usar el hook genérico para GROUP_MEETING
  const { data: groupMetrics, isLoading: groupLoading } = useMetrics({
    type: 'GROUP_MEETING',
    contextId: 'group-123',
    startTime,
    endTime,
  });
  
  if (groupLoading) return <div>Cargando...</div>;
  
  return (
    <div>
      <h2>Métricas del Grupo</h2>
      <p>Asistencia: {groupMetrics?.attendanceRate}%</p>
      <p>Total de reuniones: {groupMetrics?.totalMeetings}</p>
      <p>Promedio por reunión: {groupMetrics?.averageAttendancePerMeeting}</p>
    </div>
  );
}

// ============================================================================
// EJEMPLO 2: Hook especializado para GROUP_MEETING
// ============================================================================

export function GroupMetricsPanel({ groupId }: { groupId: string }) {
  const startTime = '2026-01-01T00:00:00-05:00';
  const endTime = '2026-01-31T23:59:59-05:00';
  
  // Usar el hook especializado (más type-safe)
  const { data: metrics, isLoading, error } = useGroupMetrics(groupId, startTime, endTime);
  
  if (isLoading) return <div>Cargando métricas...</div>;
  if (error) return <div>Error: {error.message}</div>;
  if (!metrics) return <div>Sin datos</div>;
  
  return (
    <div className="bg-white p-6 rounded-lg shadow">
      <h3>Estadísticas de Asistencia - Grupo</h3>
      
      <div className="grid grid-cols-2 gap-4">
        <div>
          <p className="text-sm text-gray-600">Tasa de Asistencia</p>
          <p className="text-2xl font-bold">{metrics.attendanceRate.toFixed(1)}%</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-600">Total de Personas</p>
          <p className="text-2xl font-bold">{metrics.totalPeople}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-600">Reuniones Realizadas</p>
          <p className="text-2xl font-bold">{metrics.totalMeetings}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-600">Promedio por Reunión</p>
          <p className="text-2xl font-bold">{metrics.averageAttendancePerMeeting.toFixed(1)}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-600">Nuevos Asistentes</p>
          <p className="text-2xl font-bold">{metrics.newAttendees}</p>
        </div>
        
        <div>
          <p className="text-sm text-gray-600">Tasa de Ausencia</p>
          <p className="text-2xl font-bold">{metrics.absenceRate.toFixed(1)}%</p>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// EJEMPLO 3: Hook especializado para TEMPLE_WORHSIP (con desglose)
// ============================================================================

export function WorshipMetricsPanel({ churchId }: { churchId: string }) {
  const startTime = '2026-01-01T00:00:00-05:00';
  const endTime = '2026-01-31T23:59:59-05:00';
  
  // Usar el hook especializado para worship (tipo WorshipMetrics)
  const { data: metrics, isLoading, error } = useWorshipMetrics(churchId, startTime, endTime);
  
  if (isLoading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error.message}</div>;
  if (!metrics) return <div>Sin datos</div>;
  
  return (
    <div className="space-y-6">
      {/* Métricas Generales */}
      <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
        <h3 className="text-lg font-bold mb-4">Métricas Generales - Iglesia</h3>
        
        <div className="grid grid-cols-3 gap-4">
          <div>
            <p className="text-sm text-gray-600">Asistencia General</p>
            <p className="text-2xl font-bold">{metrics.attendanceRate.toFixed(1)}%</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Total Grupos</p>
            <p className="text-2xl font-bold">{metrics.totalGroups}</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Total Reuniones</p>
            <p className="text-2xl font-bold">{metrics.totalMeetings}</p>
          </div>
        </div>
      </div>
      
      {/* Desglose: Métricas de Grupos */}
      <div className="bg-green-50 p-6 rounded-lg border border-green-200">
        <h4 className="font-bold mb-4">Estadísticas - Grupos</h4>
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-600">Asistencia en Grupos</p>
            <p className="text-xl font-bold">{metrics.groupMetrics.attendanceRate.toFixed(1)}%</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Reuniones de Grupo</p>
            <p className="text-xl font-bold">{metrics.groupMetrics.totalMeetings}</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Total en Grupos</p>
            <p className="text-xl font-bold">{metrics.groupMetrics.totalPeople}</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Promedio/Reunión</p>
            <p className="text-xl font-bold">{metrics.groupMetrics.averageAttendancePerMeeting.toFixed(1)}</p>
          </div>
        </div>
      </div>
      
      {/* Desglose: Métricas de Iglesia */}
      <div className="bg-purple-50 p-6 rounded-lg border border-purple-200">
        <h4 className="font-bold mb-4">Estadísticas - Iglesia (Cultos)</h4>
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-600">Asistencia en Cultos</p>
            <p className="text-xl font-bold">{metrics.churchMetrics.attendanceRate.toFixed(1)}%</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Cultos Realizados</p>
            <p className="text-xl font-bold">{metrics.churchMetrics.totalMeetings}</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Total en Cultos</p>
            <p className="text-xl font-bold">{metrics.churchMetrics.totalPeople}</p>
          </div>
          
          <div>
            <p className="text-sm text-gray-600">Promedio/Culto</p>
            <p className="text-xl font-bold">{metrics.churchMetrics.averageAttendancePerMeeting.toFixed(1)}</p>
          </div>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// EJEMPLO 4: Uso directo del servicio (más bajo nivel)
// ============================================================================

import { metricsService } from './services';

export async function fetchMetricsDirectly() {
  try {
    // Para GROUP_MEETING
    const groupMetrics = await metricsService.getMetrics({
      type: 'GROUP_MEETING',
      contextId: 'group-123',
      startTime: '2026-01-01T00:00:00-05:00',
      endTime: '2026-01-31T23:59:59-05:00',
    });
    
    console.log('Métricas del grupo:', groupMetrics);
    
    // Para TEMPLE_WORHSIP
    const worshipMetrics = await metricsService.getMetrics({
      type: 'TEMPLE_WORHSIP',
      contextId: 'church-123',
      startTime: '2026-01-01T00:00:00-05:00',
      endTime: '2026-01-31T23:59:59-05:00',
    });
    
    console.log('Métricas de iglesia:', worshipMetrics);
  } catch (error) {
    console.error('Error al obtener métricas:', error);
  }
}
