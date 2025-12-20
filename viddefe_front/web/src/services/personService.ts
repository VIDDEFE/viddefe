import { apiService, type Pageable } from './api';
import type { Person } from '../models';

export const personService = {
  getAll: () => apiService.get<Pageable<Person>>('/people'),
  getById: (id: string) => apiService.get<Person>(`/people/${id}`),
  create: (person: Omit<Person, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Person>('/people', person),
  update: (id: string, person: Partial<Person>) =>
    apiService.put<Person>(`/people/${id}`, person),
  delete: (id: string) => apiService.delete(`/people/${id}`),
  getByChurch: (churchId: string) =>
    apiService.get<Person[]>(`/people?churchId=${churchId}`),
};
