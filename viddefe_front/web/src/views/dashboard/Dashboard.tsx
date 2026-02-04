import { useMemo } from "react";
import { Card, Button, PageHeader } from "../../components/shared";
import { FiBarChart2, FiUsers, FiCalendar } from "react-icons/fi";
import { MdChurch } from "react-icons/md";
import { useChurches, useWorshipMetrics } from "../../hooks";

interface StatCard {
  title: string;
  value: number;
  icon: string;
  bgColor: string;
}

export default function Dashboard() {
  // Obtener iglesias y métricas
  const { data: churchesData } = useChurches({ page: 0, size: 100 });
  
  // Calcular fechas para últimos 30 días
  const now = new Date();
  const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
  
  // Formatear fechas en ISO con timezone
  const formatDateWithTz = (date: Date): string => {
    const offset = -date.getTimezoneOffset();
    const offsetHours = String(Math.floor(Math.abs(offset) / 60)).padStart(2, '0');
    const offsetMinutes = String(Math.abs(offset) % 60).padStart(2, '0');
    const sign = offset >= 0 ? '+' : '-';
    const isoString = date.toISOString().split('T')[0];
    return `${isoString}T00:00:00${sign}${offsetHours}:${offsetMinutes}`;
  };
  
  const startTime = formatDateWithTz(thirtyDaysAgo);
  const endTime = formatDateWithTz(now);
  
  // Obtener iglesia actual (asumiendo que la primera es la principal)
  const mainChurchId = churchesData?.content?.[0]?.id;
  const { data: worshipMetrics } = useWorshipMetrics(mainChurchId, startTime, endTime);

  // Calcular estadísticas
  const stats = useMemo((): StatCard[] => [
    { 
      title: "Total Iglesias", 
      value: churchesData?.totalElements || 0, 
      icon: "church", 
      bgColor: "bg-blue-500" 
    },
    { 
      title: "Personas Activas", 
      value: worshipMetrics?.totalPeople || 0, 
      icon: "people", 
      bgColor: "bg-green-500" 
    },
    { 
      title: "Cultos Este Mes", 
      value: worshipMetrics?.totalMeetings || 0, 
      icon: "services", 
      bgColor: "bg-red-500" 
    },
    { 
      title: "Tasa de Asistencia", 
      value: Math.round(worshipMetrics?.attendanceRate || 0), 
      icon: "groups", 
      bgColor: "bg-yellow-500" 
    },
  ], [churchesData, worshipMetrics]);

  return (
    <div className="flex flex-col gap-8">
      <PageHeader
        title="Dashboard"
        subtitle="Bienvenido al sistema de gestión de iglesias"
      />

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6">
        {stats.map((stat, idx) => (
          <Card
            key={idx}
            className="flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-primary-100"
          >
            <div className={`text-2xl sm:text-3xl p-3 rounded-lg text-white ${stat.bgColor}`}>
              {stat.icon === 'church' && <MdChurch size={28} className="text-white" />}
              {stat.icon === 'people' && <FiUsers size={28} className="text-white" />}
              {stat.icon === 'services' && <FiCalendar size={28} className="text-white" />}
              {stat.icon === 'groups' && <FiUsers size={28} className="text-white" />}
              {stat.icon === 'dashboard' && <FiBarChart2 size={28} className="text-white" />}
            </div>

            <h3 className="text-base sm:text-lg font-semibold text-primary-900">
              {stat.title}
            </h3>

            <p className="text-2xl sm:text-3xl font-bold text-primary-800">
              {stat.value}
            </p>
          </Card>
        ))}
      </div>

      {/* Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Quick actions */}
        <Card className="p-5 sm:p-6 flex flex-col gap-4">
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Acciones Rápidas
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Button variant="primary">Nueva Iglesia</Button>
            <Button variant="primary">Nueva Persona</Button>
            <Button variant="primary">Nuevo Servicio</Button>
            <Button variant="primary">Nuevo Grupo</Button>
          </div>
        </Card>

        {/* Recent activity */}
        <Card className="p-5 sm:p-6 flex flex-col gap-4">
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Actividad Reciente
          </h2>

          <ul className="list-disc pl-5 space-y-2 text-primary-800 text-sm sm:text-base">
            <li>Se agregó a Juan Pérez como miembro</li>
            <li>Nuevo servicio programado para el domingo</li>
            <li>Grupo de oración creado</li>
            <li>Evento especial registrado</li>
          </ul>
        </Card>
      </div>

      {/* Detailed Metrics Section */}
      {worshipMetrics && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* General Metrics */}
          <Card className="p-5 sm:p-6">
            <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
              Métricas Generales
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
                  {worshipMetrics.averageAttendancePerMeeting.toFixed(1)}
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
                {worshipMetrics.totalPeople} personas | {worshipMetrics.totalMeetings} reuniones
              </p>
            </div>
          </Card>

          {/* Group Metrics */}
          {worshipMetrics.groupMetrics && (
            <Card className="p-5 sm:p-6">
              <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
                Métricas de Grupos
              </h3>
              <div className="space-y-4">
                <div className="flex justify-between items-center border-b border-primary-100 pb-3">
                  <span className="text-sm sm:text-base text-primary-700">Total de Grupos</span>
                  <span className="text-lg sm:text-xl font-bold text-primary-800">
                    {worshipMetrics.totalGroups}
                  </span>
                </div>
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
                <div className="flex justify-between items-center">
                  <span className="text-sm sm:text-base text-primary-700">Asistencia Grupos</span>
                  <span className="text-lg sm:text-xl font-bold text-primary-800">
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
                Métricas de Cultos
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
                  <span className="text-sm sm:text-base text-primary-700">Nuevos Asistentes</span>
                  <span className="text-lg sm:text-xl font-bold text-primary-800">
                    {worshipMetrics.churchMetrics.newAttendees}
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm sm:text-base text-primary-700">Asistencia Cultos</span>
                  <span className="text-lg sm:text-xl font-bold text-primary-800">
                    {Math.round(worshipMetrics.churchMetrics.attendanceRate)}%
                  </span>
                </div>
              </div>
            </Card>
          )}
        </div>
      )}
    </div>
  );
}
