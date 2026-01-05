/**
 * Formatea una fecha ISO a formato legible en español
 */
export function formatDate(isoDate: string): string {
  try {
    const date = new Date(isoDate);
    return date.toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  } catch {
    return isoDate;
  }
}

/**
 * Formatea una fecha ISO para mostrar solo la hora
 */
export function formatTime(isoDate: string): string {
  try {
    const date = new Date(isoDate);
    return date.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return isoDate;
  }
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
