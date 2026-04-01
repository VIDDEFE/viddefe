import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationRestService, type PaginationParams } from '../services/notificationRestService';

/**
 * Hook for fetching notifications via REST API with pagination
 * Use this for the notifications listing page
 *
 * @param page - Current page number (0-indexed)
 * @param size - Items per page
 * @param sort - Sort criteria (e.g., "createdAt,desc")
 * @returns Query state with notifications data and pagination info
 *
 * @example
 * const { data, isLoading, error, isPreviousData } = useNotificationsRest(0, 10);
 * // data contains: notifications[], totalPages, hasNextPage, etc.
 */
export function useNotificationsRest(page: number = 0, size: number = 10, sort: string = 'createdAt,desc') {
  const params: PaginationParams = { page, size, sort };
  
  return useQuery({
    queryKey: ['notifications:rest', page, size, sort],
    queryFn: () => notificationRestService.listNotifications(params),
    staleTime: 30000, // Stale for 30 seconds
    gcTime: 5 * 60 * 1000, // Cache for 5 minutes
  });
}

/**
 * Hook for fetching unread notification count
 *
 * @returns Query state with unread count
 *
 * @example
 * const { data: unreadCount } = useUnreadCount();
 * console.log(`You have ${unreadCount} unread notifications`);
 */
export function useUnreadCount() {
  return useQuery({
    queryKey: ['notifications:unread:count'],
    queryFn: () => notificationRestService.getUnreadCount(),
    refetchInterval: 30000, // Refetch every 30 seconds
    staleTime: 10000, // Stale for 10 seconds
  });
}

/**
 * Hook for fetching a specific notification
 *
 * @param notificationId - ID of notification to fetch
 * @param enabled - Whether to run the query
 * @returns Query state with notification data
 *
 * @example
 * const { data: notification } = useNotification(id, id !== null);
 */
export function useNotification(notificationId: string | null, enabled: boolean = true) {
  return useQuery({
    queryKey: ['notifications:rest', notificationId],
    queryFn: () => notificationRestService.getNotification(notificationId as string),
    enabled: enabled && notificationId !== null,
    staleTime: 1 * 60 * 1000, // Stale for 1 minute
  });
}

/**
 * Hook for marking a notification as read
 *
 * @returns Mutation function and state
 *
 * @example
 * const { mutate: markAsRead } = useMarkNotificationAsRead();
 * markAsRead(notificationId);
 */
export function useMarkNotificationAsRead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (notificationId: string) => notificationRestService.markAsRead(notificationId),
    onSuccess: () => {
      // Invalidate relevant queries to refetch
      queryClient.invalidateQueries({ queryKey: ['notifications:rest'] });
      queryClient.invalidateQueries({ queryKey: ['notifications:unread:count'] });
    },
  });
}

/**
 * Hook for marking all notifications as read
 *
 * @returns Mutation function and state
 *
 * @example
 * const { mutate: markAllRead } = useMarkAllNotificationsAsRead();
 * markAllRead();
 */
export function useMarkAllNotificationsAsRead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => notificationRestService.markAllAsRead(),
    onSuccess: () => {
      // Invalidate all notification queries
      queryClient.invalidateQueries({ queryKey: ['notifications:rest'] });
      queryClient.invalidateQueries({ queryKey: ['notifications:unread:count'] });
    },
  });
}

/**
 * Hook for deleting a notification (if supported by backend)
 *
 * @returns Mutation function and state
 */
export function useDeleteNotification() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (notificationId: string) => notificationRestService.deleteNotification(notificationId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications:rest'] });
    },
  });
}

/**
 * Hook for combined notifications state (REST + unread count)
 * Useful for components that need both list and unread count
 *
 * @param page - Current page
 * @param size - Items per page
 * @returns Combined state with notifications and unread count
 *
 * @example
 * const { notifications, unreadCount, isLoading } = useNotificationsWithUnread(0, 10);
 */
export function useNotificationsWithUnread(page: number = 0, size: number = 10) {
  const notificationsList = useNotificationsRest(page, size);
  const unreadCount = useUnreadCount();

  return {
    notifications: notificationsList.data?.notifications || [],
    unreadCount: unreadCount.data || 0,
    totalElements: notificationsList.data?.totalElements || 0,
    totalPages: notificationsList.data?.totalPages || 0,
    currentPage: notificationsList.data?.currentPage || page,
    pageSize: notificationsList.data?.pageSize || size,
    hasNextPage: notificationsList.data?.hasNextPage || false,
    isFirstPage: notificationsList.data?.isFirstPage || true,
    isLastPage: notificationsList.data?.isLastPage || false,
    isLoading: notificationsList.isLoading || unreadCount.isLoading,
    error: notificationsList.error || unreadCount.error,
  };
}
