import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { Card, Button, PageHeader } from "../../components/shared";
import { StatCard, DateRangeSelector, MetricsDetailsGrid } from "../../components/metrics";
import { UpcomingEventsWidget, GroupsOverviewWidget, RecentWorshipsWidget } from "../../components/dashboard";
import { FiBarChart2, FiUsers, FiCalendar, FiHome } from "react-icons/fi";
import { MdChurch } from "react-icons/md";
import { useWorshipMetrics, useDateRange, useHomeGroups, useMyChurch, useChurchChildren } from "../../hooks";
import type { PageableRequest } from "../../services";

export default function Dashboard() {
  const navigate = useNavigate();

  // Obtener iglesias, grupos y métricas
  const { data: churchesData } = useMyChurch();
  const params:PageableRequest = {
    page: 0,
    size: 0,
  }
  const {data: churchChildrenData} = useChurchChildren(churchesData?.id,params); // Pre-cargar iglesias hijas para evitar latencia en navegación
  const { data: groupsData } = useHomeGroups({ page: 0, size: 100 });

  // Hook personalizado para manejo de fechas
  const { dateRange, formattedDates, setStartDate, setEndDate } = useDateRange(30);

  // Obtener iglesia actual (asumiendo que la primera es la principal)
  const mainChurchId = churchesData?.id;
  const { data: worshipMetrics } = useWorshipMetrics(
    mainChurchId,
    formattedDates.startTime,
    formattedDates.endTime
  );

  // Calcular estadísticas
  const stats = useMemo(() => [
    {
      title: "Total Iglesias",
      value: churchChildrenData?.totalElements || 0,
      icon: <MdChurch size={28} />,
      bgColor: "bg-blue-500"
    },
    {
      title: "Total Grupos",
      value: groupsData?.totalElements || 0,
      icon: <FiUsers size={28} />,
      bgColor: "bg-violet-500"
    },
    {
      title: "Personas Activas",
      value: worshipMetrics?.totalPeople || 0,
      icon: <FiUsers size={28} />,
      bgColor: "bg-green-500"
    },
    {
      title: "Cultos Este Período",
      value: worshipMetrics?.totalMeetings || 0,
      icon: <FiCalendar size={28} />,
      bgColor: "bg-red-500"
    },
    {
      title: "Tasa de Asistencia",
      value: `${Math.round(worshipMetrics?.attendanceRate || 0)}%`,
      icon: <FiBarChart2 size={28} />,
      bgColor: "bg-yellow-500"
    },
  ], [churchesData, groupsData, worshipMetrics]);

  return (
    <div className="flex flex-col gap-8">
      <PageHeader
        title="Dashboard"
        subtitle="Bienvenido al sistema de gestión de iglesias"
      />

      {/* Date Range Selector */}
      <DateRangeSelector
        startDate={dateRange.start}
        endDate={dateRange.end}
        onStartDateChange={setStartDate}
        onEndDateChange={setEndDate}
      />

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-5 gap-6">
        {stats.map((stat, idx) => (
          <StatCard
            key={idx}
            icon={stat.icon}
            title={stat.title}
            value={stat.value}
            bgColor={stat.bgColor}
          />
        ))}
      </div>

      {/* Sections - Overview Widgets */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Grupos Activos */}
        <GroupsOverviewWidget />

        {/* Próximos Eventos */}
        <UpcomingEventsWidget />

        {/* Cultos Recientes */}
        <RecentWorshipsWidget />
      </div>

      {/* Sections - Acciones */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Quick actions */}
        <Card className="p-5 sm:p-6 flex flex-col gap-4">
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Acciones Rápidas
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Button
              variant="primary"
              onClick={() => navigate('/churches')}
            >
              <span className="flex items-center gap-2">
                <MdChurch size={16} />
                Nueva Iglesia
              </span>
            </Button>
            <Button
              variant="primary"
              onClick={() => navigate('/people')}
            >
              <span className="flex items-center gap-2">
                <FiUsers size={16} />
                Nueva Persona
              </span>
            </Button>
            <Button
              variant="primary"
              onClick={() => navigate('/worships')}
            >
              <span className="flex items-center gap-2">
                <FiCalendar size={16} />
                Nuevo Culto
              </span>
            </Button>
            <Button
              variant="primary"
              onClick={() => navigate('/groups')}
            >
              <span className="flex items-center gap-2">
                <FiHome size={16} />
                Nuevo Grupo
              </span>
            </Button>
          </div>
        </Card>

        {/* Quick links */}
        <Card className="p-5 sm:p-6 flex flex-col gap-4">
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Accesos Rápidos
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/churches')}
            >
              Ver Iglesias
            </Button>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/groups')}
            >
              Ver Grupos
            </Button>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/people')}
            >
              Ver Personas
            </Button>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/worships')}
            >
              Ver Cultos
            </Button>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/events')}
            >
              Ver Eventos
            </Button>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/mychurch')}
            >
              Mi Iglesia
            </Button>
          </div>
        </Card>
      </div>

      {/* Detailed Metrics Section */}
      {worshipMetrics && (
        <MetricsDetailsGrid
          metrics={worshipMetrics}
          entityName="Dashboard General"
          dateRange={dateRange}
          showGeneralMetrics={true}
        />
      )}
    </div>
  );
}
