import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { offeringService } from '../services/offeringService';
import type { Offering, OfferingType, CreateOfferingDto, UpdateOfferingDto } from '../models';
import type { Pageable, PageableRequest } from '../services/api';

/**
 * Hook para obtener las ofrendas de un evento con paginación
 */
export function useEventOfferings(eventId?: string, params?: PageableRequest) {
  return useQuery<Pageable<Offering>, Error>({
    queryKey: ['offerings', eventId, params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => offeringService.getByEventId(eventId!, params),
    enabled: !!eventId,
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para obtener todos los tipos de ofrenda
 */
export function useOfferingTypes() {
  return useQuery<OfferingType[], Error>({
    queryKey: ['offeringTypes'],
    queryFn: () => offeringService.getTypes(),
    staleTime: 5 * 60 * 1000, // 5 minutos - los tipos no cambian frecuentemente
    placeholderData: keepPreviousData,
  });
}

/**
 * Hook para crear una ofrenda con actualización optimista
 */
export function useCreateOffering(eventId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateOfferingDto) => offeringService.create(data),
    onSuccess: () => {
      if (eventId) {
        qc.invalidateQueries({ queryKey: ['offerings', eventId] });
        qc.invalidateQueries({ queryKey: ['worship', eventId] });
      }
    },
  });
}

/**
 * Hook para actualizar una ofrenda con actualización optimista
 */
export function useUpdateOffering(eventId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (data: UpdateOfferingDto) => offeringService.update(data),
    
    // Actualización optimista
    onMutate: async (data) => {
      await qc.cancelQueries({ queryKey: ['offerings', eventId] });
      
      const previousOfferings = qc.getQueriesData({ queryKey: ['offerings', eventId] });
      
      qc.setQueriesData(
        { queryKey: ['offerings', eventId] },
        (old: Pageable<Offering> | undefined) => {
          if (!old) return old;
          return {
            ...old,
            content: old.content.map((offering) => {
              if (offering.id === data.id) {
                return {
                  ...offering,
                  amount: data.amount,
                  // El tipo se actualizará con el refetch
                };
              }
              return offering;
            }),
          };
        }
      );

      return { previousOfferings };
    },

    onError: (_err, _data, context) => {
      if (context?.previousOfferings) {
        for (const [queryKey, data] of context.previousOfferings) {
          qc.setQueryData(queryKey, data);
        }
      }
    },

    onSuccess: () => {
      if (eventId) {
        qc.invalidateQueries({ queryKey: ['offerings', eventId] });
      }
    },
  });
}

/**
 * Hook para eliminar una ofrenda con actualización optimista
 */
export function useDeleteOffering(eventId?: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => offeringService.delete(id),
    
    // Actualización optimista
    onMutate: async (id) => {
      await qc.cancelQueries({ queryKey: ['offerings', eventId] });
      
      const previousOfferings = qc.getQueriesData({ queryKey: ['offerings', eventId] });
      
      qc.setQueriesData(
        { queryKey: ['offerings', eventId] },
        (old: Pageable<Offering> | undefined) => {
          if (!old) return old;
          return {
            ...old,
            content: old.content.filter((offering) => offering.id !== id),
            totalElements: old.totalElements - 1,
          };
        }
      );

      return { previousOfferings };
    },

    onError: (_err, _data, context) => {
      if (context?.previousOfferings) {
        for (const [queryKey, data] of context.previousOfferings) {
          qc.setQueryData(queryKey, data);
        }
      }
    },

    onSuccess: () => {
      if (eventId) {
        qc.invalidateQueries({ queryKey: ['offerings', eventId] });
        qc.invalidateQueries({ queryKey: ['worship', eventId] });
      }
    },
  });
}
