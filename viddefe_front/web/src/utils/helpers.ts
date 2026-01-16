// ============================================================================
// DATE & TIMEZONE UTILITIES
// ============================================================================
// Backend REQUIRES scheduledDate with timezone offset (ISO-8601)
// Backend RETURNS dates in UTC (with 'Z' suffix)
// Frontend handles all timezone conversions
// User interacts only with local time
// ============================================================================

import { format as formatTz, toZonedTime } from 'date-fns-tz';

/**
 * Obtiene la zona horaria del usuario automáticamente.
 * Permite override desde localStorage para configuración manual.
 */
export const getUserTimeZone = (): string => {
  return localStorage.getItem('userTimeZone') || 
         Intl.DateTimeFormat().resolvedOptions().timeZone;
};

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
 * Regex para validar formato ISO-8601 con timezone (offset o Z)
 */
const ISO_8601_WITH_TZ_REGEX = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}([+-]\d{2}:\d{2}|Z)$/;

/**
 * Convierte un valor de datetime-local (tiempo local) a ISO-8601 CON OFFSET para enviar al backend.
 * 
 * IMPORTANTE: El backend REQUIERE el offset de timezone. Ya no acepta fechas sin timezone.
 * 
 * @param localDatetimeValue - Valor del input datetime-local (formato: "YYYY-MM-DDTHH:mm")
 * @param timeZone - Zona horaria del usuario (por defecto: detectada automáticamente)
 * @returns String ISO-8601 con offset ("YYYY-MM-DDTHH:mm:ss-05:00")
 * @throws Error si el valor no contiene una fecha válida
 * 
 * @example
 * // Usuario en Colombia (UTC-5) selecciona 10:00
 * toISOStringWithOffset("2026-01-15T10:00")
 * // Returns: "2026-01-15T10:00:00-05:00"
 * 
 * // Usuario en Madrid (UTC+1) selecciona 16:00
 * toISOStringWithOffset("2026-01-15T16:00", "Europe/Madrid")
 * // Returns: "2026-01-15T16:00:00+01:00"
 */
export const toISOStringWithOffset = (
  localDatetimeValue: string,
  timeZone: string = getUserTimeZone()
): string => {
  if (!localDatetimeValue) {
    throw new Error('Date value is required');
  }
  
  // Crear fecha interpretada como hora local del usuario
  const localDate = new Date(localDatetimeValue);
  
  if (Number.isNaN(localDate.getTime())) {
    throw new Error(`Invalid date value: ${localDatetimeValue}`);
  }
  
  // Formatear con offset de timezone usando date-fns-tz
  // 'XXX' produce formato offset como '-05:00'
  const isoWithOffset = formatTz(localDate, "yyyy-MM-dd'T'HH:mm:ssXXX", { timeZone });
  
  // Validar que el resultado tenga timezone
  if (!ISO_8601_WITH_TZ_REGEX.test(isoWithOffset)) {
    throw new Error(`Failed to generate ISO-8601 with timezone: ${isoWithOffset}`);
  }
  
  return isoWithOffset;
};

/**
 * @deprecated Use toISOStringWithOffset instead - Backend now REQUIRES timezone offset
 * Convierte un valor de datetime-local (tiempo local) a ISO-8601 UTC para enviar al backend.
 * 
 * ADVERTENCIA: Esta función genera formato UTC con 'Z' que el backend PUEDE RECHAZAR.
 * Use toISOStringWithOffset() para el nuevo contrato de API.
 * 
 * @param localDatetimeValue - Valor del input datetime-local (formato: "YYYY-MM-DDTHH:mm")
 * @returns String ISO-8601 en UTC ("YYYY-MM-DDTHH:mm:ss.sssZ")
 * @throws Error si el valor no contiene una fecha válida
 */
export const toUTCISOString = (localDatetimeValue: string): string => {
  console.warn('[DEPRECATED] toUTCISOString is deprecated. Use toISOStringWithOffset for new API contract.');
  
  if (!localDatetimeValue) {
    throw new Error('Date value is required');
  }
  
  const localDate = new Date(localDatetimeValue);
  
  if (Number.isNaN(localDate.getTime())) {
    throw new Error(`Invalid date value: ${localDatetimeValue}`);
  }
  
  return localDate.toISOString(); // Returns UTC with 'Z'
};

/**
 * Convierte una fecha UTC del backend a formato datetime-local para inputs.
 * 
 * El backend envía UTC, esta función convierte a hora local para mostrar en el input.
 * 
 * @param utcIsoDate - Fecha del backend en UTC (ISO-8601 con 'Z' o offset)
 * @param timeZone - Zona horaria del usuario (por defecto: detectada automáticamente)
 * @returns String en formato datetime-local ("YYYY-MM-DDTHH:mm") en hora local
 * 
 * @example
 * // Usuario en Colombia (UTC-5)
 * toDatetimeLocal("2026-01-15T15:00:00Z")
 * // Returns: "2026-01-15T10:00" (15:00 UTC = 10:00 Bogotá)
 */
export const toDatetimeLocal = (
  utcIsoDate: string,
  timeZone: string = getUserTimeZone()
): string => {
  if (!utcIsoDate) return '';
  
  try {
    const date = new Date(utcIsoDate);
    
    if (Number.isNaN(date.getTime())) return '';
    
    // Convertir a la zona horaria del usuario usando date-fns-tz
    const zonedDate = toZonedTime(date, timeZone);
    
    // Formatear para el input datetime-local
    const year = zonedDate.getFullYear();
    const month = String(zonedDate.getMonth() + 1).padStart(2, '0');
    const day = String(zonedDate.getDate()).padStart(2, '0');
    const hours = String(zonedDate.getHours()).padStart(2, '0');
    const minutes = String(zonedDate.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  } catch {
    return '';
  }
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
/**
 * Formatea una fecha UTC para mostrar al usuario en su hora local.
 * 
 * @param utcIsoDate - Fecha del backend en UTC (siempre termina con 'Z')
 * @param options - Opciones de formato ('full' | 'date' | 'time' | 'short')
 * @param timeZone - Zona horaria del usuario (por defecto: detectada automáticamente)
 * @returns String formateado en hora local
 * 
 * @example
 * // Usuario en Colombia (UTC-5)
 * formatDateForDisplay("2026-01-15T15:00:00Z", 'full')
 * // Returns: "15 de enero de 2026, 10:00 a. m." (en Colombia UTC-5)
 */
export const formatDateForDisplay = (
  utcIsoDate: string | undefined | null,
  options: 'full' | 'date' | 'time' | 'short' = 'full',
  timeZone: string = getUserTimeZone()
): string => {
  if (!utcIsoDate) return '-';
  
  try {
    const date = new Date(utcIsoDate);
    
    if (Number.isNaN(date.getTime())) return '-';
    
    // Usar toLocaleString con la zona horaria específica
    const formatOptions: Intl.DateTimeFormatOptions = { timeZone };
    
    switch (options) {
      case 'date':
        return date.toLocaleDateString('es-CO', {
          ...formatOptions,
          dateStyle: 'long',
        });
      case 'time':
        return date.toLocaleTimeString('es-CO', {
          ...formatOptions,
          timeStyle: 'short',
        });
      case 'short':
        return date.toLocaleString('es-CO', {
          ...formatOptions,
          dateStyle: 'medium',
          timeStyle: 'short',
        });
      case 'full':
      default:
        return date.toLocaleString('es-CO', {
          ...formatOptions,
          dateStyle: 'long',
          timeStyle: 'short',
        });
    }
  } catch {
    return utcIsoDate;
  }
};

/**
 * Regex para validar que una fecha string tenga zona horaria (Z o offset).
 */
const TIMEZONE_REGEX = /Z$|[+-]\d{2}:\d{2}$/;

/**
 * Valida que una fecha string tenga zona horaria (Z o offset).
 * Usar como guardrail antes de enviar al backend.
 * 
 * @param dateString - String de fecha a validar
 * @returns true si contiene indicador de timezone
 */
export const hasTimezone = (dateString: string): boolean => {
  return TIMEZONE_REGEX.test(dateString);
};

/**
 * Valida que una fecha cumpla con el formato ISO-8601 con timezone obligatorio.
 * 
 * @param dateString - String de fecha a validar
 * @returns true si cumple con formato ISO-8601 con offset o Z
 */
export const isValidISOWithTimezone = (dateString: string): boolean => {
  return ISO_8601_WITH_TZ_REGEX.test(dateString);
};

/**
 * Guardrail para validar payload antes de enviar.
 * SIEMPRE lanza error si la fecha no tiene timezone (el backend rechazará con 400).
 * 
 * @param dateString - String de fecha a validar
 * @param fieldName - Nombre del campo para el mensaje de error
 * @throws Error si la fecha no incluye timezone
 */
export const validateDatePayload = (dateString: string, fieldName: string): void => {
  if (!hasTimezone(dateString)) {
    const error = `[TIMEZONE ERROR] Campo "${fieldName}" debe incluir zona horaria. ` +
                  `Formato esperado: 2026-01-15T10:00:00-05:00 o 2026-01-15T15:00:00Z. ` +
                  `Recibido: "${dateString}"`;
    console.error(error);
    throw new Error(error);
  }
};

// ============================================================================
// LEGACY FUNCTION - DEPRECATED
// ============================================================================

/**
 * @deprecated Use toISOStringWithOffset instead
 * Esta función se mantiene por compatibilidad pero debe migrarse
 */
export const toLocalDateTime = (value: string): string => {
  console.warn('[DEPRECATED] toLocalDateTime is deprecated. Use toISOStringWithOffset for new API contract.');
  return toISOStringWithOffset(value);
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
