# Notification REST API Integration Guide

## Overview

The frontend now includes complete REST API integration for notification management. This complements the existing SSE (Server-Sent Events) system for real-time notifications.

**Architecture:**
- **SSE Streaming** (`/notifications/stream/{clientId}`): Real-time notification delivery
- **REST API** (`/notifications`): Notification history, read status, and management
- **Combined UX**: Real-time alerts + persistent notification storage

---

## File Structure

### New Files Created

```
src/
├── services/
│   └── notificationRestService.ts      # REST API client
├── hooks/
│   └── useNotificationsRest.ts        # TanStack Query hooks
└── views/
    └── notifications/
        └── Notifications.tsx           # Updated with pagination & REST API
```

### Modified Files

- `src/services/index.ts` - Added exports for REST service
- `src/hooks/index.ts` - Added exports for REST hooks
- `src/services/api.ts` - Added `patch()` method for REST API

---

## Service Layer: `notificationRestService.ts`

REST client wrapping backend notification endpoints.

### Methods

```typescript
// List notifications (paginated)
notificationRestService.listNotifications(params?: {
  page?: number;
  size?: number;
  sort?: string;
})
// Returns: NotificationListResponse

// Get single notification
notificationRestService.getNotification(notificationId: string)
// Returns: UserNotificationResponse

// Get unread count
notificationRestService.getUnreadCount()
// Returns: number

// Mark notification as read
notificationRestService.markAsRead(notificationId: string)
// Returns: void

// Mark all as read
notificationRestService.markAllAsRead()
// Returns: number (count of marked notifications)

// Delete notification
notificationRestService.deleteNotification(notificationId: string)
// Returns: void
```

### Usage Example

```typescript
import { notificationRestService } from '@/services';

// List first page
const data = await notificationRestService.listNotifications({
  page: 0,
  size: 10,
  sort: 'createdAt,desc'
});

console.log(data.notifications);  // Array of notifications
console.log(data.totalPages);     // Total pages available

// Get unread count
const count = await notificationRestService.getUnreadCount();
console.log(`${count} unread notifications`);

// Mark as read
await notificationRestService.markAsRead(notificationId);
```

---

## Hook Layer: `useNotificationsRest.ts`

TanStack Query hooks for React component integration.

### Hooks Available

#### 1. `useNotificationsRest(page, size, sort)`
Fetch paginated notifications.

```typescript
const { data, isLoading, error } = useNotificationsRest(0, 10);

// data structure:
// {
//   notifications: UserNotificationResponse[],
//   totalElements: number,
//   totalPages: number,
//   currentPage: number,
//   pageSize: number,
//   hasNextPage: boolean,
//   isFirstPage: boolean,
//   isLastPage: boolean
// }
```

#### 2. `useUnreadCount()`
Fetch and auto-refresh unread count.

```typescript
const { data: unreadCount } = useUnreadCount();
console.log(`${unreadCount} unread`);
```

#### 3. `useNotification(notificationId, enabled)`
Fetch specific notification.

```typescript
const { data: notification } = useNotification(id, id !== null);
```

#### 4. `useMarkNotificationAsRead()`
Mutation to mark notification as read.

```typescript
const { mutate: markAsRead } = useMarkNotificationAsRead();

// When notification is clicked:
markAsRead(notificationId);
```

#### 5. `useMarkAllNotificationsAsRead()`
Mutation to mark all as read.

```typescript
const { mutate: markAllRead } = useMarkAllNotificationsAsRead();

// Mark all:
markAllRead();
```

#### 6. `useNotificationsWithUnread(page, size)` (Convenience Hook)
Combined hook providing both notifications and unread count.

```typescript
const { 
  notifications, 
  unreadCount, 
  totalPages, 
  hasNextPage, 
  isLoading 
} = useNotificationsWithUnread(0, 10);
```

---

## Data Types

### `UserNotificationResponse`
```typescript
interface UserNotificationResponse {
  id: string;                                    // UUID
  notificationId: string;                       // UUID
  peopleId: string;                            // UUID
  title: string;                               // Notification title
  body: string;                                // Message content
  type: 'EVENT' | 'MINISTRY' | 'ADMINISTRATIVE';
  channel: 'APP' | 'EMAIL' | 'WHATSAPP';
  template: string;                           // Template used
  variables: Record<string, any>;             // Template variables
  status: 'PENDING' | 'SENT' | 'READ' | 'FAILED';
  readAt: string | null;                      // ISO-8601 timestamp
  createdAt: string;                          // ISO-8601 timestamp
  updatedAt: string;                          // ISO-8601 timestamp
}
```

### `NotificationListResponse`
```typescript
interface NotificationListResponse {
  notifications: UserNotificationResponse[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNextPage: boolean;
  isFirstPage: boolean;
  isLastPage: boolean;
}
```

---

## Usage Examples

### Example 1: Display Paginated Notifications List

```typescript
import { useNotificationsRest } from '@/hooks';

export function NotificationsList() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useNotificationsRest(page, 10);

  if (isLoading) return <Spinner />;

  return (
    <div>
      {data?.notifications.map(notif => (
        <NotificationItem key={notif.id} notification={notif} />
      ))}
      
      {data && (
        <Pagination
          currentPage={data.currentPage}
          totalPages={data.totalPages}
          onPageChange={setPage}
        />
      )}
    </div>
  );
}
```

### Example 2: Mark as Read on Click

```typescript
import { useMarkNotificationAsRead } from '@/hooks';

export function NotificationItem({ notification }) {
  const { mutate: markAsRead } = useMarkNotificationAsRead();

  const handleClick = () => {
    markAsRead(notification.id);
    // Automatically refetches list after marking as read
  };

  return (
    <div 
      onClick={handleClick}
      className={notification.status === 'READ' ? 'opacity-50' : ''}
    >
      {notification.title}
    </div>
  );
}
```

### Example 3: Show Unread Badge

```typescript
import { useUnreadCount } from '@/hooks';

export function NotificationBell() {
  const { data: unreadCount } = useUnreadCount();

  return (
    <button className="relative">
      <BellIcon />
      {unreadCount > 0 && (
        <Badge>{unreadCount > 99 ? '99+' : unreadCount}</Badge>
      )}
    </button>
  );
}
```

### Example 4: Mark All as Read Button

```typescript
import { useMarkAllNotificationsAsRead } from '@/hooks';

export function MarkAllReadButton() {
  const { mutate: markAllRead, isPending } = useMarkAllNotificationsAsRead();

  return (
    <button 
      onClick={() => markAllRead()}
      disabled={isPending}
    >
      Mark All as Read
    </button>
  );
}
```

---

## Updated Notifications Page

The `/notifications` page has been completely rebuilt with:

✅ **Pagination Controls**
- Previous/Next buttons
- Page size selector (5, 10, 20, 50 per page)
- Current page indicator

✅ **Type Filtering**
- EVENT
- MINISTRY
- ADMINISTRATIVE
- ALL (default)

✅ **Status Filtering**
- SENT
- READ
- PENDING
- FAILED
- ALL (default)

✅ **Read Status Management**
- Mark individual notifications as read
- Mark all as read at once
- Visual indication of read/unread status (opacity)

✅ **Real-time Unread Count**
- Shows in header
- Auto-refreshes every 30 seconds
- Updates after marking as read

✅ **Enhanced UX**
- Loading states
- Error handling
- Empty state messages
- Responsive design
- Notification metadata (channel, type, timestamps)

---

## Query Caching Strategy

TanStack Query automatically caches results:

```typescript
// Cache configuration in useNotificationsRest
{
  staleTime: 30000,      // Data considered fresh for 30 seconds
  gcTime: 5 * 60 * 1000, // Keep cache for 5 minutes
}

// Unread count auto-refetches every 30 seconds
{
  refetchInterval: 30000,
}
```

### Manual Invalidation

After mutations, queries are automatically invalidated:

```typescript
// When marking notification as read:
queryClient.invalidateQueries({ queryKey: ['notifications:rest'] });
queryClient.invalidateQueries({ queryKey: ['notifications:unread:count'] });
// This refetches both lists and counts
```

---

## Integration with SSE

**Current Architecture:**

1. **Real-time Notifications (SSE)**
   - Backend streams notifications via `/stream/{clientId}`
   - Frontend receives in real-time
   - Displays as toasts and bell icon
   - Stored in memory (last 50)

2. **Persistent Notifications (REST)**
   - Backend stores all notifications in database
   - Frontend fetches via paginated `/notifications` endpoint
   - Full history available
   - Read/unread status tracked

**Best Practice:**
- Use SSE for immediate alerts and toasts
- Use REST API for the notifications listing page
- Combine both for comprehensive notification experience

---

## Error Handling

The API service automatically wraps errors. Handle them in components:

```typescript
const { data, error, isError } = useNotificationsRest(0, 10);

if (isError) {
  return (
    <ErrorBox>
      <p>Failed to load notifications</p>
      <small>{error.message}</small>
    </ErrorBox>
  );
}
```

---

## Performance Optimization

### Pagination
Always use pagination to limit data:
```typescript
// ✅ Good
useNotificationsRest(0, 10);  // Fetch 10 items

// ❌ Bad
useNotificationsRest(0, 1000); // Don't fetch everything
```

### Lazy Loading
Load notifications only when user navigates  to the page:
```typescript
const { enabled } = useUserAuth();
useNotificationsRest(page, 10, enabled ? 'createdAt,desc' : undefined);
```

### Selective Refetching
Refetch only what changed:
```typescript
const { mutate: markAsRead } = useMarkNotificationAsRead();
// Automatically refetches notifications and unread count
// No need for manual refetch
```

---

## Testing

```typescript
describe('useNotificationsRest', () => {
  it('should fetch paginated notifications', async () => {
    const { result } = renderHook(() => useNotificationsRest(0, 10));
    
    await waitFor(() => {
      expect(result.current.data?.notifications).toHaveLength(10);
      expect(result.current.data?.totalPages).toBeGreaterThan(0);
    });
  });

  it('should mark notification as read', async () => {
    const { result } = renderHook(() => useMarkNotificationAsRead());
    
    act(() => {
      result.current.mutate('notification-id');
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });
  });
});
```

---

## Troubleshooting

### Notifications not updating
- Check browser console for API errors
- Verify `VITE_API_URL` environment variable is correct
- Ensure JWT token is valid and not expired
- Check that backend `/notifications` endpoint is implemented

### Unread count not refreshing
- Clear browser cache
- Check Network tab in DevTools for API calls
- Verify `refetchInterval` is configured (default: 30s)

### Pagination not working
- Ensure backend implements correct `page` and `size` parameters
- Verify your backend returns proper `totalPages` in response
- Check sort parameter format matches backend expectations

---

## Next Steps

1. **Verify Backend**
   - Ensure all 5 notification endpoints are implemented
   - Test with Postman/curl before frontend integration
   - Check authentication and authorization

2. **Customize UI**
   - Adjust pagination page size
   - Modify filter options
   - Match your design system

3. **Add Features**
   - Notification preferences/settings
   - Delete notifications (if backend supports)
   - Bulk actions
   - Search/filter
   - Rich notification content

---

## API Reference

See the backend Notification API documentation for:
- Detailed endpoint specifications
- Request/response examples
- Error codes
- Authentication requirements
- Rate limiting

