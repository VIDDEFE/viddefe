import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { serviceService } from '../services/serviceService'
import type { Service } from '../models'

export function useServices() {
  return useQuery<Service[], Error>(['services'], () => serviceService.getAll())
}

export function useService(id?: string) {
  return useQuery<Service, Error>(['services', id], () => serviceService.getById(id!), { enabled: !!id })
}

export function useCreateService() {
  const qc = useQueryClient()
  return useMutation((data: Omit<Service, 'id' | 'createdAt' | 'updatedAt'>) => serviceService.create(data), {
    onSuccess() {
      qc.invalidateQueries(['services'])
    },
  })
}

export function useUpdateService() {
  const qc = useQueryClient()
  return useMutation(({ id, data }: { id: string; data: Partial<Service> }) => serviceService.update(id, data), {
    onSuccess() {
      qc.invalidateQueries(['services'])
    },
  })
}

export function useDeleteService() {
  const qc = useQueryClient()
  return useMutation((id: string) => serviceService.delete(id), {
    onSuccess() {
      qc.invalidateQueries(['services'])
    },
  })
}
