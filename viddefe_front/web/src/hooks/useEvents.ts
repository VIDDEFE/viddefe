import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { eventService } from '../services/eventService'
import type { Event } from '../models'

export function useEvents() {
  return useQuery<Event[], Error>({
    queryKey: ['events'],
    queryFn: () => eventService.getAll()
  })
}

export function useEvent(id?: string) {
  return useQuery<Event, Error>({
    queryKey: ['events', id],
    queryFn: () => eventService.getById(id!),
    enabled: !!id
  })
}

export function useCreateEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: Omit<Event, 'id' | 'createdAt' | 'updatedAt'>) => eventService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['events'] })
    },
  })
}

export function useUpdateEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Event> }) => eventService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['events'] })
    },
  })
}

export function useDeleteEvent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => eventService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['events'] })
    },
  })
}
