// SSE notification interfaces
export interface Notification {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'error' | 'warning';
  icon?: string;
  timestamp: string;
  read: boolean;
}

export interface NotificationEvent {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'error' | 'warning';
  icon?: string;
  timestamp: string;
}

// SSE subscription manager
class NotificationServiceImpl {
  private eventSource: EventSource | null = null;
  private clientId: string | null = null;
  private listeners: ((notification: NotificationEvent) => void)[] = [];
  private isConnecting = false;

  /**
   * Connect to SSE stream
   * @param clientId - Unique client identifier (typically user_id)
   */
  connectToStream(clientId: string): void {
    // Prevent duplicate connections
    if (this.eventSource && this.clientId === clientId) {
      console.log('SSE already connected for this client');
      return;
    }

    // Close existing connection if any
    if (this.eventSource) {
      this.disconnect();
    }

    if (this.isConnecting) {
      console.log('SSE connection in progress...');
      return;
    }

    this.isConnecting = true;
    this.clientId = clientId;

    try {
      const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';
      const sseUrl = `${apiUrl}/stream/${clientId}`;
      
      console.log('🔌 Connecting to SSE stream:', sseUrl);
      
      this.eventSource = new EventSource(sseUrl, {withCredentials: true});

      // Handle incoming messages
      this.eventSource.onmessage = (event) => {
        try {
          const notification: NotificationEvent = JSON.parse(event.data);
          console.log('📬 Notification received:', notification);
          this.notifyListeners(notification);
        } catch (error) {
          console.error('Error parsing notification event:', error);
        }
      };

      // Handle connection open
      this.eventSource.onopen = () => {
        console.log('✅ SSE stream connected');
        this.isConnecting = false;
      };

      // Handle errors
      this.eventSource.onerror = (error) => {
        console.error('❌ SSE stream error:', error);
        this.isConnecting = false;
        
        // Attempt to reconnect after delay
        if (this.eventSource?.readyState === EventSource.CLOSED) {
          console.log('⏳ SSE stream closed, will attempt reconnection...');
          setTimeout(() => {
            if (this.clientId && !this.eventSource) {
              console.log('🔄 Attempting to reconnect...');
              this.connectToStream(this.clientId);
            }
          }, 5000); // Retry after 5 seconds
        }
      };
    } catch (error) {
      console.error('Failed to establish SSE connection:', error);
      this.isConnecting = false;
    }
  }

  /**
   * Disconnect from SSE stream
   */
  disconnect(): void {
    if (this.eventSource) {
      console.log('🔌 Disconnecting from SSE stream');
      this.eventSource.close();
      this.eventSource = null;
    }
    this.clientId = null;
    this.isConnecting = false;
  }

  /**
   * Subscribe to notification events
   * @param listener - Callback function to handle notifications
   * @returns Unsubscribe function
   */
  subscribe(listener: (notification: NotificationEvent) => void): () => void {
    this.listeners.push(listener);
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }

  /**
   * Notify all listeners
   */
  private notifyListeners(notification: NotificationEvent): void {
    this.listeners.forEach(listener => {
      try {
        listener(notification);
      } catch (error) {
        console.error('Error in notification listener:', error);
      }
    });
  }

  /**
   * Check if connected
   */
  isConnected(): boolean {
    return this.eventSource !== null && this.eventSource.readyState === EventSource.OPEN;
  }

  /**
   * Get connection status
   */
  getStatus(): 'connected' | 'connecting' | 'disconnected' {
    if (this.isConnecting) return 'connecting';
    if (this.isConnected()) return 'connected';
    return 'disconnected';
  }
}

export const notificationService = new NotificationServiceImpl();
