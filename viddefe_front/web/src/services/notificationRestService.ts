import { apiService } from './api';

export interface UserNotificationResponse {
  id: string;
  notificationId: string;
  peopleId: string;
  title: string;
  body: string;
  type: 'EVENT' | 'MINISTRY' | 'ADMINISTRATIVE';
  channel: 'APP' | 'EMAIL' | 'WHATSAPP';
  template: string;
  variables: Record<string, any>;
  status: 'PENDING' | 'SENT' | 'READ' | 'FAILED';
  readAt: string | null;
  createdAt: string;
  message: string; // Added for frontend convenience (can be generated from template + variables)
  updatedAt: string;
}

export interface NotificationListResponse {
  notifications: UserNotificationResponse[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNextPage: boolean;
  isFirstPage: boolean;
  isLastPage: boolean;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

/**
 * Notification REST API Service
 * Handles communication with backend notification endpoints
 * Uses standard REST API (different from SSE streaming)
 */
export const notificationRestService = {
  /**
   * List all notifications for the authenticated user (paginated)
   */
  listNotifications: (params?: PaginationParams) => {
    const queryParams = new URLSearchParams();
    if (params) {
      if (params.page !== undefined) queryParams.append('page', params.page.toString());
      if (params.size !== undefined) queryParams.append('size', params.size.toString());
      if (params.sort) queryParams.append('sort', params.sort);
    }

    const query = queryParams.toString();
    const url = query ? `/notifications?${query}` : '/notifications';
    
    return apiService.get<NotificationListResponse>(url);
  },

  /**
   * Get a specific notification by ID
   */
  getNotification: (notificationId: string) => {
    return apiService.get<UserNotificationResponse>(`/notifications/${notificationId}`);
  },

  /**
   * Get count of unread notifications
   */
  getUnreadCount: () => {
    return apiService.get<number>('/notifications/unread/count');
  },

  /**
   * Mark a specific notification as read
   */
  markAsRead: (notificationId: string) => {
    return apiService.patch(`/notifications/${notificationId}/read`, {});
  },

  /**
   * Mark all notifications as read
   * Returns count of notifications marked
   */
  markAllAsRead: () => {
    return apiService.patch<number>('/notifications/mark-all-read', {});
  },

  /**
   * Delete a notification (if backend supports it)
   * Note: Check if backend implements this endpoint
   */
  deleteNotification: (notificationId: string) => {
    return apiService.delete<void>(`/notifications/${notificationId}`);
  },

  /**
   * Bulk mark as read (if backend supports it)
   * Note: Check if backend implements this endpoint
   */
  bulkMarkAsRead: (notificationIds: string[]) => {
    return apiService.post<void>('/notifications/bulk/read', { notificationIds });
  },
};
