import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { personService } from '../services/personService'
import type { Person } from '../models'

export function usePeople() {
  return useQuery<Person[], Error>(['people'], () => personService.getAll())
}

export function usePerson(id?: string) {
  return useQuery<Person, Error>(['people', id], () => personService.getById(id!), { enabled: !!id })
}

export function useCreatePerson() {
  const qc = useQueryClient()
  return useMutation((data: Omit<Person, 'id' | 'createdAt' | 'updatedAt'>) => personService.create(data), {
    onSuccess() {
      qc.invalidateQueries(['people'])
    },
  })
}

export function useUpdatePerson() {
  const qc = useQueryClient()
  return useMutation(({ id, data }: { id: string; data: Partial<Person> }) => personService.update(id, data), {
    onSuccess() {
      qc.invalidateQueries(['people'])
      qc.invalidateQueries(['people', 'list'])
    },
  })
}

export function useDeletePerson() {
  const qc = useQueryClient()
  return useMutation((id: string) => personService.delete(id), {
    onSuccess() {
      qc.invalidateQueries(['people'])
    },
  })
}
