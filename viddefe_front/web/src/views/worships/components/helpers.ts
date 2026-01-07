import { formatDateForDisplay } from '../../../utils/helpers';

/**
 * Formatea una fecha ISO a formato legible en español
 * @deprecated Usar formatDateForDisplay de utils/helpers directamente
 */
export function formatDate(isoDate: string): string {
  return formatDateForDisplay(isoDate, 'date');
}

/**
 * Formatea una fecha ISO para mostrar solo la hora
 * @deprecated Usar formatDateForDisplay de utils/helpers directamente
 */
export function formatTime(isoDate: string): string {
  return formatDateForDisplay(isoDate, 'time');
}

/**
 * Formatea un número como moneda
 */
export function formatCurrency(amount: number): string {
  return amount.toLocaleString('es-ES', { minimumFractionDigits: 2 });
}

/**
 * Calcula el porcentaje de asistencia
 */
export function calculateAttendancePercentage(totalAttendance: number, presentCount: number): number {
  return totalAttendance > 0 
    ? Math.round((presentCount / totalAttendance) * 100) 
    : 0;
}
