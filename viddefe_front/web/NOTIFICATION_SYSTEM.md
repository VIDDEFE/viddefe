# SSE Notification System - Implementation Guide

## Overview
This implementation adds Real-time Server-Sent Events (SSE) notification streaming to the VIDDEFE frontend. After a user authenticates, the system automatically connects to the backend's notification stream and displays incoming notifications as toast messages and in a notification bell dropdown.

## Architecture

### Components & Files Created

```
src/
├── services/
│   └── notificationService.ts        # SSE connection handler & listener management
├── hooks/
│   ├── useNotifications.ts           # React hook for notification management
│   └── useSSEConnection.ts           # Auto-connect hook based on auth state
├── components/shared/
│   ├── NotificationToast.tsx         # Auto toast display for notifications
│   └── NotificationBell.tsx          # Notification bell with dropdown
```

## How It Works

### 1. **SSE Connection Flow**
```
User Logs In
    ↓
Layout Component Mounts
    ↓
useSSEConnection() Hook Runs
    ↓
Extracts user.id → Calls notificationService.connectToStream(userId)
    ↓
EventSource connects to: GET /notifications/stream/{userId}
    ↓
Backend sends notifications via SSE
    ↓
Notifications received & displayed as toast + bell
```

### 2. **Service Layer** (`notificationService.ts`)

- **Singleton Pattern**: `notificationService` manages a single EventSource connection
- **Listener Pattern**: Subscribers can listen to notification events
- **Auto-reconnection**: Automatically reconnects if connection drops
- **Connection Management**: `connect()`, `disconnect()`, `subscribe()` methods

**Key Methods:**
```typescript
connectToStream(clientId: string) // Start SSE connection
disconnect()                        // Stop SSE connection
subscribe(listener)                 // Listen to notifications
isConnected()                       // Check connection status
getStatus()                         // Get connection status
```

### 3. **Hook Layer** (`useNotifications.ts`)

Provides React state management for notifications:
```typescript
const { 
  notifications,        // Array of all received notifications
  lastNotification,     // Most recent notification
  isConnected,         // Boolean: is SSE connected
  connectionStatus,    // 'connected' | 'connecting' | 'disconnected'
  connect,             // Manual connect function
  disconnect,          // Manual disconnect function
  clearNotifications   // Clear all notifications
} = useNotifications();
```

### 4. **Auto-Connection Hook** (`useSSEConnection.ts`)

- Called in `Layout.tsx` 
- Automatically connects when `user` is not null
- Automatically disconnects when `user` is null
- Handles authentication lifecycle

```typescript
// In Layout.tsx
export default function Layout() {
  useSSEConnection(); // ← Automatic SSE management
  // ...
}
```

### 5. **UI Components**

#### **NotificationToast** (`components/shared/NotificationToast.tsx`)
- Automatically displays notifications as toast messages
- Uses `sonner` library for toast UI
- Maps notification types to toast types (success, error, warning, info)
- Custom icons support
- Added to App.tsx

#### **NotificationBell** (`components/shared/NotificationBell.tsx`)
- Bell icon with unread count badge
- Dropdown list of recent notifications
- Shows connection status
- Clear all functionality
- Time-relative display ("5m ago", "2h ago", etc.)
- Added to Navbar.tsx

## Usage Examples

### Display a Toast Notification
```typescript
// Backend sends SSE message with this structure:
{
  id: "notif-123",
  title: "Group Meeting",
  message: "Your group meeting starts in 15 minutes",
  type: "info",
  icon: "🔔",
  timestamp: "2026-03-14T10:30:00Z"
}

// Frontend automatically shows as toast + in bell
```

### Manual SSE Connection
```typescript
import { useNotifications } from '@/hooks';

function MyComponent() {
  const { connect, disconnect, notifications } = useNotifications();

  // Manual connection
  const handleConnect = () => connect('user-123');
  
  // Listen to notifications
  console.log(notifications); // All notifications
  
  return (
    <button onClick={handleConnect}>Connect</button>
  );
}
```

### Listen to Notification in Any Component
```typescript
import { useNotifications } from '@/hooks';

function Dashboard() {
  const { lastNotification } = useNotifications();

  useEffect(() => {
    if (lastNotification?.type === 'success') {
      // Do something when success notification arrives
      console.log('New success notification!', lastNotification.message);
    }
  }, [lastNotification]);

  return <div>Dashboard</div>;
}
```

### Direct Service Access
```typescript
import { notificationService } from '@/services/notificationService';

// Manual subscription
const unsubscribe = notificationService.subscribe((notification) => {
  console.log('New notification:', notification);
});

// Connect
notificationService.connectToStream('user-123');

// Check status
console.log(notificationService.isConnected()); // true/false
console.log(notificationService.getStatus());   // 'connected' | 'connecting' | 'disconnected'

// Cleanup
unsubscribe();
notificationService.disconnect();
```

## Backend Integration

### Expected Endpoint
```
GET /notifications/stream/{clientId}

Headers:
  Authorization: Bearer {token}
  Accept: text/event-stream

Response:
  Content-Type: text/event-stream
  
  data: {"id":"...", "title":"...", "message":"...", ...}
```

### Example Java/Spring Boot Backend

```java
@GetMapping("/stream/{clientId}")
public SseEmitter stream(@PathVariable String clientId, HttpServletRequest request) {
  SseEmitter emitter = new SseEmitter();
  registerEmitter(clientId, emitter);
  
  emitter.onCompletion(() -> removeEmitter(clientId));
  emitter.onTimeout(() -> removeEmitter(clientId));
  
  return emitter;
}

// Send notification
public void broadcastNotification(NotificationEvent notification) {
  SseEmitter emitter = getEmitterForUser(notification.userId);
  emitter.send(SseEmitter.event()
    .id(notification.id)
    .name("notification")
    .data(notification)
    .build());
}
```

## Browser Compatibility

- **Chrome/Edge**: ✅ Full support
- **Firefox**: ✅ Full support
- **Safari**: ✅ Full support
- **IE11**: ❌ Not supported (use polyfill or fallback)

## Troubleshooting

### Connection Not Establishing

1. **Check user is logged in**: 
   ```typescript
   const { user } = useAppContext();
   console.log('User:', user); // Should not be null
   ```

2. **Check API URL**:
   ```typescript
   // Browser DevTools → Network tab
   // Look for SSE request to: http://localhost:8080/api/v1/notifications/stream/{userId}
   ```

3. **Check backend CORS** (if frontend and backend on different origins):
   ```java
   // Spring Boot
   @CrossOrigin(origins = "*", allowedHeaders = "*")
   @GetMapping("/stream/{clientId}")
   public SseEmitter stream(@PathVariable String clientId) { ... }
   ```

### Notifications Not Showing

1. **Check console for errors**:
   ```typescript
   // Open browser DevTools → Console tab
   // Look for error messages from notificationService
   ```

2. **Verify notification structure**:
   ```typescript
   // Backend must send exactly:
   {
     id: string,
     title: string,
     message: string,
     type: 'info' | 'success' | 'error' | 'warning',
     icon?: string,
     timestamp: string
   }
   ```

3. **Check Sonner Toaster** exists:
   ```tsx
   // App.tsx should have:
   <Toaster richColors position="top-right" />
   <NotificationToast />
   ```

### Connection Drops & Reconnects

- **Automatic**: The service attempts to reconnect after 5 seconds
- **Manual**: Call `connect(userId)` again in your component

```typescript
const { connect } = useNotifications();

useEffect(() => {
  connect('user-123');
}, [connect]);
```

## Performance Notes

- **Memory**: Keeps last 50 notifications in memory
- **CPU**: Minimal - only processes incoming messages
- **Network**: One persistent SSE connection per user
- **Re-renders**: Only when new notifications arrive

## Security Considerations

1. **Authentication**: SSE endpoint must verify auth token
2. **User Isolation**: Backend must only send notifications to subscribed user
3. **Input Validation**: Validate notification data before displaying

```typescript
// Example validation
const isValidNotification = (data: any): data is NotificationEvent => {
  return (
    data.id && 
    data.message && 
    ['info', 'success', 'error', 'warning'].includes(data.type) &&
    data.timestamp
  );
};
```

## Future Enhancements

- [ ] Persistent notification history (database)
- [ ] Notification preferences/settings
- [ ] Rich notification templates
- [ ] Sound/desktop notifications
- [ ] Notification actions/buttons
- [ ] Read/unread status tracking
- [ ] Notification categories/filtering
- [ ] Offline queue with sync
