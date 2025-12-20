import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { personService } from '../services/personService'
import type { Person } from '../models'
import type { Pageable } from '../services'

export function usePeople() {
  return useQuery<Pageable<Person>, Error>({
    queryKey: ['people'],
    queryFn: () => personService.getAll()
  })
}

export function usePerson(id?: string) {
  return useQuery<Person, Error>({
    queryKey: ['people', id],
    queryFn: () => personService.getById(id!),
    enabled: !!id
  })
}

export function useCreatePerson() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: Omit<Person, 'id' | 'createdAt' | 'updatedAt'>) => personService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['people'] })
    },
  })
}

export function useUpdatePerson() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Person> }) => personService.update(id, data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['people'] })
    },
  })
}

export function useDeletePerson() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => personService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['people'] })
    },
  })
}
