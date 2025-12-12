import { apiService } from './api';
import type { Event } from '../models';

export const eventService = {
  getAll: () => apiService.get<Event[]>('/events'),
  getById: (id: string) => apiService.get<Event>(`/events/${id}`),
  create: (event: Omit<Event, 'id' | 'createdAt' | 'updatedAt'>) =>
    apiService.post<Event>('/events', event),
  update: (id: string, event: Partial<Event>) =>
    apiService.put<Event>(`/events/${id}`, event),
  delete: (id: string) => apiService.delete(`/events/${id}`),
  getByChurch: (churchId: string) =>
    apiService.get<Event[]>(`/events?churchId=${churchId}`),
};
