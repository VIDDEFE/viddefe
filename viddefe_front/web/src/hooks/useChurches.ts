import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { churchService } from '../services/churchService'
import type { Church, ChurchDetail } from '../models'

export function useChurches() {
  return useQuery({
    queryKey: ['churches'],
    queryFn: churchService.getAll
  })
}

export function useChurch(id?: string) {
  return useQuery<ChurchDetail>({
    queryKey: ['church', id],
    queryFn: () => churchService.getById(id!),
    enabled: !!id
  })
}

export function useChurchChildren(churchId?: string) {
  return useQuery({
    queryKey: ['churches', 'children', churchId],
    queryFn: () => churchService.getChildren(churchId!),
    enabled: !!churchId
  })
}

export function useCreateChildrenChurch(churchId?: string) {
  const qc = useQueryClient()

  return useMutation({
    mutationFn: (data: Omit<Church, 'id' | 'createdAt' | 'updatedAt'>) =>
      churchService.createChildren(churchId!, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['churches'] })
    }
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
