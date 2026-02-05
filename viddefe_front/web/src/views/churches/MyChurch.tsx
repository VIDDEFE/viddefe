import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMyChurch, useWorshipMetrics } from '../../hooks';
import { Button, PageHeader, Card } from '../../components/shared';
import ChurchDetailLayout from '../../components/churches/ChurchDetailLayout';
import { FiUsers, FiHome, FiCalendar } from 'react-icons/fi';
import { MdChurch } from 'react-icons/md';

export default function MyChurch() {
  const navigate = useNavigate();
  const { data: myChurch, isLoading, error } = useMyChurch();

  // State para las fechas
  const [dateRange, setDateRange] = useState<{ start: string; end: string }>(() => {
    const now = new Date();
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    
    // Formatear fechas para input type="date"
    const formatDateForInput = (date: Date): string => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };
    
    return {
      start: formatDateForInput(thirtyDaysAgo),
      end: formatDateForInput(now),
    };
  });

  // Formatear fechas en ISO con timezone para API
  const formatDateWithTz = (dateStr: string): string => {
    const date = new Date(dateStr);
    const offset = -date.getTimezoneOffset();
    const offsetHours = String(Math.floor(Math.abs(offset) / 60)).padStart(2, '0');
    const offsetMinutes = String(Math.abs(offset) % 60).padStart(2, '0');
    const sign = offset >= 0 ? '+' : '-';
    return `${dateStr}T00:00:00${sign}${offsetHours}:${offsetMinutes}`;
  };
  
  const startTime = formatDateWithTz(dateRange.start);
  const endTime = formatDateWithTz(dateRange.end);
  
  // Obtener métricas de cultos de mi iglesia (sin contextId, es mi iglesia)
  const { data: worshipMetrics } = useWorshipMetrics(myChurch?.id, startTime, endTime, false);

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando tu iglesia...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !myChurch) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FiHome className="text-amber-600 text-2xl" />
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">
              No se encontró tu iglesia
            </h2>
            <p className="text-neutral-600 mb-4">
              {error?.message || 'No se pudo cargar la información de tu iglesia'}
            </p>
            <Button variant="secondary" onClick={() => navigate('/churches')}>
              Ver todas las iglesias
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Acciones rápidas habilitadas SOLO para "Mi Iglesia"
  const quickActions = [
    { icon: <FiUsers size={16} />, label: 'Administrar Miembros', onClick: () => navigate('/people'), disabled: false },
    { icon: <FiHome size={16} />, label: 'Ver Grupos', onClick: () => navigate('/groups'), disabled: false },
    { icon: <FiCalendar size={16} />, label: 'Ver Servicios', onClick: () => navigate('/services'), disabled: false },
    { icon: <FiCalendar size={16} />, label: 'Ver Eventos', onClick: () => navigate('/events'), disabled: false },
  ];

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title="Mi Iglesia"
        subtitle={`${myChurch.city?.name || 'Ciudad no especificada'}, ${myChurch.states?.name || ''}`}
        actions={
          <Button variant="secondary" onClick={() => navigate('/churches')}>
            Ver todas las iglesias
          </Button>
        }
      />

      {/* Date Range Selector */}
      <Card className="p-5 sm:p-6 mb-6">
        <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
          <FiCalendar size={20} />
          Rango de Fechas para Métricas
        </h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-neutral-700 mb-2">
              Fecha Inicio
            </label>
            <input
              type="date"
              value={dateRange.start}
              onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
              className="w-full px-4 py-2 border border-neutral-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-neutral-700 mb-2">
              Fecha Fin
            </label>
            <input
              type="date"
              value={dateRange.end}
              onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
              className="w-full px-4 py-2 border border-neutral-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
        </div>
      </Card>

      {/* 
        showQuickActions=true → acciones rápidas VISIBLES y habilitadas 
        en la vista "Mi Iglesia" 
      */}
      <ChurchDetailLayout 
        church={myChurch} 
        showQuickActions={true}
        quickActions={quickActions}
      >
        {/* Metrics Section for My Church */}
        {worshipMetrics && (
          <>
            {/* Stats based on Metrics */}
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6 mb-6">
              <Card className="flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-neutral-100">
                <div className="text-2xl sm:text-3xl p-3 rounded-lg text-white bg-green-500">
                  <FiUsers size={28} />
                </div>
                <h3 className="text-base sm:text-lg font-semibold text-neutral-900">Total Miembros</h3>
                <p className="text-2xl sm:text-3xl font-bold text-neutral-800">{worshipMetrics.totalPeople}</p>
              </Card>

              <Card className="flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-neutral-100">
                <div className="text-2xl sm:text-3xl p-3 rounded-lg text-white bg-violet-500">
                  <FiHome size={28} />
                </div>
                <h3 className="text-base sm:text-lg font-semibold text-neutral-900">Grupos Activos</h3>
                <p className="text-2xl sm:text-3xl font-bold text-neutral-800">{worshipMetrics.totalGroups}</p>
              </Card>

              <Card className="flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-neutral-100">
                <div className="text-2xl sm:text-3xl p-3 rounded-lg text-white bg-blue-500">
                  <FiCalendar size={28} />
                </div>
                <h3 className="text-base sm:text-lg font-semibold text-neutral-900">Cultos Este Período</h3>
                <p className="text-2xl sm:text-3xl font-bold text-neutral-800">{worshipMetrics.totalMeetings}</p>
              </Card>

              <Card className="flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-neutral-100">
                <div className="text-2xl sm:text-3xl p-3 rounded-lg text-white bg-yellow-500">
                  <MdChurch size={28} />
                </div>
                <h3 className="text-base sm:text-lg font-semibold text-neutral-900">Tasa de Asistencia</h3>
                <p className="text-2xl sm:text-3xl font-bold text-neutral-800">{Math.round(worshipMetrics.attendanceRate)}%</p>
              </Card>
            </div>

            {/* Detailed Metrics Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Church Metrics */}
              <Card className="p-5 sm:p-6">
                <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
                  Métricas de {myChurch.name}
                </h3>
                <div className="space-y-4">
                  <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                    <span className="text-sm sm:text-base text-primary-700">Nuevos Asistentes</span>
                    <span className="text-lg sm:text-xl font-bold text-primary-800">
                      {worshipMetrics.newAttendees}
                    </span>
                  </div>
                  <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                    <span className="text-sm sm:text-base text-primary-700">Total Asistentes</span>
                    <span className="text-lg sm:text-xl font-bold text-primary-800">
                      {worshipMetrics.totalPeopleAttended}
                    </span>
                  </div>
                  <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                    <span className="text-sm sm:text-base text-primary-700">Asistencia Promedio</span>
                    <span className="text-lg sm:text-xl font-bold text-primary-800">
                      {(worshipMetrics.averageAttendancePerMeeting || 0).toFixed(1)}
                    </span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-sm sm:text-base text-primary-700">Tasa de Inasistencia</span>
                    <span className="text-lg sm:text-xl font-bold text-primary-800">
                      {Math.round(worshipMetrics.absenceRate)}%
                    </span>
                  </div>
                </div>
              </Card>

              {/* Attendance Rate Visualization */}
              <Card className="p-5 sm:p-6">
                <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
                  Tasa de Asistencia
                </h3>
                <div className="flex flex-col items-center gap-4">
                  <div className="relative w-24 h-24">
                    <div className="w-24 h-24 rounded-full bg-gray-100 flex items-center justify-center">
                      <div className="text-center">
                        <p className="text-2xl font-bold text-primary-800">
                          {Math.round(worshipMetrics.attendanceRate)}%
                        </p>
                      </div>
                    </div>
                  </div>
                  <div className="w-full">
                    <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-green-500 transition-all"
                        style={{ width: `${worshipMetrics.attendanceRate}%` }}
                      ></div>
                    </div>
                  </div>
                  <p className="text-sm text-primary-600 text-center">
                    {worshipMetrics.totalPeople} personas | {worshipMetrics.totalMeetings} reuniones totales
                  </p>
                </div>
              </Card>

              {/* Group Metrics */}
              {worshipMetrics.groupMetrics && (
                <Card className="p-5 sm:p-6">
                  <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
                    Métricas de Grupos ({dateRange.start} a {dateRange.end})
                  </h3>
                  <div className="space-y-4">
                    <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                      <span className="text-sm sm:text-base text-primary-700">Personas en Grupos</span>
                      <span className="text-lg sm:text-xl font-bold text-primary-800">
                        {worshipMetrics.groupMetrics.totalPeople}
                      </span>
                    </div>
                    <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                      <span className="text-sm sm:text-base text-primary-700">Reuniones de Grupos</span>
                      <span className="text-lg sm:text-xl font-bold text-primary-800">
                        {worshipMetrics.groupMetrics.totalMeetings}
                      </span>
                    </div>
                    <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                      <span className="text-sm sm:text-base text-primary-700">Asist. Promedio Grupos</span>
                      <span className="text-lg sm:text-xl font-bold text-primary-800">
                        {(worshipMetrics.groupMetrics.averageAttendancePerMeeting || 0).toFixed(1)}
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm sm:text-base text-primary-700">Asistencia Grupos</span>
                      <span className="text-lg sm:text-xl font-bold text-green-600">
                        {Math.round(worshipMetrics.groupMetrics.attendanceRate)}%
                      </span>
                    </div>
                  </div>
                </Card>
              )}

              {/* Church Metrics */}
              {worshipMetrics.churchMetrics && (
                <Card className="p-5 sm:p-6">
                  <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
                    Métricas de Cultos ({dateRange.start} a {dateRange.end})
                  </h3>
                  <div className="space-y-4">
                    <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                      <span className="text-sm sm:text-base text-primary-700">Personas en Cultos</span>
                      <span className="text-lg sm:text-xl font-bold text-primary-800">
                        {worshipMetrics.churchMetrics.totalPeople}
                      </span>
                    </div>
                    <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                      <span className="text-sm sm:text-base text-primary-700">Total de Cultos</span>
                      <span className="text-lg sm:text-xl font-bold text-primary-800">
                        {worshipMetrics.churchMetrics.totalMeetings}
                      </span>
                    </div>
                    <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                      <span className="text-sm sm:text-base text-primary-700">Nuevos Asistentes Cultos</span>
                      <span className="text-lg sm:text-xl font-bold text-primary-800">
                        {worshipMetrics.churchMetrics.newAttendees}
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm sm:text-base text-primary-700">Asistencia Cultos</span>
                      <span className="text-lg sm:text-xl font-bold text-green-600">
                        {Math.round(worshipMetrics.churchMetrics.attendanceRate)}%
                      </span>
                    </div>
                  </div>
                </Card>
              )}
            </div>
          </>
        )}
      </ChurchDetailLayout>
    </div>
  );
}
