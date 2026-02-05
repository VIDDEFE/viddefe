import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { groupMeetingService, type RegisterMeetingAttendanceDto } from '../services/groupMeetingService';
import type { Meeting, MeetingType, CreateMeetingDto, UpdateMeetingDto, MeetingAttendance } from '../models';
import type { Pageable, PageableRequest } from '../services/api';

/**
 * Hook para obtener todas las reuniones de un grupo con paginación
 */
export function useMeetings(groupId?: string, params?: PageableRequest) {
  return useQuery<Pageable<Meeting>, Error>({
    queryKey: ['meetings', groupId, params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => groupMeetingService.getAll(groupId!, params),
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
    queryFn: () => groupMeetingService.getById(groupId!, meetingId!),
    enabled: !!groupId && !!meetingId,
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para obtener los tipos de reunión de grupo
 */
export function useMeetingTypes() {
  return useQuery<MeetingType[], Error>({
    queryKey: ['meetingTypes'],
    queryFn: () => groupMeetingService.getTypes(),
    staleTime: 5 * 60 * 1000, // 5 minutos
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para crear una nueva reunión
 */
export function useCreateMeeting(groupId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateMeetingDto) => groupMeetingService.create(groupId!, data),
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
      groupMeetingService.update(groupId!, meetingId, data),
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
    mutationFn: (meetingId: string) => groupMeetingService.delete(groupId!, meetingId),
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
    queryKey: ['meetingAttendance', groupId, meetingId, params?.page, params?.size],
    queryFn: () => groupMeetingService.getAttendance(groupId!, meetingId!, params),
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
    mutationFn: (data: RegisterMeetingAttendanceDto) => groupMeetingService.registerAttendance(groupId!, meetingId!, data),

    // Actualización optimista
    onMutate: async (data) => {
      await qc.cancelQueries({ queryKey: ['meetingAttendance', groupId, meetingId] });
      await qc.cancelQueries({ queryKey: ['meeting', groupId, meetingId] });

      const previousAttendance = qc.getQueriesData({ queryKey: ['meetingAttendance', groupId, meetingId] });
      const previousMeeting = qc.getQueryData(['meeting', groupId, meetingId]);

      // Actualizar optimistamente la lista de asistencia
      qc.setQueriesData(
        { queryKey: ['meetingAttendance', groupId, meetingId] },
        (old: Pageable<MeetingAttendance> | undefined) => {
          if (!old) return old;
          return {
            ...old,
            content: old.content.map((record) => {
              if (record.people.id === data.peopleId) {
                const newStatus = record.status === 'PRESENT' ? 'ABSENT' : 'PRESENT';
                return { ...record, status: newStatus };
              }
              return record;
            }),
          };
        }
      );

      // Actualizar optimistamente los contadores del meeting
      qc.setQueryData(['meeting', groupId, meetingId], (old: Meeting | undefined) => {
        if (!old) return old;

        const attendanceQueries = qc.getQueriesData<Pageable<MeetingAttendance>>({
          queryKey: ['meetingAttendance', groupId, meetingId],
        });

        let wasPresent = false;
        for (const [, queryData] of attendanceQueries) {
          const record = queryData?.content?.find((r) => r.people.id === data.peopleId);
          if (record) {
            wasPresent = record.status === 'ABSENT';
            break;
          }
        }

        return {
          ...old,
          presentCount: wasPresent ? (old.presentCount ?? 0) - 1 : (old.presentCount ?? 0) + 1,
          absentCount: wasPresent ? (old.absentCount ?? 0) + 1 : (old.absentCount ?? 0) - 1,
        };
      });

      return { previousAttendance, previousMeeting };
    },

    onError: (_err, _data, context) => {
      if (context?.previousAttendance) {
        for (const [queryKey, data] of context.previousAttendance) {
          qc.setQueryData(queryKey, data);
        }
      }
      if (context?.previousMeeting) {
        qc.setQueryData(['meeting', groupId, meetingId], context.previousMeeting);
      }
      if (groupId && meetingId) {
        qc.invalidateQueries({ queryKey: ['meeting', groupId, meetingId] });
        qc.invalidateQueries({ queryKey: ['meetingAttendance', groupId, meetingId] });
      }
    },

    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['meetings', groupId] });
    },
  });
}
