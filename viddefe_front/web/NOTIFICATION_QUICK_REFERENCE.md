# 🔔 SSE Notification System - Quick Reference Card

## Auto-Enabled Features
Everything works automatically once user logs in! ✨

```
Login → Layout Mounts → useSSEConnection → Connects to SSE → Notifications Flow
```

## Visual Components Locations

### 🔔 Notification Bell
**Location:** Top-right navbar (Navbar.tsx)
- Shows unread count
- Click to see dropdown
- Displays recent notifications
- Shows connection status

### 🍞 Toast Notifications
**Location:** Top-right corner (via Sonner)
- Auto-appears when notification arrives
- Shows for 3-5 seconds
- Type-specific styling

### 📋 Full Notifications Page
**Location:** Left sidebar → "Notifications"
**Route:** `/notifications`
- View all notifications
- Filter by type
- Clear all
- Search (future)

---

## API Endpoint

```
GET /api/v1/notifications/stream/{clientId}

Parameters:
  clientId = user.id (from AppContext)

Headers:
  Authorization: Bearer {token}
  Accept: text/event-stream

Response:
  Content-Type: text/event-stream
  Transfer-Encoding: chunked
  
  data: {
    "id": "notif-123",
    "title": "Alert Title",
    "message": "What happened",
    "type": "success|info|error|warning",
    "icon": "✅",
    "timestamp": "2026-03-14T10:30:00Z"
  }
```

---

## Notification Types & Icons

| Type | Icon | Toast Color | Background |
|------|------|-------------|-----------|
| `success` | ✓ | Green | bg-green-50 |
| `error` | ✗ | Red | bg-red-50 |
| `warning` | ⚠ | Yellow | bg-yellow-50 |
| `info` | ℹ | Blue | bg-blue-50 |

---

## Code Examples

### Use in Component
```typescript
import { useNotifications } from '@/hooks';

function MyComponent() {
  const { 
    notifications,        // Array of all notifications
    lastNotification,     // Most recent one
    isConnected,         // Boolean
    connectionStatus,    // 'connected' | 'connecting' | 'disconnected'
    clearNotifications   // Function
  } = useNotifications();

  return <div>{notifications.length} notifications</div>;
}
```

### Direct Service Access
```typescript
import { notificationService } from '@/services/notificationService';

// Subscribe
const unsubscribe = notificationService.subscribe(notification => {
  console.log('Notification:', notification);
});

// Connect
notificationService.connectToStream('user-123');

// Check status
console.log(notificationService.isConnected());
console.log(notificationService.getStatus());

// Cleanup
unsubscribe();
notificationService.disconnect();
```

---

## Files Reference

**Core:**
- `src/services/notificationService.ts` - SSE handler
- `src/hooks/useNotifications.ts` - React hook
- `src/hooks/useSSEConnection.ts` - Auto-connection

**UI:**
- `src/components/shared/NotificationBell.tsx` - Bell icon
- `src/components/shared/NotificationToast.tsx` - Toast auto-display
- `src/views/notifications/Notifications.tsx` - Full page

**Integration Points:**
- `src/App.tsx` - Added `<NotificationToast />`
- `src/components/layout/Layout.tsx` - Added `useSSEConnection()`
- `src/components/layout/Navbar.tsx` - Added `<NotificationBell />`

---

## Connection Lifecycle

```
User Logs In
  ├─ AppContext.user gets set
  └─ useSSEConnection hook runs
     └─ Calls: notificationService.connectToStream(user.id)
        └─ Opens: EventSource to GET /stream/{userId}
           └─ Backend starts sending notifications
              └─ NotificationToast displays each one
              └─ NotificationBell updates
              └─ Stored in memory (last 50)

User Logs Out
  ├─ AppContext.user becomes null
  └─ useSSEConnection hook detects change
     └─ Calls: notificationService.disconnect()
        └─ Closes: EventSource
           └─ Connection ends
```

---

## Debug Commands

```javascript
// Check if connected
console.log('Connected?', notificationService.isConnected());

// Get status
console.log('Status:', notificationService.getStatus());

// Check persisted notifications
// Via useNotifications hook in component:
const { notifications } = useNotifications();
console.log('Total notifications:', notifications.length);

// Check browser network tab
// Look for: GET /api/v1/notifications/stream/[userId]
// Should show: text/event-stream
```

---

## Common Issues & Fixes

| Issue | Cause | Fix |
|-------|-------|-----|
| No toast notifications | `<NotificationToast />` missing | Add to App.tsx |
| No bell icon | `<NotificationBell />` missing | Add to Navbar.tsx |
| Not connecting | User not logged in | Check `user` in AppContext |
| Connection drops | Network issue | Auto-reconnects in 5s |
| No notifications page | Route not added | Check router/index.tsx |
| Can't see endpoint calls | Endpoint wrong | Check: /api/v1/notifications/stream/{id} |

---

## Configuration

### Toast Duration (NotificationToast.tsx)
```typescript
case 'success':
  toast.success(msg, { duration: 4000 }); // Change here
```

### Toast Position (App.tsx)
```typescript
<Toaster position="top-right" /> {/* Change position */}
```

### Bell Dropdown Width (NotificationBell.tsx)
```typescript
<div className="w-96"> {/* Change to w-80, w-full, etc */}
```

### Max Notifications in Memory
```typescript
// In useNotifications.ts
setNotifications(prev => prev.slice(0, 50)); // Change 50
```

---

## Testing Backend Endpoint

### With curl
```bash
curl -N \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Accept: text/event-stream" \
  "http://localhost:8080/api/v1/notifications/stream/user-123"
```

### Expected Response
```
data: {"id":"1","message":"test","type":"info","timestamp":"2026-03-14T10:30:00Z"}

data: {"id":"2","message":"test2","type":"success","timestamp":"2026-03-14T10:31:00Z"}
```

---

## Most Important Rules

1. **✅ DO:**
   - Use `useSSEConnection()` in Layout (already done)
   - Send correct notification format from backend
   - Include all required fields in notification
   - Handle eventSource errors gracefully

2. **❌ DON'T:**
   - Manually call `connectToStream()` on every component render
   - Forget to set `Authorization: Bearer {token}` header
   - Send notifications for every action (make them valuable)
   - Forget to close eventSource on disconnect

---

## Performance Tips

- System keeps last 50 notifications in memory
- Only one EventSource connection per user
- Minimal re-renders (only on new notification)
- Component lifecycle properly managed
- Automatic cleanup on unmount/logout

---

## Next Steps

1. **Implement backend endpoint:** `GET /stream/{clientId}`
2. **Test with curl/Postman** first
3. **Verify notification format**
4. **Test in browser** - check Network tab
5. **Test all scenarios:**
   - Login → notifications flow
   - Logout → connection closes
   - Network drops → auto-reconnects
   - See toast notifications
   - See bell badge update
   - View `/notifications` page
   - Filter notifications

---

**Quick Links:**
- 📖 Full Docs: [NOTIFICATION_SYSTEM.md](NOTIFICATION_SYSTEM.md)
- 💻 Examples: [NOTIFICATION_EXAMPLES.ts](NOTIFICATION_EXAMPLES.ts)
- 📋 Summary: [NOTIFICATION_IMPLEMENTATION.md](NOTIFICATION_IMPLEMENTATION.md)

**Status:** ✅ Ready to Test with Backend
