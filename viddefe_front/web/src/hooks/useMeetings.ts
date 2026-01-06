import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { meetingService, type RegisterMeetingAttendanceDto } from '../services/meetingService';
import type { Meeting, MeetingType, MeetingAttendance, CreateMeetingDto, UpdateMeetingDto } from '../models';
import type { Pageable, PageableRequest } from '../services/api';

/**
 * Hook para obtener todas las reuniones de un grupo con paginación
 */
export function useMeetings(groupId?: string, params?: PageableRequest) {
  return useQuery<Pageable<Meeting>, Error>({
    queryKey: ['meetings', groupId, params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => meetingService.getAll(groupId!, params),
    enabled: !!groupId,
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para obtener una reunión por ID
 */
export function useMeeting(groupId?: string, meetingId?: string) {
  return useQuery<Meeting, Error>({
    queryKey: ['meeting', groupId, meetingId],
    queryFn: () => meetingService.getById(groupId!, meetingId!),
    enabled: !!groupId && !!meetingId,
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para obtener todos los tipos de reunión
 */
export function useMeetingTypes() {
  return useQuery<MeetingType[], Error>({
    queryKey: ['meetingTypes'],
    queryFn: () => meetingService.getTypes(),
    staleTime: 5 * 60 * 1000, // 5 minutos - los tipos no cambian frecuentemente
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para crear una nueva reunión
 */
export function useCreateMeeting(groupId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateMeetingDto) => meetingService.create(groupId!, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['meetings', groupId] });
    },
  });
}

/**
 * Hook para actualizar una reunión existente
 */
export function useUpdateMeeting(groupId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: ({ meetingId, data }: { meetingId: string; data: UpdateMeetingDto }) =>
      meetingService.update(groupId!, meetingId, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['meetings', groupId] });
    },
  });
}

/**
 * Hook para eliminar una reunión
 */
export function useDeleteMeeting(groupId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (meetingId: string) => meetingService.delete(groupId!, meetingId),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['meetings', groupId] });
    },
  });
}

/**
 * Hook para obtener la asistencia de una reunión paginada
 */
export function useMeetingAttendance(groupId?: string, meetingId?: string, params?: PageableRequest) {
  return useQuery<Pageable<MeetingAttendance>, Error>({
    queryKey: ['meetingAttendance', groupId, meetingId, params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => meetingService.getAttendance(groupId!, meetingId!, params),
    enabled: !!groupId && !!meetingId,
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para registrar o cambiar asistencia con actualización optimista
 */
export function useRegisterMeetingAttendance(groupId?: string, meetingId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: RegisterMeetingAttendanceDto) => meetingService.registerAttendance(groupId!, data),
    
    // Actualización optimista para evitar parpadeo
    onMutate: async (data) => {
      // Cancelar queries en curso
      await qc.cancelQueries({ queryKey: ['meetingAttendance', groupId, meetingId] });

      // Snapshot del estado anterior
      const previousAttendance = qc.getQueriesData({ queryKey: ['meetingAttendance', groupId, meetingId] });

      // Actualizar optimistamente la lista de asistencia
      qc.setQueriesData(
        { queryKey: ['meetingAttendance', groupId, meetingId] },
        (old: Pageable<MeetingAttendance> | undefined) => {
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

      return { previousAttendance };
    },

    // Revertir en caso de error
    onError: (_err, _data, context) => {
      if (context?.previousAttendance) {
        for (const [queryKey, data] of context.previousAttendance) {
          qc.setQueryData(queryKey, data);
        }
      }
      // Solo invalidamos en caso de error para resincronizar
      if (groupId && meetingId) {
        qc.invalidateQueries({ queryKey: ['meetingAttendance', groupId, meetingId] });
      }
    },

    // Invalidar queries relacionadas
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['meetings', groupId] });
    },
  });
}
