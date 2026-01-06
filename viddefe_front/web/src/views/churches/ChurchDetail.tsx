import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, PageHeader } from '../../components/shared';
import { useChurch } from '../../hooks';
import { FiArrowLeft, FiUsers, FiCalendar, FiMapPin, FiPhone, FiMail, FiUser, FiHome } from 'react-icons/fi';
import { MdChurch } from 'react-icons/md';

interface StatCardProps {
  readonly title: string;
  readonly value: number | string;
  readonly icon: React.ReactNode;
  readonly bgColor: string;
}

function StatCard({ title, value, icon, bgColor }: StatCardProps) {
  return (
    <Card className="flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-neutral-100">
      <div className={`text-2xl sm:text-3xl p-3 rounded-lg text-white ${bgColor}`}>
        {icon}
      </div>
      <h3 className="text-base sm:text-lg font-semibold text-neutral-900">{title}</h3>
      <p className="text-2xl sm:text-3xl font-bold text-neutral-800">{value}</p>
    </Card>
  );
}

export default function ChurchDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const { data: church, isLoading, error } = useChurch(id);

  // Stats mockup (esto se puede conectar a endpoints reales más adelante)
  const [stats] = useState([
    { title: 'Total Miembros', value: church?.memberCount ?? 0, icon: <FiUsers size={28} />, bgColor: 'bg-green-500' },
    { title: 'Grupos Activos', value: 0, icon: <FiHome size={28} />, bgColor: 'bg-violet-500' },
    { title: 'Servicios Este Mes', value: 0, icon: <FiCalendar size={28} />, bgColor: 'bg-blue-500' },
    { title: 'Eventos Próximos', value: 0, icon: <FiCalendar size={28} />, bgColor: 'bg-amber-500' },
  ]);

  const handleGoBack = () => navigate('/churches');

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando información de la iglesia...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !church) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-red-600 text-2xl">!</span>
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">Error al cargar iglesia</h2>
            <p className="text-neutral-600 mb-4">{(error as Error)?.message || 'No se pudo encontrar la iglesia solicitada'}</p>
            <Button variant="secondary" onClick={handleGoBack}>
              <span className="flex items-center gap-2"><FiArrowLeft size={16}/>Volver</span>
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const getPastorName = (): string => {
    if (church.pastor && typeof church.pastor === 'object') {
      return `${church.pastor.firstName} ${church.pastor.lastName}`;
    }
    return 'Sin pastor asignado';
  };

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title={church.name}
        subtitle={`${church.city?.name || 'Ciudad no especificada'}, ${church.states?.name || ''}`}
        actions={
          <Button variant="secondary" onClick={handleGoBack}>
            <span className="flex items-center gap-2"><FiArrowLeft size={16}/>Volver a Iglesias</span>
          </Button>
        }
      />

      <div className="space-y-6 animate-fadeIn">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6">
          {stats.map((stat, idx) => (
            <StatCard
              key={idx}
              title={stat.title}
              value={stat.value}
              icon={stat.icon}
              bgColor={stat.bgColor}
            />
          ))}
        </div>

        {/* Información detallada */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Info básica */}
          <Card className="p-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2 bg-primary-100 rounded-lg">
                <MdChurch className="text-primary-600" size={24} />
              </div>
              <h3 className="text-lg font-semibold text-neutral-800">Información General</h3>
            </div>
            
            <div className="space-y-4">
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">Nombre</span>
                <p className="text-neutral-800 font-medium mt-1">{church.name}</p>
              </div>
              
              {church.foundationDate && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">Fecha de Fundación</span>
                  <p className="text-neutral-800 mt-1">
                    {new Date(church.foundationDate).toLocaleDateString('es-ES', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric'
                    })}
                  </p>
                </div>
              )}
            </div>
          </Card>

          {/* Pastor */}
          <Card className="p-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2 bg-green-100 rounded-lg">
                <FiUser className="text-green-600" size={24} />
              </div>
              <h3 className="text-lg font-semibold text-neutral-800">Pastor</h3>
            </div>
            
            <div className="flex items-center gap-3">
              {church.pastor ? (
                <>
                  <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center text-primary-700 font-semibold text-lg">
                    {church.pastor.firstName?.[0]}{church.pastor.lastName?.[0]}
                  </div>
                  <div>
                    <p className="font-medium text-neutral-800">{getPastorName()}</p>
                    {church.pastor.phone && (
                      <p className="text-sm text-neutral-500">{church.pastor.phone}</p>
                    )}
                  </div>
                </>
              ) : (
                <p className="text-neutral-500">Sin pastor asignado</p>
              )}
            </div>
          </Card>

          {/* Contacto */}
          <Card className="p-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2 bg-blue-100 rounded-lg">
                <FiPhone className="text-blue-600" size={24} />
              </div>
              <h3 className="text-lg font-semibold text-neutral-800">Contacto</h3>
            </div>
            
            <div className="space-y-3">
              {church.phone && (
                <div className="flex items-center gap-3">
                  <FiPhone className="text-neutral-400" size={16} />
                  <span className="text-neutral-700">{church.phone}</span>
                </div>
              )}
              {church.email && (
                <div className="flex items-center gap-3">
                  <FiMail className="text-neutral-400" size={16} />
                  <span className="text-neutral-700">{church.email}</span>
                </div>
              )}
              {!church.phone && !church.email && (
                <p className="text-neutral-500">Sin información de contacto</p>
              )}
            </div>
          </Card>
        </div>

        {/* Ubicación */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-4">
            <div className="p-2 bg-amber-100 rounded-lg">
              <FiMapPin className="text-amber-600" size={24} />
            </div>
            <h3 className="text-lg font-semibold text-neutral-800">Ubicación</h3>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-3">
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">Departamento</span>
                <p className="text-neutral-800 mt-1">{church.states?.name || 'No especificado'}</p>
              </div>
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">Ciudad</span>
                <p className="text-neutral-800 mt-1">{church.city?.name || 'No especificada'}</p>
              </div>
              {church.latitude && church.longitude && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">Coordenadas</span>
                  <p className="text-neutral-600 mt-1 font-mono text-sm">
                    {church.latitude.toFixed(6)}, {church.longitude.toFixed(6)}
                  </p>
                  <a
                    href={`https://www.google.com/maps?q=${church.latitude},${church.longitude}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-primary-600 hover:text-primary-700 text-sm mt-2 inline-flex items-center gap-1"
                  >
                    <FiMapPin size={14} />
                    Ver en Google Maps
                  </a>
                </div>
              )}
            </div>
            
            {/* Mini mapa placeholder o info adicional */}
            <div className="bg-neutral-50 rounded-lg p-4 flex items-center justify-center min-h-40">
              {church.latitude && church.longitude ? (
                <div className="text-center">
                  <div className="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-3">
                    <MdChurch className="text-primary-600" size={32} />
                  </div>
                  <p className="text-sm text-neutral-600">
                    Ubicación registrada en el sistema
                  </p>
                </div>
              ) : (
                <div className="text-center text-neutral-500">
                  <FiMapPin size={32} className="mx-auto mb-2 opacity-50" />
                  <p className="text-sm">Sin coordenadas registradas</p>
                </div>
              )}
            </div>
          </div>
        </Card>

        {/* Acciones rápidas */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold text-neutral-800 mb-4">Acciones Rápidas</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Button variant="secondary" disabled>
              <span className="flex items-center gap-2">
                <FiUsers size={16} />
                Ver Miembros
              </span>
            </Button>
            <Button variant="secondary" disabled>
              <span className="flex items-center gap-2">
                <FiHome size={16} />
                Ver Grupos
              </span>
            </Button>
            <Button variant="secondary" disabled>
              <span className="flex items-center gap-2">
                <FiCalendar size={16} />
                Ver Servicios
              </span>
            </Button>
            <Button variant="secondary" disabled>
              <span className="flex items-center gap-2">
                <FiCalendar size={16} />
                Ver Eventos
              </span>
            </Button>
          </div>
          <p className="text-xs text-neutral-500 mt-3">
            * Funcionalidades próximamente disponibles
          </p>
        </Card>
      </div>
    </div>
  );
}
