import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { groupService } from '../services/groupService'
import type { Group } from '../models'

export function useGroups() {
  return useQuery<Group[], Error>({
    queryKey: ['groups'],
    queryFn: () => groupService.getAll()
  })
}

export function useGroup(id?: string) {
  return useQuery<Group, Error>({
    queryKey: ['groups', id],
    queryFn: () => groupService.getById(id!),
    enabled: !!id
  })
}

export function useCreateGroup() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: Omit<Group, 'id' | 'createdAt' | 'updatedAt'>) => groupService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['groups'] })
    },
  })
}

export function useUpdateGroup() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Group> }) => groupService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['groups'] })
    },
  })
}

export function useDeleteGroup() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => groupService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['groups'] })
    },
  })
}
