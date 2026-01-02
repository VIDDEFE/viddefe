// Formatear fechas
export const formatDate = (date: Date | string | undefined): string | null => {
  if (!date) return null;
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

// Formatear hora
export const formatTime = (time: string): string => {
  const [hours, minutes] = time.split(':');
  return `${hours}:${minutes}`;
};

// Validar email
export const validateEmail = (email: string): boolean => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
};

// Validar teléfono
export const validatePhone = (phone: string): boolean => {
  const re = /^[\d\s\-\+\(\)]+$/;
  return re.test(phone) && phone.replace(/\D/g, '').length >= 9;
};

// Generar ID único
export const generateId = (): string => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2);
};

// Truncar texto
export const truncateText = (text: string, maxLength: number): string => {
  return text.length > maxLength ? text.substr(0, maxLength) + '...' : text;
};

// Capitalizar
export const capitalize = (text: string): string => {
  return text.charAt(0).toUpperCase() + text.slice(1);
};

// Traducciones de tipos
export const translateRole = (role: string): string => {
  const translations: Record<string, string> = {
    pastor: 'Pastor',
    deacon: 'Diácono',
    member: 'Miembro',
    visitor: 'Visitante',
    volunteer: 'Voluntario',
  };
  return translations[role] || role;
};

export const translateServiceType = (type: string): string => {
  const translations: Record<string, string> = {
    sunday_service: 'Servicio Dominical',
    wednesday_service: 'Servicio Miércoles',
    prayer_night: 'Noche de Oración',
    special_event: 'Evento Especial',
    youth_service: 'Servicio de Jóvenes',
  };
  return translations[type] || type;
};

export const translateGroupType = (type: string): string => {
  const translations: Record<string, string> = {
    home_group: 'Grupo de Hogar',
    youth_group: 'Grupo de Jóvenes',
    womens_group: 'Grupo de Mujeres',
    mens_group: 'Grupo de Hombres',
    prayer_group: 'Grupo de Oración',
    study_group: 'Grupo de Estudio',
  };
  return translations[type] || type;
};

export const translateEventStatus = (status: string): string => {
  const translations: Record<string, string> = {
    planned: 'Planeado',
    in_progress: 'En Progreso',
    completed: 'Completado',
    cancelled: 'Cancelado',
  };
  return translations[status] || status;
};

/**
 * Convierte un valor de datetime-local a LocalDateTime string para enviar al backend.
 * IMPORTANTE: NO usar new Date().toISOString() porque convierte a UTC y agrega 'Z'.
 * El backend espera LocalDateTime sin zona horaria: "YYYY-MM-DDTHH:mm:ss"
 * 
 * @param value - Valor del input datetime-local (formato: "YYYY-MM-DDTHH:mm")
 * @returns String en formato LocalDateTime ("YYYY-MM-DDTHH:mm:ss")
 */
export const toLocalDateTime = (value: string): string => {
  // datetime-local devuelve "YYYY-MM-DDTHH:mm" (16 caracteres)
  // Solo agregamos los segundos si faltan
  return value.length === 16 ? `${value}:00` : value;
};

/**
 * Convierte una fecha ISO o LocalDateTime del backend a formato datetime-local para el input.
 * Extrae solo la parte "YYYY-MM-DDTHH:mm" sin conversiones de zona horaria.
 * 
 * @param isoDate - Fecha del backend (puede tener o no zona horaria)
 * @returns String en formato datetime-local ("YYYY-MM-DDTHH:mm")
 */
export const toDatetimeLocal = (isoDate: string): string => {
  if (!isoDate) return '';
  // Extraer solo los primeros 16 caracteres: "YYYY-MM-DDTHH:mm"
  // Esto funciona tanto para "2025-01-03T16:00:00" como para "2025-01-03T16:00:00Z"
  return isoDate.slice(0, 16);
};
