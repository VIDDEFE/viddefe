import { useState, useMemo } from 'react';
import { PageHeader, Table } from '../../components/shared';
import { 
  useNotificationsRest, 
  useMarkNotificationAsRead, 
  useMarkAllNotificationsAsRead,
  useUnreadCount,
  useNotifications,
} from '../../hooks';
import { 
  FiBell, 
  FiFilter, 
  FiCheckCircle, 
  FiAlertTriangle, 
  FiInfo,
  FiLoader,
  FiEye
} from 'react-icons/fi';
import type { UserNotificationResponse } from '../../services/notificationRestService';
import type { NotificationEvent } from '../../services/notificationService';

type FilterType = 'all' | 'EVENT' | 'MINISTRY' | 'ADMINISTRATIVE';
type StatusFilter = 'all' | 'READ' | 'SENT' | 'PENDING' | 'FAILED';

interface CombinedNotification {
  id: string;
  title: string;
  message?: string;
  body?: string;
  type: 'EVENT' | 'MINISTRY' | 'ADMINISTRATIVE' | 'info' | 'success' | 'error' | 'warning';
  status?: 'READ' | 'SENT' | 'PENDING' | 'FAILED';
  channel?: string;
  timestamp: string;
  notificationId?: string;
  source: 'rest' | 'sse';
  original: UserNotificationResponse | NotificationEvent;
}

export default function NotificationsPage() {
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [typeFilter, setTypeFilter] = useState<FilterType>('all');
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('all');
  
  // Fetch data using REST API
  const { data, isLoading, error } = useNotificationsRest(currentPage, pageSize, 'createdAt,desc');
  const { data: unreadCount = 0 } = useUnreadCount();
  const { notifications: sseNotifications, isConnected } = useNotifications();
  
  // Mutations
  const { mutate: markAsRead, isPending: isMarkingRead } = useMarkNotificationAsRead();
  const { mutate: markAllRead, isPending: isMarkingAll } = useMarkAllNotificationsAsRead();

  const restNotifications = data?.notifications || [];

  // Combine SSE and REST notifications
  const combinedNotifications: CombinedNotification[] = useMemo(() => {
    const combined: CombinedNotification[] = [];

    // Add SSE notifications (they are real-time, so show them first)
    sseNotifications.forEach((sse) => {
      combined.push({
        id: sse.id,
        title: sse.title,
        message: sse.message,
        type: sse.type as any,
        status: 'SENT',
        channel: 'APP',
        timestamp: sse.timestamp,
        source: 'sse',
        original: sse,
      });
    });

    // Add REST API notifications (filter out duplicates)
    restNotifications.forEach((rest) => {
      const isDuplicate = combined.some(
        (c) => c.source === 'sse' && c.message === rest.body
      );
      if (!isDuplicate) {
        combined.push({
          id: rest.id,
          title: rest.title,
          body: rest.body,
          message: rest.message,
          type: rest.type,
          status: rest.status,
          channel: rest.channel,
          timestamp: rest.createdAt,
          notificationId: rest.notificationId,
          source: 'rest',
          original: rest,
        });
      }
    });

    return combined;
  }, [sseNotifications, restNotifications]);

  // Filter notifications
  const filteredNotifications = useMemo(() => {
    let result = combinedNotifications;
    
    if (typeFilter !== 'all') {
      result = result.filter(n => n.type === typeFilter);
    }
    
    if (statusFilter !== 'all') {
      result = result.filter(n => n.status === statusFilter);
    }
    
    return result;
  }, [combinedNotifications, typeFilter, statusFilter]);

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'EVENT':
        return 'text-blue-600';
      case 'MINISTRY':
        return 'text-purple-600';
      case 'ADMINISTRATIVE':
        return 'text-green-600';
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
      case 'success':
        return <FiCheckCircle className="w-5 h-5" />;
      case 'error':
        return <FiAlertTriangle className="w-5 h-5" />;
      case 'warning':
        return <FiAlertTriangle className="w-5 h-5" />;
      case 'info':
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

  const typeFilters: Array<{ key: FilterType; label: string }> = [
    { key: 'all', label: 'Todas' },
    { key: 'EVENT', label: 'Eventos' },
    { key: 'MINISTRY', label: 'Ministerio' },
    { key: 'ADMINISTRATIVE', label: 'Admin' },
  ];

  const statusFilters: Array<{ key: StatusFilter; label: string }> = [
    { key: 'all', label: 'Todas' },
    { key: 'SENT', label: 'Enviado' },
    { key: 'READ', label: 'Leído' },
    { key: 'PENDING', label: 'Pendiente' },
    { key: 'FAILED', label: 'Error' },
  ];

  // Columnas para la tabla
  const columns = useMemo(() => [
    {
      key: 'title' as const,
      label: 'Título',
      priority: 1,
      render: (_value: CombinedNotification[keyof CombinedNotification], item: CombinedNotification) => (
        <div className="flex items-center gap-2">
          <div className={getTypeColor(item.type)}>
            {getTypeIcon(item.type)}
          </div>
          <div>
            <p className="font-medium text-gray-900">{item.title}</p>
            <p className="text-sm text-gray-600 line-clamp-1">
              {item.message || item.body}
            </p>
          </div>
        </div>
      ),
    },
    {
      key: 'type' as const,
      label: 'Tipo',
      priority: 3,
      render: (value: CombinedNotification[keyof CombinedNotification]) => (
        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
          {String(value)}
        </span>
      ),
    },
    {
      key: 'status' as const,
      label: 'Estado',
      priority: 2,
      render: (value: CombinedNotification[keyof CombinedNotification]) => (
        <span
          className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(
            String(value || 'SENT')
          )}`}
        >
          {String(value || 'SENT')}
        </span>
      ),
    },
    {
      key: 'timestamp' as const,
      label: 'Fecha',
      priority: 4,
      render: (value: CombinedNotification[keyof CombinedNotification]) => (
        <span className="text-sm text-gray-500">
          {formatTime(String(value))}
        </span>
      ),
    },
    {
      key: 'channel' as const,
      label: 'Canal',
      priority: 5,
      render: (value: CombinedNotification[keyof CombinedNotification]) => (
        <span className="text-sm text-gray-600">
          {String(value || 'APP')}
        </span>
      ),
    },
    {
      key: 'id' as const,
      label: 'Acciones',
      priority: 6,
      render: (_value: CombinedNotification[keyof CombinedNotification], item: CombinedNotification) => (
        <div className="flex items-center gap-2">
          {item.source === 'rest' && item.status !== 'READ' && (
            <button
              type="button"
              onClick={() => markAsRead(item.notificationId || item.id)}
              disabled={isMarkingRead}
              className="p-1.5 text-gray-500 hover:text-green-600 hover:bg-green-50 rounded transition-colors disabled:opacity-50"
              title="Marcar como leído"
            >
              <FiEye size={14} />
            </button>
          )}
          {item.source === 'sse' && (
            <span className="inline-flex items-center px-1.5 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-700">
              🔴 Live
            </span>
          )}
        </div>
      ),
    },
  ], [markAsRead, isMarkingRead]);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div>
            <PageHeader
              title="Notificaciones"
              subtitle={`Gestiona todas tus notificaciones (${filteredNotifications.length} mostradas)`}
            />
          </div>
          {/* SSE Connection Status Indicator */}
          <div className="flex items-center gap-2 px-3 py-2 rounded-lg bg-gray-50 border border-gray-200">
            <span
              className={`h-2 w-2 rounded-full ${
                isConnected
                  ? 'bg-green-500 animate-pulsingDot'
                  : 'bg-gray-400'
              }`}
            />
            <span className="text-xs font-medium text-gray-600">
              {isConnected ? 'Stream: Conectado' : 'Stream: Desconectado'}
            </span>
          </div>
        </div>
        {unreadCount > 0 && (
          <div className="text-right">
            <p className="text-sm font-medium text-gray-600">{unreadCount} No leídas</p>
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

          {filteredNotifications.length > 0 && (
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
              Marcar Todas como Leídas
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
              className={`px-3 py-1 text-xs rounded-full font-medium transition-colors ${
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
          <p className="text-red-700 font-medium">Error al cargar notificaciones</p>
          <p className="text-red-600 text-sm">{error.message || 'Intenta de nuevo más tarde'}</p>
        </div>
      )}

      {/* Table */}
      <Table<CombinedNotification>
        data={filteredNotifications}
        columns={columns}
        loading={isLoading}
        pagination={{
          mode: 'manual',
          currentPage,
          totalPages: data?.totalPages || 1,
          totalElements: data?.totalElements || 0,
          pageSize,
          onPageChange: setCurrentPage,
          onPageSizeChange: (size) => {
            setPageSize(size);
            setCurrentPage(0);
          },
        }}
      />

      {/* Info Box */}
      <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <h4 className="font-semibold text-blue-900 mb-2">
          💡 Acerca de Notificaciones
        </h4>
        <ul className="text-sm text-blue-800 space-y-1">
          <li>
            • <strong>Notificaciones SSE (🔴 Live)</strong>: Notificaciones en tiempo real
            del stream. Aparecen en la parte superior.
          </li>
          <li>
            • <strong>Notificaciones REST</strong>: Notificaciones almacenadas en la
            base de datos, con paginación.
          </li>
          <li>• Usa los filtros para organizar notificaciones por tipo y estado</li>
          <li>
            • Marca como leída para registrar que has visto la notificación (solo API)
          </li>
          <li>• Haz clic en "Marcar Todas como Leídas" para limpiar rápidamente</li>
          <li>
            • Estado de Conexión: Muestra si tu conexión en tiempo real está activa
          </li>
        </ul>
      </div>
    </div>
  );
}
