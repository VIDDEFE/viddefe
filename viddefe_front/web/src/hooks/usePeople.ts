import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { personService, type PeopleSearchParams, type PersonType } from '../services/personService'
import type { Person } from '../models'
import type { Pageable } from '../services/api'

export function usePeople(params?: PeopleSearchParams) {
  return useQuery<Pageable<Person>, Error>({
    queryKey: ['people', params?.page, params?.size, params?.typePersonId, params?.sort?.field, params?.sort?.direction],
    queryFn: () => personService.getAll(params)
  })
}

export function usePersonTypes() {
  return useQuery<PersonType[], Error>({
    queryKey: ['personTypes'],
    queryFn: () => personService.getTypes(),
    staleTime: 1000 * 60 * 10, // 10 minutos - los tipos no cambian frecuentemente
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
