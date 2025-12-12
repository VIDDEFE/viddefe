import { apiService } from './api';
import type { Group } from '../models';

export const groupService = {
  getAll: () => apiService.get<Group[]>('/groups'),
  getById: (id: string) => apiService.get<Group>(`/groups/${id}`),
  create: (group: Omit<Group, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Group>('/groups', group),
  update: (id: string, group: Partial<Group>) =>
    apiService.put<Group>(`/groups/${id}`, group),
  delete: (id: string) => apiService.delete(`/groups/${id}`),
  getByChurch: (churchId: string) =>
    apiService.get<Group[]>(`/groups?churchId=${churchId}`),
};
