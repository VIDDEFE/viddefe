import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { churchService } from '../services/churchService'
import type { Church } from '../models'

export function useChurches() {
  return useQuery({
    queryKey: ['churches'],
    queryFn: churchService.getAll
  })
}

export function useChurch(id?: string) {
  return useQuery({
    queryKey: ['church', id],
    queryFn: () => churchService.getById(id!),
    enabled: !!id
  })
}

export function useCreateChurch() {
  const qc = useQueryClient()

  return useMutation({
    mutationFn: (data: Omit<Church, 'id' | 'createdAt' | 'updatedAt'>) =>
      churchService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['churches'] })
    }
  })
}

export function useUpdateChurch() {
  const qc = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Church> }) =>
      churchService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['churches'] })
    }
  })
}

export function useDeleteChurch() {
  const qc = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => churchService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['churches'] })
    }
  })
}
