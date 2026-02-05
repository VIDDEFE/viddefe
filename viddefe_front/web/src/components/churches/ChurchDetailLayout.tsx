import { Card, Button } from '../shared';
import { FiMapPin, FiPhone, FiMail, FiUser, FiCalendar, FiUsers, FiHome } from 'react-icons/fi';
import { MdChurch } from 'react-icons/md';
import { formatDateForDisplay } from '../../utils/helpers';
import type { ChurchDetail, ChurchPastor } from '../../models';

interface QuickAction {
  icon: React.ReactNode;
  label: string;
  onClick: () => void;
  disabled?: boolean;
}

interface ChurchDetailLayoutProps {
  readonly church: ChurchDetail;
  /** Si es true, muestra las acciones rápidas habilitadas para navegación */
  readonly showQuickActions?: boolean;
  /** Acciones personalizadas para el modo "Mi Iglesia" */
  readonly quickActions?: QuickAction[];
  /** Contenido adicional a renderizar después de la sección de ubicación */
  readonly children?: React.ReactNode;
}

export default function ChurchDetailLayout({
  church,
  showQuickActions = false,
  quickActions,
  children,
}: ChurchDetailLayoutProps) {
  const getPastorName = (pastor: ChurchPastor | null | undefined): string => {
    if (pastor && typeof pastor === 'object') {
      return `${pastor.firstName} ${pastor.lastName}`;
    }
    return 'Sin pastor asignado';
  };

  // Acciones por defecto (deshabilitadas para vistas de solo lectura)
  const defaultQuickActions: QuickAction[] = [
    { icon: <FiUsers size={16} />, label: 'Ver Miembros', onClick: () => {}, disabled: true },
    { icon: <FiHome size={16} />, label: 'Ver Grupos', onClick: () => {}, disabled: true },
    { icon: <FiCalendar size={16} />, label: 'Ver Servicios', onClick: () => {}, disabled: true },
    { icon: <FiCalendar size={16} />, label: 'Ver Eventos', onClick: () => {}, disabled: true },
  ];

  const actionsToRender = quickActions ?? defaultQuickActions;

  return (
    <div className="space-y-6 animate-fadeIn">

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
                  {formatDateForDisplay(church.foundationDate, 'date')}
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
                  <p className="font-medium text-neutral-800">{getPastorName(church.pastor)}</p>
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
                <a href={`mailto:${church.email}`} className="text-primary-600 hover:text-primary-700">
                  {church.email}
                </a>
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
            {Boolean(church.latitude) && Boolean(church.longitude) && (
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
          
          {/* Mini mapa placeholder */}
          <div className="bg-neutral-50 rounded-lg p-4 flex items-center justify-center min-h-40">
            {Boolean(church.latitude) && Boolean(church.longitude) ? (
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

      {/* Acciones rápidas - Solo visibles si showQuickActions es true */}
      {showQuickActions && (
        <Card className="p-6">
          <h3 className="text-lg font-semibold text-neutral-800 mb-4">Acciones Rápidas</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {actionsToRender.map((action) => (
              <Button
                key={action.label}
                variant="secondary"
                onClick={action.onClick}
                disabled={action.disabled}
              >
                <span className="flex items-center gap-2">
                  {action.icon}
                  {action.label}
                </span>
              </Button>
            ))}
          </div>
          {actionsToRender.some(a => a.disabled) && (
            <p className="text-xs text-neutral-500 mt-3">
              * Algunas funcionalidades próximamente disponibles
            </p>
          )}
        </Card>
      )}

      {/* Contenido adicional (ej: lista de iglesias hijas en MyChurch) */}
      {children}
    </div>
  );
}
