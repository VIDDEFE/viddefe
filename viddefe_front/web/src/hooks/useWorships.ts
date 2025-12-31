import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { worshipService, type RegisterAttendanceDto } from '../services/worshipService';
import type { Worship, WorshipDetail, WorshipType, CreateWorshipDto, UpdateWorshipDto } from '../models';
import type { Pageable, PageableRequest } from '../services/api';

/**
 * Hook para obtener todos los cultos con paginación
 */
export function useWorshipMeetings(params?: PageableRequest) {
  return useQuery<Pageable<Worship>, Error>({
    queryKey: ['worships', params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => worshipService.getAll(params),
    placeholderData: keepPreviousData
  });
}

/**
 * Hook para obtener un culto por ID con detalle de asistencia
 */
export function useWorshipMeeting(id?: string) {
  return useQuery<WorshipDetail, Error>({
    queryKey: ['worship', id],
    queryFn: () => worshipService.getById(id!),
    enabled: !!id,
    placeholderData: keepPreviousData
  });
}

/**
 * Hook para obtener todos los tipos de culto
 */
export function useWorshipMeetingTypes() {
  return useQuery<WorshipType[], Error>({
    queryKey: ['worshipTypes'],
    queryFn: () => worshipService.getTypes(),
    staleTime: 5 * 60 * 1000, // 5 minutos - los tipos no cambian frecuentemente
    placeholderData: keepPreviousData
  });
}

/**
 * Hook para crear un nuevo culto
 */
export function useCreateWorship() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateWorshipDto) => worshipService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['worships'] });
    },
  });
}

/**
 * Hook para actualizar un culto existente
 */
export function useUpdateWorship() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateWorshipDto }) =>
      worshipService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['worships'] });
    },
  });
}

/**
 * Hook para eliminar un culto
 */
export function useDeleteWorship() {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => worshipService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['worships'] });
    },
  });
}

/**
 * Hook para obtener la asistencia de un culto paginada
 */
export function useWorshipAttendance(id?: string, params?: PageableRequest) {
  return useQuery<Pageable<import('../models').WorshipAttendance>, Error>({
    queryKey: ['worshipAttendance', id, params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => worshipService.getAttendance(id!, params),
    enabled: !!id,
    placeholderData: keepPreviousData
  });
}

/**
 * Hook para registrar o cambiar asistencia con actualización optimista
 */
export function useRegisterAttendance(worshipId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: RegisterAttendanceDto) => worshipService.registerAttendance(data),
    
    // Actualización optimista para evitar parpadeo
    onMutate: async (data) => {
      // Cancelar queries en curso para evitar sobreescribir la actualización optimista
      await qc.cancelQueries({ queryKey: ['worshipAttendance', worshipId] });
      await qc.cancelQueries({ queryKey: ['worship', worshipId] });

      // Snapshot del estado anterior
      const previousAttendance = qc.getQueriesData({ queryKey: ['worshipAttendance', worshipId] });
      const previousWorship = qc.getQueryData(['worship', worshipId]);

      // Actualizar optimistamente la lista de asistencia
      qc.setQueriesData(
        { queryKey: ['worshipAttendance', worshipId] },
        (old: Pageable<import('../models').WorshipAttendance> | undefined) => {
          if (!old) return old;
          return {
            ...old,
            content: old.content.map((record) => {
              if (record.people.id === data.peopleId) {
                // Toggle el estado
                const newStatus = record.status === 'PRESENT' ? 'ABSENT' : 'PRESENT';
                return { ...record, status: newStatus };
              }
              return record;
            }),
          };
        }
      );

      // Actualizar optimistamente los contadores del worship detail
      qc.setQueryData(['worship', worshipId], (old: WorshipDetail | undefined) => {
        if (!old) return old;
        
        // Buscar el estado actual de la persona en attendance
        const attendanceQueries = qc.getQueriesData<Pageable<import('../models').WorshipAttendance>>({ 
          queryKey: ['worshipAttendance', worshipId] 
        });
        
        let wasPresent = false;
        for (const [, queryData] of attendanceQueries) {
          const record = queryData?.content?.find(r => r.people.id === data.peopleId);
          if (record) {
            // El estado ya fue toggleado en la actualización anterior, así que verificamos el NUEVO estado
            wasPresent = record.status === 'ABSENT'; // Si ahora es ABSENT, antes era PRESENT
            break;
          }
        }

        return {
          ...old,
          presentCount: wasPresent ? old.presentCount - 1 : old.presentCount + 1,
          absentCount: wasPresent ? old.absentCount + 1 : old.absentCount - 1,
        };
      });

      return { previousAttendance, previousWorship };
    },

    // Revertir en caso de error
    onError: (_err, _data, context) => {
      if (context?.previousAttendance) {
        for (const [queryKey, data] of context.previousAttendance) {
          qc.setQueryData(queryKey, data);
        }
      }
      if (context?.previousWorship) {
        qc.setQueryData(['worship', worshipId], context.previousWorship);
      }
    },

    // Siempre refetch después para asegurar sincronización
    onSettled: () => {
      if (worshipId) {
        qc.invalidateQueries({ queryKey: ['worship', worshipId] });
        qc.invalidateQueries({ queryKey: ['worshipAttendance', worshipId] });
      }
      qc.invalidateQueries({ queryKey: ['worships'] });
    },
  });
}
