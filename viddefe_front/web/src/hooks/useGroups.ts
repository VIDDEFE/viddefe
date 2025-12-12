import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { groupService } from '../services/groupService'
import type { Group } from '../models'

export function useGroups() {
  return useQuery<Group[], Error>(['groups'], () => groupService.getAll())
}

export function useGroup(id?: string) {
  return useQuery<Group, Error>(['groups', id], () => groupService.getById(id!), { enabled: !!id })
}

export function useCreateGroup() {
  const qc = useQueryClient()
  return useMutation((data: Omit<Group, 'id' | 'createdAt' | 'updatedAt'>) => groupService.create(data), {
    onSuccess() {
      qc.invalidateQueries(['groups'])
    },
  })
}

export function useUpdateGroup() {
  const qc = useQueryClient()
  return useMutation(({ id, data }: { id: string; data: Partial<Group> }) => groupService.update(id, data), {
    onSuccess() {
      qc.invalidateQueries(['groups'])
    },
  })
}

export function useDeleteGroup() {
  const qc = useQueryClient()
  return useMutation((id: string) => groupService.delete(id), {
    onSuccess() {
      qc.invalidateQueries(['groups'])
    },
  })
}
