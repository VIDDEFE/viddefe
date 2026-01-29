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

  // Miembros de grupo (paginado)
  getMembers: (groupId: string, params?: any) => {
    const query = new URLSearchParams();
    if (params?.page !== undefined) query.append('page', String(params.page));
    if (params?.size !== undefined) query.append('size', String(params.size));
    if (params?.sort) query.append('sort', params.sort);
    const qs = query.toString();
    return apiService.get(`/groups/${groupId}/members${qs ? `?${qs}` : ''}`);
  },
  addMember: (groupId: string, peopleId: string) => apiService.post(`/groups/${groupId}/members/${peopleId}`),
  removeMember: (groupId: string, peopleId: string) => apiService.delete(`/groups/${groupId}/members/${peopleId}`),
};
