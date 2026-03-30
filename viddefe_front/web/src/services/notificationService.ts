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

/**
 * Parses a plain text message from SSE and extracts notification data
 * Handles messages like: "Hola, se ha actualizado la reunión de templo. La nueva fecha de la reunión es 2026-03-31T21:44Z. ¡No te lo pierdas!."
 */
function parsePlainTextMessage(data: string): NotificationEvent {
  const isoDateRegex = /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(?::\d{2})?Z?/;
  const dateMatch = data.match(isoDateRegex);
  const eventDate = dateMatch ? new Date(dateMatch[0]) : new Date();
  
  // Detect entity type and create appropriate title
  let title = 'Notificación';
  let icon = 'ℹ️';
  let type: 'info' | 'success' | 'error' | 'warning' = 'info';
  
  const lowerData = data.toLowerCase();
  
  if (lowerData.includes('reunión') || lowerData.includes('worship')) {
    title = '🙏 Actualización de Reunión';
    type = 'info';
  } else if (lowerData.includes('grupo') || lowerData.includes('group')) {
    title = '👥 Actualización de Grupo';
    type = 'info';
  } else if (lowerData.includes('ofrendas') || lowerData.includes('offering')) {
    title = '💰 Actualización de Ofrendas';
    type = 'success';
  } else if (lowerData.includes('error') || lowerData.includes('problema')) {
    title = '⚠️ Error';
    type = 'error';
  } else if (lowerData.includes('actualizado') || lowerData.includes('updated')) {
    title = '✅ Se ha actualizado';
    type = 'success';
  }
  
  return {
    id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
    title,
    message: data,
    type,
    icon,
    timestamp: eventDate.toISOString(),
  };
}

/**
 * Attempts to parse event data as JSON, falls back to plain text parsing
 */
function parseEventData(data: string): NotificationEvent {
  try {
    // First, try to parse as JSON
    const parsed = JSON.parse(data);
    
    // Validate that it has the required NotificationEvent properties
    if (parsed.id && parsed.title && parsed.message && parsed.type && parsed.timestamp) {
      return parsed as NotificationEvent;
    }
    
    // If JSON is missing required fields, treat as plain text
    throw new Error('Invalid NotificationEvent structure');
  } catch (e) {
    // If JSON parsing fails or structure is invalid, treat as plain text
    console.log('📝 Parsing as plain text message');
    return parsePlainTextMessage(data);
  }
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
          const notification: NotificationEvent = parseEventData(event.data);
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
