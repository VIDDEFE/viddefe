import { apiService, type Pageable } from './api';
import type { Church } from '../models';

export const churchService = {
  getAll: () => apiService.get<Pageable<Church>>('/churches'),
  getById: (id: string) => apiService.get<Church>(`/churches/${id}`),
  create: (church: Omit<Church, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Church>('/churches', church),
  update: (id: string, church: Partial<Church>) =>
    apiService.put<Church>(`/churches/${id}`, church),
  delete: (id: string) => apiService.delete(`/churches/${id}`),
};
