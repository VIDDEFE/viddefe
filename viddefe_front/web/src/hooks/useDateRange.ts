import { useState, useMemo } from 'react';

export interface DateRange {
  start: string;
  end: string;
}

/**
 * Hook para manejar el rango de fechas con formateo automático
 * Inicializa con los últimos 30 días por defecto
 */
export function useDateRange(daysBack: number = 30) {
  // State para las fechas en formato YYYY-MM-DD
  const [dateRange, setDateRange] = useState<DateRange>(() => {
    const now = new Date();
    const daysAgo = new Date(now.getTime() - daysBack * 24 * 60 * 60 * 1000);

    const formatDateForInput = (date: Date): string => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    return {
      start: formatDateForInput(daysAgo),
      end: formatDateForInput(now),
    };
  });

  // Formatear fechas en ISO con timezone para API
  const formatDateWithTz = (dateStr: string): string => {
    const date = new Date(dateStr);
    const offset = -date.getTimezoneOffset();
    const offsetHours = String(Math.floor(Math.abs(offset) / 60)).padStart(2, '0');
    const offsetMinutes = String(Math.abs(offset) % 60).padStart(2, '0');
    const sign = offset >= 0 ? '+' : '-';
    return `${dateStr}T00:00:00${sign}${offsetHours}:${offsetMinutes}`;
  };

  // Fechas formateadas para la API
  const formattedDates = useMemo(
    () => ({
      startTime: formatDateWithTz(dateRange.start),
      endTime: formatDateWithTz(dateRange.end),
    }),
    [dateRange]
  );

  // Handlers para actualizar fechas
  const setStartDate = (date: string) => {
    setDateRange((prev) => ({ ...prev, start: date }));
  };

  const setEndDate = (date: string) => {
    setDateRange((prev) => ({ ...prev, end: date }));
  };

  return {
    dateRange,
    formattedDates,
    setStartDate,
    setEndDate,
    setDateRange,
  };
}
