import { apiService } from './api';
import type { Service } from '../models';

export const serviceService = {
  getAll: () => apiService.get<Service[]>('/services'),
  getById: (id: string) => apiService.get<Service>(`/services/${id}`),
  create: (service: Omit<Service, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Service>('/services', service),
  update: (id: string, service: Partial<Service>) =>
    apiService.put<Service>(`/services/${id}`, service),
  delete: (id: string) => apiService.delete(`/services/${id}`),
  getByChurch: (churchId: string) =>
    apiService.get<Service[]>(`/services?churchId=${churchId}`),
};
