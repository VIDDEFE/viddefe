import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiBell, FiTrash2, FiLoader } from 'react-icons/fi';
import { useNotifications } from '../../hooks/useNotifications';
import { useMarkNotificationAsRead, useNotificationsRest, useUnreadCount } from '../../hooks/useNotificationsRest';
import type { NotificationEvent } from '../../services/notificationService';
import type { UserNotificationResponse } from '../../services/notificationRestService';

export const NotificationBell: React.FC = () => {
  const navigate = useNavigate();
  const { notifications, isConnected, clearNotifications } = useNotifications();
  const [displayPage, setDisplayPage] = useState(0);
  const [itemsPerPage] = useState(10);
  const [hasMore, setHasMore] = useState(true);
  const sentinelRef = useRef<HTMLDivElement>(null);
  const { data: restNotifications } = useNotificationsRest(0, 50);
  const { data: unreadCountData } = useUnreadCount();
  const { mutate: markAsRead } = useMarkNotificationAsRead();
  
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const unreadCount = notifications.length;

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }
  }, [isOpen]);

  // Combine SSE notifications with REST API notifications
  const allNotifications = [
    ...notifications,
    ...(restNotifications?.notifications?.filter(
      (restNotif) =>
        !notifications.some((sseNotif) => sseNotif.id === restNotif.id)
    ) ?? []),
  ];

  // Infinite scroll: load more notifications
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore) {
          const totalExpected = (displayPage + 1) * itemsPerPage;
          if (totalExpected < allNotifications.length) {
            setDisplayPage((prev) => prev + 1);
          } else if (totalExpected >= allNotifications.length) {
            setHasMore(false);
          }
        }
      },
      { threshold: 0.1 }
    );

    if (sentinelRef.current) {
      observer.observe(sentinelRef.current);
    }

    return () => observer.disconnect();
  }, [displayPage, hasMore, allNotifications.length, itemsPerPage]);

  // Reset pagination when notifications change
  useEffect(() => {
    setDisplayPage(0);
    setHasMore(true);
  }, [notifications.length, restNotifications?.notifications?.length]);

  // Get paginated notifications
  const paginatedNotifications = allNotifications.slice(0, (displayPage + 1) * itemsPerPage);

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'success':
        return 'text-green-600';
      case 'error':
        return 'text-red-600';
      case 'warning':
        return 'text-yellow-600';
      case 'info':
      default:
        return 'text-blue-600';
    }
  };

  const getTypeBgColor = (type: string) => {
    switch (type) {
      case 'success':
        return 'bg-green-50';
      case 'error':
        return 'bg-red-50';
      case 'warning':
        return 'bg-yellow-50';
      case 'info':
      default:
        return 'bg-blue-50';
    }
  };

  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    
    return date.toLocaleDateString();
  };

  const handleNotificationClick = (notification: NotificationEvent | UserNotificationResponse) => {
    // If it's a REST API notification, mark it as read via PATCH
    if ('notificationId' in notification) {
      markAsRead(notification.notificationId);
    }
  };

  const isSSENotification = (notif: any): notif is NotificationEvent => {
    return 'title' in notif && 'message' in notif && !('notificationId' in notif);
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors"
        title="Notifications"
      >
        <FiBell className="w-5 h-5" />
        
        {/* Unread count badge */}
        {unreadCount > 0 && (
          <span className="absolute top-1 right-1 flex items-center justify-center h-5 w-5 bg-red-500 text-white text-xs font-bold rounded-full">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}

        {/* Pulsing connection indicator - shows when SSE is connected */}
        {isConnected && (
          <span className="absolute -top-1 -right-1 flex h-3 w-3 bg-red-500 rounded-full animate-pulsingDot" />
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-96 bg-white border border-gray-200 rounded-lg shadow-lg z-50 max-h-96 flex flex-col">
          <div className="flex items-center justify-between p-4 border-b">
            <h3 className="font-semibold text-gray-900">Notifications</h3>
            {unreadCount > 0 && (
              <button
                onClick={clearNotifications}
                className="flex items-center gap-1 text-xs text-gray-500 hover:text-gray-700 transition-colors"
                title="Clear all notifications"
              >
                <FiTrash2 className="w-4 h-4" />
                Clear
              </button>
            )}
          </div>

          <div className="px-3 py-2 text-xs border-b">
            <div className="flex items-center gap-2">
              <div
                className={`h-2 w-2 rounded-full ${
                  isConnected ? 'bg-green-500 animate-pulsingDot' : 'bg-gray-400'
                }`}
              />
              <span className="text-gray-600">
                {isConnected ? 'Connected to stream' : 'Disconnected from stream'}
              </span>
              <span className="text-gray-500">
                ({unreadCountData ?? 0} unread)
              </span>
            </div>
          </div>

          <div className="flex-1 overflow-y-auto">
            {allNotifications.length === 0 ? (
              <div className="p-8 text-center text-gray-500">
                <FiBell className="w-12 h-12 mx-auto mb-2 opacity-20" />
                <p>No notifications yet</p>
              </div>
            ) : (
              <div className="divide-y">
                {paginatedNotifications.map((notification: NotificationEvent | UserNotificationResponse) => (
                  <div
                    key={notification.id}
                    onClick={() => handleNotificationClick(notification)}
                    className={`p-3 cursor-pointer hover:bg-gray-50 transition-colors ${
                      isSSENotification(notification)
                        ? getTypeBgColor(notification.type)
                        : 'bg-blue-50'
                    }`}
                  >
                    <div className="flex gap-2">
                      <div
                        className={`shrink-0 mt-1 ${
                          isSSENotification(notification)
                            ? getTypeColor(notification.type)
                            : 'text-blue-600'
                        }`}
                      >
                        {isSSENotification(notification) ? (
                          notification.icon ? (
                            <span className="text-lg">{notification.icon}</span>
                          ) : (
                            <FiBell className="w-4 h-4" />
                          )
                        ) : (
                          <FiBell className="w-4 h-4" />
                        )}
                      </div>

                      <div className="flex-1 min-w-0">
                        {isSSENotification(notification) ? (
                          <>
                            {notification.title && (
                              <p className="font-medium text-gray-900">
                                {notification.title}
                              </p>
                            )}
                            <p className="text-sm text-gray-600 line-clamp-2">
                              {notification.message}
                            </p>
                          </>
                        ) : (
                          <>
                            <p className="font-medium text-gray-900">
                              {notification.title}
                            </p>
                            <p className="text-sm text-gray-600 line-clamp-2">
                              {notification.message}
                            </p>
                            {notification.status === 'READ' && (
                              <span className="inline-block mt-1 text-xs bg-green-100 text-green-800 px-2 py-0.5 rounded">
                                ✓ Read
                              </span>
                            )}
                          </>
                        )}
                        <p className="text-xs text-gray-400 mt-1">
                          {formatTime(
                            isSSENotification(notification)
                              ? notification.timestamp
                              : notification.createdAt
                          )}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}

                {/* Infinite scroll sentinel */}
                {allNotifications.length > paginatedNotifications.length && (
                  <div ref={sentinelRef} className="p-4 text-center">
                    <FiLoader className="w-4 h-4 mx-auto animate-spin text-gray-400" />
                  </div>
                )}
              </div>
            )}
          </div>

          {allNotifications.length > 0 && (
            <div className="p-3 border-t text-center">
              <button
                onClick={() => {
                  setIsOpen(false);
                  navigate('/notifications');
                }}
                className="text-sm text-blue-600 hover:text-blue-700 font-medium"
              >
                View all notifications
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
