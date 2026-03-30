import { useState, useMemo } from 'react';
import { PageHeader } from '../../components/shared';
import { 
  useNotificationsRest, 
  useMarkNotificationAsRead, 
  useMarkAllNotificationsAsRead,
  useUnreadCount 
} from '../../hooks';
import { 
  FiBell, 
  FiFilter, 
  FiCheckCircle, 
  FiAlertTriangle, 
  FiInfo,
  FiChevronLeft,
  FiChevronRight,
  FiLoader
} from 'react-icons/fi';
import type { UserNotificationResponse } from '../../services/notificationRestService';

type FilterType = 'all' | 'EVENT' | 'MINISTRY' | 'ADMINISTRATIVE';
type StatusFilter = 'all' | 'READ' | 'SENT' | 'PENDING' | 'FAILED';

interface NotificationWithRead extends UserNotificationResponse {
  isUnread?: boolean;
}

export default function NotificationsPage() {
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [typeFilter, setTypeFilter] = useState<FilterType>('all');
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('all');
  
  // Fetch data using REST API
  const { data, isLoading, error } = useNotificationsRest(currentPage, pageSize, 'createdAt,desc');
  const { data: unreadCount = 0 } = useUnreadCount();
  
  // Mutations
  const { mutate: markAsRead, isPending: isMarkingRead } = useMarkNotificationAsRead();
  const { mutate: markAllRead, isPending: isMarkingAll } = useMarkAllNotificationsAsRead();

  const notifications = data?.notifications || [];

  // Filter notifications
  const filteredNotifications = useMemo(() => {
    let result = notifications;
    
    if (typeFilter !== 'all') {
      result = result.filter(n => n.type === typeFilter);
    }
    
    if (statusFilter !== 'all') {
      result = result.filter(n => n.status === statusFilter);
    }
    
    return result;
  }, [notifications, typeFilter, statusFilter]);

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'EVENT':
        return 'text-blue-600';
      case 'MINISTRY':
        return 'text-purple-600';
      case 'ADMINISTRATIVE':
        return 'text-green-600';
      default:
        return 'text-gray-600';
    }
  };

  const getBorderColor = (type: string): string => {
    switch (type) {
      case 'EVENT':
        return '#2563eb';
      case 'MINISTRY':
        return '#9333ea';
      case 'ADMINISTRATIVE':
        return '#16a34a';
      default:
        return '#6b7280';
    }
  };

  const getTypeBgColor = (type: string) => {
    switch (type) {
      case 'EVENT':
        return 'bg-blue-50';
      case 'MINISTRY':
        return 'bg-purple-50';
      case 'ADMINISTRATIVE':
        return 'bg-green-50';
      default:
        return 'bg-gray-50';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'READ':
        return 'bg-green-100 text-green-800';
      case 'SENT':
        return 'bg-blue-100 text-blue-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'EVENT':
        return <FiBell className="w-5 h-5" />;
      case 'MINISTRY':
        return <FiCheckCircle className="w-5 h-5" />;
      case 'ADMINISTRATIVE':
        return <FiAlertTriangle className="w-5 h-5" />;
      default:
        return <FiInfo className="w-5 h-5" />;
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
    
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const typeFilters: Array<{ key: FilterType; label: string; count?: number }> = [
    { key: 'all', label: 'All' },
    { key: 'EVENT', label: 'Events' },
    { key: 'MINISTRY', label: 'Ministry' },
    { key: 'ADMINISTRATIVE', label: 'Admin' },
  ];

  const statusFilters: Array<{ key: StatusFilter; label: string; count?: number }> = [
    { key: 'all', label: 'All' },
    { key: 'SENT', label: 'Sent' },
    { key: 'READ', label: 'Read' },
    { key: 'PENDING', label: 'Pending' },
    { key: 'FAILED', label: 'Failed' },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <PageHeader
          title="Notifications"
          subtitle={`View and manage all your notifications (${data?.totalElements || 0} total)`}
        />
        {unreadCount > 0 && (
          <div className="text-right">
            <p className="text-sm font-medium text-gray-600">{unreadCount} Unread</p>
          </div>
        )}
      </div>

      {/* Type Filter Tabs */}
      <div className="space-y-4">
        <div className="flex flex-wrap gap-2 pb-4 border-b">
          {typeFilters.map(f => (
            <button
              key={f.key}
              onClick={() => {
                setTypeFilter(f.key);
                setCurrentPage(0);
              }}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
                typeFilter === f.key
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              <FiFilter className="w-4 h-4" />
              {f.label}
            </button>
          ))}

          {notifications.length > 0 && (
            <button
              onClick={() => markAllRead()}
              disabled={isMarkingAll}
              className="ml-auto flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-100 text-blue-700 hover:bg-blue-200 disabled:opacity-50 transition-colors font-medium"
            >
              {isMarkingAll ? (
                <FiLoader className="w-4 h-4 animate-spin" />
              ) : (
                <FiCheckCircle className="w-4 h-4" />
              )}
              Mark All Read
            </button>
          )}
        </div>

        {/* Status Filter Tabs */}
        <div className="flex flex-wrap gap-2 pb-4 border-b">
          {statusFilters.map(f => (
            <button
              key={f.key}
              onClick={() => {
                setStatusFilter(f.key);
                setCurrentPage(0);
              }}
              className={`px-3 py-1 text-sm rounded-full font-medium transition-colors ${
                statusFilter === f.key
                  ? 'bg-purple-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              {f.label}
            </button>
          ))}
        </div>
      </div>

      {/* Error State */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700 font-medium">Error loading notifications</p>
          <p className="text-red-600 text-sm">{error.message || 'Please try again later'}</p>
        </div>
      )}

      {/* Loading State */}
      {isLoading && (
        <div className="flex items-center justify-center py-12">
          <FiLoader className="w-8 h-8 text-blue-600 animate-spin" />
          <p className="ml-3 text-gray-600">Loading notifications...</p>
        </div>
      )}

      {/* Notifications List */}
      {!isLoading && filteredNotifications.length === 0 ? (
        <div className="text-center py-12">
          <FiBell className="w-16 h-16 mx-auto mb-4 text-gray-300" />
          <p className="text-gray-500 text-lg">
            {notifications.length === 0 
              ? 'No notifications yet' 
              : 'No notifications for selected filter'}
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredNotifications.map((notification: NotificationWithRead) => (
            <div
              key={notification.id}
              className={`p-4 rounded-lg border-l-4 transition-colors ${getTypeBgColor(
                notification.type
              )} ${notification.status === 'READ' ? 'opacity-75' : ''}`}
              style={{
                borderLeftColor: getBorderColor(notification.type)
              }}
            >
              <div className="flex gap-4">
                {/* Icon */}
                <div className={`shrink-0 mt-1 ${getTypeColor(notification.type)}`}>
                  {getTypeIcon(notification.type)}
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <h3 className="font-semibold text-gray-900">
                          {notification.title || notification?.body?.substring(0, 50)}
                        </h3>
                        <span className={`text-xs px-2 py-1 rounded-full font-medium ${getStatusColor(notification.status)}`}>
                          {notification.status}
                        </span>
                      </div>
                      {notification.title && (
                        <p className="text-gray-700 text-sm">
                          {notification.message}
                        </p>
                      )}
                    </div>

                    {/* Actions */}
                    {notification.status !== 'READ' && (
                      <button
                        onClick={() => markAsRead(notification.notificationId)}
                        disabled={isMarkingRead}
                        className="shrink-0 px-3 py-1 text-xs font-medium text-blue-600 hover:text-blue-700 disabled:opacity-50 transition-colors"
                        title="Mark as read"
                      >
                        {isMarkingRead ? '...' : 'Mark Read'}
                      </button>
                    )}
                  </div>

                  {/* Meta info */}
                  <div className="flex items-center justify-between mt-2">
                    <p className="text-xs text-gray-500">
                      {formatTime(notification.createdAt)}
                    </p>
                    <p className="text-xs text-gray-400">
                      {notification.channel} • {notification.type}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination Controls */}
      {!isLoading && data && data.totalPages > 1 && (
        <div className="flex items-center justify-between pt-6 border-t">
          <div className="text-sm text-gray-600">
            Page {(data.currentPage || 0) + 1} of {data.totalPages} ({data.totalElements} total)
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setCurrentPage(currentPage - 1)}
              disabled={data.isFirstPage || isLoading}
              className="flex items-center gap-2 px-4 py-2 rounded-lg bg-gray-100 text-gray-700 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              <FiChevronLeft className="w-4 h-4" />
              Previous
            </button>

            <div className="flex items-center gap-1">
              {Array.from({ length: Math.min(data.totalPages, 5) }).map((_, i) => {
                const pageNum = i;
                return (
                  <button
                    key={pageNum}
                    onClick={() => setCurrentPage(pageNum)}
                    className={`w-8 h-8 rounded-lg font-medium transition-colors ${
                      currentPage === pageNum
                        ? 'bg-blue-600 text-white'
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    }`}
                  >
                    {pageNum + 1}
                  </button>
                );
              })}
            </div>

            <button
              onClick={() => setCurrentPage(currentPage + 1)}
              disabled={data.isLastPage || isLoading}
              className="flex items-center gap-2 px-4 py-2 rounded-lg bg-gray-100 text-gray-700 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Next
              <FiChevronRight className="w-4 h-4" />
            </button>
          </div>

          <select
            value={pageSize}
            onChange={(e) => {
              setPageSize(Number(e.target.value));
              setCurrentPage(0);
            }}
            className="px-3 py-2 text-sm border border-gray-300 rounded-lg bg-white text-gray-700"
          >
            <option value={5}>5 per page</option>
            <option value={10}>10 per page</option>
            <option value={20}>20 per page</option>
            <option value={50}>50 per page</option>
          </select>
        </div>
      )}

      {/* Info Box */}
      <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <h4 className="font-semibold text-blue-900 mb-2">💡 About Notifications</h4>
        <ul className="text-sm text-blue-800 space-y-1">
          <li>• Notifications are stored in the database and persist across sessions</li>
          <li>• Real-time notifications appear as toasts and in the bell icon</li>
          <li>• Use filters to organize notifications by type and status</li>
          <li>• Mark notifications as read to keep track of what you've seen</li>
          <li>• Click "Mark All Read" to quickly clear the list</li>
        </ul>
      </div>
    </div>
  );
}