// Opciones para roles
export const ROLE_OPTIONS = [
  { value: 'pastor', label: 'Pastor' },
  { value: 'deacon', label: 'Diácono' },
  { value: 'member', label: 'Miembro' },
  { value: 'visitor', label: 'Visitante' },
  { value: 'volunteer', label: 'Voluntario' },
];

// Opciones para tipos de servicio
export const SERVICE_TYPE_OPTIONS = [
  { value: 'sunday_service', label: 'Servicio Dominical' },
  { value: 'wednesday_service', label: 'Servicio Miércoles' },
  { value: 'prayer_night', label: 'Noche de Oración' },
  { value: 'special_event', label: 'Evento Especial' },
  { value: 'youth_service', label: 'Servicio de Jóvenes' },
];

// Opciones para tipos de grupo
export const GROUP_TYPE_OPTIONS = [
  { value: 'home_group', label: 'Grupo de Hogar' },
  { value: 'youth_group', label: 'Grupo de Jóvenes' },
  { value: 'womens_group', label: 'Grupo de Mujeres' },
  { value: 'mens_group', label: 'Grupo de Hombres' },
  { value: 'prayer_group', label: 'Grupo de Oración' },
  { value: 'study_group', label: 'Grupo de Estudio' },
];

// Opciones para estados de eventos
export const EVENT_STATUS_OPTIONS = [
  { value: 'planned', label: 'Planeado' },
  { value: 'in_progress', label: 'En Progreso' },
  { value: 'completed', label: 'Completado' },
  { value: 'cancelled', label: 'Cancelado' },
];

// Opciones para estados de personas
export const PERSON_STATUS_OPTIONS = [
  { value: 'active', label: 'Activo' },
  { value: 'inactive', label: 'Inactivo' },
  { value: 'suspended', label: 'Suspendido' },
];

// Mensajes comunes
export const MESSAGES = {
  SUCCESS: 'Operación completada exitosamente',
  ERROR: 'Ocurrió un error inesperado',
  CONFIRM_DELETE: '¿Estás seguro de que deseas eliminar este elemento?',
  LOADING: 'Cargando...',
  NO_DATA: 'No hay datos disponibles',
};

// Colores por estado
export const STATUS_COLORS = {
  active: '#48bb78',
  inactive: '#ed8936',
  suspended: '#f56565',
  planned: '#667eea',
  in_progress: '#ed8936',
  completed: '#48bb78',
  cancelled: '#cbd5e0',
};
