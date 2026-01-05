import { useQuery } from '@tanstack/react-query'
import { stateCitiesService } from '../services/stateCitiesService'

export function useStates() {
  return useQuery({
    queryKey: ['states'],
    queryFn: () => stateCitiesService.getStates(),
  })
}

export function useCities(stateId?: number) {
  return useQuery({
    queryKey: ['cities', stateId],
    queryFn: () => stateCitiesService.getCitiesByState(stateId ?? 0),
    enabled: typeof stateId === 'number' && stateId > 0,
  })
}
