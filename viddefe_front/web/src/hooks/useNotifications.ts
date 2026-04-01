import { useState, useEffect, useCallback } from 'react';
import { notificationService, type NotificationEvent } from '../services/notificationService';

interface UseNotificationsReturn {
  notifications: NotificationEvent[];
  lastNotification: NotificationEvent | null;
  isConnected: boolean;
  connectionStatus: 'connected' | 'connecting' | 'disconnected';
  connect: (clientId: string) => void;
  disconnect: () => void;
  clearNotifications: () => void;
}

/**
 * Hook to manage SSE notifications
 * Use this in a component that wraps your entire app to establish SSE connection
 * 
 * @example
 * const { notifications, isConnected } = useNotifications();
 * useEffect(() => {
 *   if (user?.id) {
 *     connect(user.id);
 *   }
 * }, [user?.id, connect]);
 */
export function useNotifications(): UseNotificationsReturn {
  const [notifications, setNotifications] = useState<NotificationEvent[]>([]);
  const [lastNotification, setLastNotification] = useState<NotificationEvent | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const [connectionStatus, setConnectionStatus] = useState<'connected' | 'connecting' | 'disconnected'>('disconnected');

  // Handle incoming notifications
  useEffect(() => {
    const unsubscribe = notificationService.subscribe((notification) => {
      // Add new notification
      setNotifications(prev => [notification, ...prev]);
      setLastNotification(notification);

      // Keep only last 50 notifications in memory
      setNotifications(prev => prev.slice(0, 50));
    });

    return () => {
      unsubscribe();
    };
  }, []);

  // Update connection status periodically
  useEffect(() => {
    const interval = setInterval(() => {
      const status = notificationService.getStatus();
      setConnectionStatus(status);
      setIsConnected(status === 'connected');
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const connect = useCallback((clientId: string) => {
    console.log('🎯 useNotifications: Connecting with clientId:', clientId);
    notificationService.connectToStream(clientId);
    setConnectionStatus('connecting');
  }, []);

  const disconnect = useCallback(() => {
    console.log('🎯 useNotifications: Disconnecting');
    notificationService.disconnect();
    setConnectionStatus('disconnected');
    setIsConnected(false);
  }, []);

  const clearNotifications = useCallback(() => {
    setNotifications([]);
    setLastNotification(null);
  }, []);

  return {
    notifications,
    lastNotification,
    isConnected,
    connectionStatus,
    connect,
    disconnect,
    clearNotifications,
  };
}
