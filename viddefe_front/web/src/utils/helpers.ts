// ============================================================================
// DATE & TIMEZONE UTILITIES
// ============================================================================
// Backend expects and returns UTC (ISO-8601 with 'Z')
// Frontend handles all timezone conversions
// User interacts only with local time
// ============================================================================

/**
 * Formateador estándar para fechas en español (Colombia)
 * Respeta locale, DST y timezone del dispositivo
 */
export const dateFormatter = new Intl.DateTimeFormat('es-CO', {
  dateStyle: 'long',
  timeStyle: 'short',
});

export const dateOnlyFormatter = new Intl.DateTimeFormat('es-CO', {
  dateStyle: 'long',
});

export const timeOnlyFormatter = new Intl.DateTimeFormat('es-CO', {
  timeStyle: 'short',
});

export const shortDateFormatter = new Intl.DateTimeFormat('es-CO', {
  dateStyle: 'medium',
  timeStyle: 'short',
});

/**
 * Convierte un valor de datetime-local (tiempo local) a ISO-8601 UTC para enviar al backend.
 * 
 * El usuario selecciona en hora local, y esta función convierte a UTC con 'Z'.
 * 
 * @param localDatetimeValue - Valor del input datetime-local (formato: "YYYY-MM-DDTHH:mm")
 * @returns String ISO-8601 en UTC ("YYYY-MM-DDTHH:mm:ss.sssZ")
 * @throws Error si el valor no contiene una fecha válida
 * 
 * @example
 * // Usuario en Colombia (UTC-5) selecciona 20:34
 * toUTCISOString("2026-01-06T20:34")
 * // Returns: "2026-01-07T01:34:00.000Z"
 */
export const toUTCISOString = (localDatetimeValue: string): string => {
  if (!localDatetimeValue) {
    throw new Error('Date value is required');
  }
  
  const localDate = new Date(localDatetimeValue);
  
  if (Number.isNaN(localDate.getTime())) {
    throw new Error(`Invalid date value: ${localDatetimeValue}`);
  }
  
  return localDate.toISOString(); // Always returns UTC with 'Z'
};

/**
 * Convierte una fecha UTC del backend a formato datetime-local para inputs.
 * 
 * El backend envía UTC, esta función convierte a hora local para mostrar en el input.
 * 
 * @param utcIsoDate - Fecha del backend en UTC (ISO-8601)
 * @returns String en formato datetime-local ("YYYY-MM-DDTHH:mm") en hora local
 * 
 * @example
 * // Usuario en Colombia (UTC-5)
 * toDatetimeLocal("2026-01-07T01:34:00Z")
 * // Returns: "2026-01-06T20:34"
 */
export const toDatetimeLocal = (utcIsoDate: string): string => {
  if (!utcIsoDate) return '';
  
  const date = new Date(utcIsoDate);
  
  if (Number.isNaN(date.getTime())) return '';
  
  // Formatear en hora local para el input datetime-local
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day}T${hours}:${minutes}`;
};

/**
 * Formatea una fecha UTC para mostrar al usuario en su hora local.
 * 
 * @param utcIsoDate - Fecha del backend en UTC
 * @param options - Opciones de formato ('full' | 'date' | 'time' | 'short')
 * @returns String formateado en hora local
 * 
 * @example
 * formatDateForDisplay("2026-01-07T01:34:00Z", 'full')
 * // Returns: "6 de enero de 2026, 8:34 p. m." (en Colombia UTC-5)
 */
export const formatDateForDisplay = (
  utcIsoDate: string | undefined | null,
  options: 'full' | 'date' | 'time' | 'short' = 'full'
): string => {
  if (!utcIsoDate) return '-';
  
  try {
    const date = new Date(utcIsoDate);
    
    if (Number.isNaN(date.getTime())) return '-';
    
    switch (options) {
      case 'date':
        return dateOnlyFormatter.format(date);
      case 'time':
        return timeOnlyFormatter.format(date);
      case 'short':
        return shortDateFormatter.format(date);
      case 'full':
      default:
        return dateFormatter.format(date);
    }
  } catch {
    return utcIsoDate;
  }
};

/**
 * Valida que una fecha string tenga zona horaria (Z o offset).
 * Usar como guardrail antes de enviar al backend.
 * 
 * @param dateString - String de fecha a validar
 * @returns true si contiene indicador de timezone
 */
export const hasTimezone = (dateString: string): boolean => {
  // Debe contener Z o +/-HH:mm
  return /Z$|[+-]\d{2}:\d{2}$/.test(dateString);
};

/**
 * Guardrail para validar payload antes de enviar.
 * En desarrollo, lanza error si la fecha no tiene timezone.
 * 
 * @param dateString - String de fecha a validar
 * @param fieldName - Nombre del campo para el mensaje de error
 */
export const validateDatePayload = (dateString: string, fieldName: string): void => {
  if (!hasTimezone(dateString)) {
    const error = `[TIMEZONE ERROR] Field "${fieldName}" must include timezone (Z or offset). Got: "${dateString}"`;
    console.error(error);
    if (import.meta.env.DEV) {
      throw new Error(error);
    }
  }
};

// ============================================================================
// LEGACY FUNCTION - DEPRECATED
// ============================================================================

/**
 * @deprecated Use toUTCISOString instead
 * Esta función se mantiene por compatibilidad pero debe migrarse
 */
export const toLocalDateTime = (value: string): string => {
  console.warn('[DEPRECATED] toLocalDateTime is deprecated. Use toUTCISOString for UTC conversion.');
  return toUTCISOString(value);
};

// ============================================================================
// GENERAL UTILITIES
// ============================================================================

// Formatear fechas (legacy - para casos simples)
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
