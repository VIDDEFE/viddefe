import React, { useState, useMemo } from 'react';
import { PageHeader } from '../../components/shared';
import { useNotifications } from '../../hooks';
import { Trash2, Filter, Bell, CheckCircle2, AlertCircle, AlertTriangle, Info } from 'lucide-react';
import type { NotificationEvent } from '../../services/notificationService';

type FilterType = 'all' | 'info' | 'success' | 'error' | 'warning';

export default function NotificationsPage() {
  const { notifications, clearNotifications } = useNotifications();
  const [filter, setFilter] = useState<FilterType>('all');

  // Filter notifications
  const filteredNotifications = useMemo(() => {
    if (filter === 'all') return notifications;
    return notifications.filter(n => n.type === filter);
  }, [notifications, filter]);

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

  const getTypeIcon = (type: string, customIcon?: string) => {
    if (customIcon) {
      return <span className="text-lg">{customIcon}</span>;
    }

    switch (type) {
      case 'success':
        return <CheckCircle2 className="w-5 h-5" />;
      case 'error':
        return <AlertCircle className="w-5 h-5" />;
      case 'warning':
        return <AlertTriangle className="w-5 h-5" />;
      case 'info':
      default:
        return <Info className="w-5 h-5" />;
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
    
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const countByType = {
    all: notifications.length,
    info: notifications.filter(n => n.type === 'info').length,
    success: notifications.filter(n => n.type === 'success').length,
    warning: notifications.filter(n => n.type === 'warning').length,
    error: notifications.filter(n => n.type === 'error').length,
  };

  const filters: Array<{ key: FilterType; label: string; count: number }> = [
    { key: 'all', label: 'All', count: countByType.all },
    { key: 'info', label: 'Info', count: countByType.info },
    { key: 'success', label: 'Success', count: countByType.success },
    { key: 'warning', label: 'Warnings', count: countByType.warning },
    { key: 'error', label: 'Errors', count: countByType.error },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <PageHeader
        title="Notifications"
        subtitle="View and manage all your notifications"
      />

      {/* Filter Tabs */}
      <div className="flex flex-wrap gap-2 pb-4 border-b">
        {filters.map(f => (
          <button
            key={f.key}
            onClick={() => setFilter(f.key)}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
              filter === f.key
                ? 'bg-blue-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            <Filter className="w-4 h-4" />
            {f.label}
            {f.count > 0 && (
              <span className={`ml-2 px-2 py-1 rounded-full text-xs font-bold ${
                filter === f.key ? 'bg-blue-800' : 'bg-gray-300'
              }`}>
                {f.count}
              </span>
            )}
          </button>
        ))}

        {notifications.length > 0 && (
          <button
            onClick={clearNotifications}
            className="ml-auto flex items-center gap-2 px-4 py-2 rounded-lg bg-red-100 text-red-700 hover:bg-red-200 transition-colors font-medium"
          >
            <Trash2 className="w-4 h-4" />
            Clear All
          </button>
        )}
      </div>

      {/* Notifications List */}
      <div className="space-y-4">
        {filteredNotifications.length === 0 ? (
          <div className="text-center py-12">
            <Bell className="w-16 h-16 mx-auto mb-4 text-gray-300" />
            <p className="text-gray-500 text-lg">
              {notifications.length === 0 
                ? 'No notifications yet' 
                : `No ${filter} notifications`}
            </p>
          </div>
        ) : (
          filteredNotifications.map((notification: NotificationEvent) => (
            <div
              key={notification.id}
              className={`p-4 rounded-lg border-l-4 ${getTypeBgColor(
                notification.type
              )} border-l-4 border-current`}
            >
              <div className="flex gap-4">
                {/* Icon */}
                <div className={`flex-shrink-0 ${getTypeColor(notification.type)}`}>
                  {getTypeIcon(notification.type, notification.icon)}
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1">
                      {notification.title && (
                        <h3 className="font-semibold text-gray-900">
                          {notification.title}
                        </h3>
                      )}
                      <p className="text-gray-700 mt-1">
                        {notification.message}
                      </p>
                    </div>
                  </div>

                  <p className="text-sm text-gray-500 mt-2">
                    {formatTime(notification.timestamp)}
                  </p>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Info Box */}
      <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <h4 className="font-semibold text-blue-900 mb-2">💡 About Notifications</h4>
        <ul className="text-sm text-blue-800 space-y-1">
          <li>• New notifications appear here in real-time</li>
          <li>• You can also see recent notifications in the bell icon (top-right)</li>
          <li>• System notifications will also appear as toast messages</li>
          <li>• Click "Clear All" to remove all notifications</li>
        </ul>
      </div>
    </div>
  );
}
