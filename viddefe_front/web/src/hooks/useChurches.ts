import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { churchService, type MapBounds } from '../services/churchService'
import type { Church, ChurchDetail, ChurchSummary } from '../models'
import type { Pageable, PageableRequest } from '../services/api'

export function useChurches(params?: PageableRequest) {
  return useQuery<Pageable<ChurchSummary>, Error>({
    queryKey: ['churches', params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => churchService.getAll(params)
  })
}

export function useChurch(id?: string) {
  return useQuery<ChurchDetail>({
    queryKey: ['church', id],
    queryFn: () => churchService.getById(id!),
    enabled: !!id
  })
}

/**
 * Hook para obtener la iglesia del usuario actual
 */
export function useMyChurch() {
  return useQuery<ChurchDetail>({
    queryKey: ['myChurch'],
    queryFn: () => churchService.getMine(),
  })
}

export function useChurchChildren(churchId?: string, params?: PageableRequest) {
  return useQuery<Pageable<ChurchSummary>, Error>({
    queryKey: ['churches', 'children', churchId, params?.page, params?.size, params?.sort?.field, params?.sort?.direction],
    queryFn: () => churchService.getChildren(churchId!, params),
    enabled: !!churchId
  })
}

/**
 * Hook para obtener iglesias hijas cercanas según los bounds del mapa
 */
export function useNearbyChurches(churchId?: string, bounds?: MapBounds | null) {
  return useQuery<ChurchSummary[], Error>({
    queryKey: ['churches', 'nearby', churchId, bounds?.southLat, bounds?.westLng, bounds?.northLat, bounds?.eastLng],
    queryFn: () => churchService.getChildrenNearby(churchId!, bounds!),
    enabled: !!churchId && !!bounds,
    staleTime: 30000, // 30 segundos para evitar refetches excesivos al mover el mapa
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
