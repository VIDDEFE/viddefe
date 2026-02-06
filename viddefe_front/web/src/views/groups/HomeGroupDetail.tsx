import { useParams, useNavigate } from 'react-router-dom';
import { useHomeGroupDetail, useGroupMetrics, useDateRange } from '../../hooks';
import { Card, Button, PageHeader } from '../../components/shared';
import { DateRangeSelector, MetricsStatsGrid, MetricCard } from '../../components/metrics';
import RoleTree from '../../components/groups/RoleTree';
import { FiArrowLeft, FiMapPin, FiUser, FiGrid } from 'react-icons/fi';

export default function HomeGroupDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data, isLoading, error } = useHomeGroupDetail(id);

  // Hook personalizado para manejo de fechas
  const { dateRange, formattedDates, setStartDate, setEndDate } = useDateRange(30);

  // Obtener métricas del grupo
  const { data: groupMetrics } = useGroupMetrics(
    id,
    formattedDates.startTime,
    formattedDates.endTime
  );

  const handleGoBack = () => {
    navigate('/groups');
  };

  // Estado de carga
  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando detalles del grupo...</p>
          </div>
        </div>
      </div>
    );
  }

  // Estado de error
  if (error || !data) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-red-600 text-2xl">!</span>
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">
              Error al cargar el grupo
            </h2>
            <p className="text-neutral-600 mb-4">
              {error?.message || 'No se pudo encontrar el grupo solicitado'}
            </p>
            <Button variant="secondary" onClick={handleGoBack}>
              <span className="flex items-center gap-2">
                <FiArrowLeft size={16} />
                Volver a Grupos
              </span>
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const { homeGroup, strategy, hierarchy } = data;

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title={homeGroup.name}
        subtitle="Detalle del grupo de hogar"
        actions={
          <div className="flex items-center gap-2">
            <Button variant="secondary" onClick={handleGoBack}>
              <span className="flex items-center gap-2">
                <FiArrowLeft size={16} />
                Volver
              </span>
            </Button>
          </div>
        }
      />

      {/* Date Range Selector */}
      <DateRangeSelector
        startDate={dateRange.start}
        endDate={dateRange.end}
        onStartDateChange={setStartDate}
        onEndDateChange={setEndDate}
      />

      {/* Stats based on Metrics */}
      {groupMetrics && (
        <MetricsStatsGrid
          totalPeople={groupMetrics.totalPeople}
          totalMeetings={groupMetrics.totalMeetings}
          attendanceRate={groupMetrics.attendanceRate}
          averageAttendance={groupMetrics.averageAttendancePerMeeting}
          labels={{
            totalPeople: 'Asistentes',
            totalMeetings: 'Reuniones Este Período',
            attendanceRate: 'Tasa de Asistencia',
          }}
        />
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fadeIn">
        {/* Columna izquierda: Info básica + Estrategia */}
        <div className="space-y-6">
          {/* Información básica */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiGrid className="text-primary-600" />
              Información General
            </h3>

            <div className="space-y-4">
              {/* Nombre */}
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                  Nombre
                </span>
                <p className="text-neutral-800 font-medium mt-1">{homeGroup.name}</p>
              </div>

              {/* Descripción */}
              {homeGroup.description && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                    Descripción
                  </span>
                  <p className="text-neutral-700 mt-1 whitespace-pre-wrap">
                    {homeGroup.description}
                  </p>
                </div>
              )}

              {/* Ubicación */}
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                  Ubicación
                </span>
                <div className="flex items-center gap-2 mt-1">
                  <FiMapPin className="text-neutral-400" size={16} />
                  <span className="text-neutral-700 text-sm font-mono">
                    {homeGroup.latitude.toFixed(6)}, {homeGroup.longitude.toFixed(6)}
                  </span>
                </div>
                <a
                  href={`https://www.google.com/maps?q=${homeGroup.latitude},${homeGroup.longitude}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-primary-600 hover:text-primary-700 text-sm mt-1 inline-block"
                >
                  Ver en Google Maps →
                </a>
              </div>
            </div>
          </Card>

          {/* Líder */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiUser className="text-primary-600" />
              Líder del Grupo
            </h3>

            {homeGroup.manager ? (
              <div className="flex items-center gap-3">
                {homeGroup.manager.avatar ? (
                  <img
                    src={homeGroup.manager.avatar}
                    alt={`${homeGroup.manager.firstName} ${homeGroup.manager.lastName}`}
                    className="w-12 h-12 rounded-full object-cover"
                  />
                ) : (
                  <div className="w-12 h-12 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-semibold text-lg">
                    {homeGroup.manager.firstName?.[0]}
                    {homeGroup.manager.lastName?.[0]}
                  </div>
                )}
                <div>
                  <p className="font-medium text-neutral-800">
                    {homeGroup.manager.firstName} {homeGroup.manager.lastName}
                  </p>
                  {homeGroup.manager.phone && (
                    <p className="text-sm text-neutral-500">{homeGroup.manager.phone}</p>
                  )}
                </div>
              </div>
            ) : (
              <p className="text-neutral-500 text-center py-4">
                Sin responsable asignado
              </p>
            )}
          </Card>

          {/* Estrategia */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4">
              Estrategia
            </h3>

            {strategy ? (
              <div className="inline-flex items-center px-4 py-2 bg-violet-50 border border-violet-200 rounded-lg">
                <span className="w-3 h-3 bg-violet-500 rounded-full mr-3" />
                <span className="font-medium text-violet-800">{strategy.name}</span>
              </div>
            ) : (
              <p className="text-neutral-500 text-center py-4">
                Sin estrategia asignada
              </p>
            )}
          </Card>

          {/* Group Metrics */}
          {groupMetrics && (
            <MetricCard
              title={`Métricas de ${homeGroup.name} (${dateRange.start} a ${dateRange.end})`}
              metrics={[
                {
                  label: "Total Reuniones",
                  value: groupMetrics.totalMeetings
                },
                {
                  label: "Asistentes",
                  value: groupMetrics.totalPeople
                },
                {
                  label: "Asist. Promedio",
                  value: groupMetrics.averageAttendancePerMeeting.toFixed(1)
                },
                {
                  label: "Tasa de Asistencia",
                  value: `${Math.round(groupMetrics.attendanceRate)}%`,
                  colorClass: "text-green-600"
                },
                {
                  label: "Tasa de Inasistencia",
                  value: `${Math.round(groupMetrics.absenceRate)}%`
                }
              ]}
            />
          )}
        </div>

        {/* Columna derecha: Jerarquía de roles (2 columnas) */}
        <div className="lg:col-span-2">
          <Card className="h-full">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-800">
                Estructura de Roles
              </h3>
              {strategy && (
                <p className="text-sm text-neutral-500">
                  Estrategia: <span className="font-medium text-violet-700">{strategy.name}</span>
                </p>
              )}
            </div>

            {/* Info sobre cómo gestionar la estructura */}
            {strategy && (
              <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                <p className="text-sm text-blue-800">
                  Para editar la estructura de roles o asignar personas, ve a{' '}
                  <strong>"Mi Grupo"</strong> si eres parte de este grupo, o{' '}
                  <strong>Gestionar Estrategias</strong> desde la lista de grupos.
                </p>
              </div>
            )}

            <div className="border border-neutral-200 rounded-lg p-4 bg-neutral-50/50 min-h-75">
              <RoleTree
                hierarchy={hierarchy}
                emptyMessage={
                  strategy
                    ? 'Esta estrategia no tiene roles definidos. Configúralos desde "Gestionar Estrategias".'
                    : 'Asigna una estrategia al grupo para poder ver y asignar roles'
                }
                // Vista de solo lectura: sin acciones de gestión de personas
              />
            </div>

            {/* Leyenda */}
            {hierarchy && hierarchy.length > 0 && (
              <div className="mt-4 pt-4 border-t border-neutral-200">
                <p className="text-xs text-neutral-500 mb-2">Leyenda:</p>
                <div className="flex flex-wrap gap-4 text-xs text-neutral-600">
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-primary-500" />
                    <span>Nivel 1</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-violet-500" />
                    <span>Nivel 2</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-blue-500" />
                    <span>Nivel 3</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-emerald-500" />
                    <span>Nivel 4+</span>
                  </div>
                </div>
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
}
