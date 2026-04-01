# Notification API Endpoints Documentation

## Overview
REST API endpoints for managing user notifications. All endpoints require JWT authentication via `access_token` cookie.

**Base URL:** `/notifications`

**Authentication:** JWT token in `access_token` cookie

---

## Endpoints

### 1. List User Notifications (Paginated)
**GET** `/notifications`

Retrieves all notifications for the authenticated user with pagination support.

#### Request Parameters
```
Query Parameters:
  - page: int (default: 0) - Page number (0-indexed)
  - size: int (default: 20) - Items per page
  - sort: String (optional) - Sort criteria (e.g., "createdAt,desc")

Cookie:
  - access_token: String (required) - JWT authorization token
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "id": "uuid",
        "notificationId": "uuid",
        "peopleId": "uuid",
        "title": "string",
        "body": "string",
        "type": "EVENT|MINISTRY|ADMINISTRATIVE",
        "channel": "APP|EMAIL|WHATSAPP",
        "template": "string",
        "variables": {},
        "status": "PENDING|SENT|READ|FAILED",
        "readAt": "2026-03-14T10:30:00Z",
        "createdAt": "2026-03-14T10:00:00Z",
        "updatedAt": "2026-03-14T10:30:00Z"
      }
    ],
    "totalElements": 42,
    "totalPages": 3,
    "currentPage": 0,
    "pageSize": 20,
    "hasNextPage": true,
    "isFirstPage": true,
    "isLastPage": false
  }
}
```

#### Example Request
```bash
curl -X GET "http://localhost:8080/notifications?page=0&size=10&sort=createdAt,desc" \
  -H "Cookie: access_token=your_jwt_token"
```

---

### 2. Get Specific Notification
**GET** `/notifications/{notificationId}`

Retrieves a single notification by ID (only if it belongs to the authenticated user).

#### Path Parameters
```
  - notificationId: UUID (required) - ID of the notification to retrieve
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "notificationId": "uuid",
    "peopleId": "uuid",
    "title": "string",
    "body": "string",
    "type": "EVENT|MINISTRY|ADMINISTRATIVE",
    "channel": "APP|EMAIL|WHATSAPP",
    "template": "string",
    "variables": {},
    "status": "PENDING|SENT|READ|FAILED",
    "readAt": "2026-03-14T10:30:00Z",
    "createdAt": "2026-03-14T10:00:00Z",
    "updatedAt": "2026-03-14T10:30:00Z"
  }
}
```

#### Example Request
```bash
curl -X GET "http://localhost:8080/notifications/550e8400-e29b-41d4-a716-446655440000" \
  -H "Cookie: access_token=your_jwt_token"
```

---

### 3. Get Unread Notification Count
**GET** `/notifications/unread/count`

Returns the count of unread notifications for the authenticated user.

#### Response (200 OK)
```json
{
  "success": true,
  "data": 5
}
```

#### Example Request
```bash
curl -X GET "http://localhost:8080/notifications/unread/count" \
  -H "Cookie: access_token=your_jwt_token"
```

---

### 4. Mark Notification as Read
**PATCH** `/notifications/{notificationId}/read`

Marks a specific notification as read (updates `readAt` timestamp and status to `READ`).

#### Path Parameters
```
  - notificationId: UUID (required) - ID of the notification to mark as read
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": null
}
```

#### Example Request
```bash
curl -X PATCH "http://localhost:8080/notifications/550e8400-e29b-41d4-a716-446655440000/read" \
  -H "Cookie: access_token=your_jwt_token" \
  -H "Content-Type: application/json"
```

---

### 5. Mark All Notifications as Read
**PATCH** `/notifications/mark-all-read`

Marks all unread notifications as read for the authenticated user.

#### Response (200 OK)
```json
{
  "success": true,
  "data": 3
}
```

Returns the count of notifications that were marked as read.

#### Example Request
```bash
curl -X PATCH "http://localhost:8080/notifications/mark-all-read" \
  -H "Cookie: access_token=your_jwt_token" \
  -H "Content-Type: application/json"
```

---

## Authentication

All endpoints require JWT authentication via the `access_token` cookie.

The JWT token must contain:
- `userId` - UUID of the authenticated user (extracted for filtering)
- Standard JWT claims (exp, iat, etc.)

**Example Cookie Header:**
```
Cookie: access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Error Responses

### 400 Bad Request
Invalid parameters or malformed request
```json
{
  "success": false,
  "error": "Invalid pagination parameters"
}
```

### 401 Unauthorized
Missing or invalid JWT token
```json
{
  "success": false,
  "error": "Unauthorized - Invalid token"
}
```

### 404 Not Found
Notification not found or doesn't belong to user
```json
{
  "success": false,
  "error": "Notification not found"
}
```

### 500 Internal Server Error
Server error occurred
```json
{
  "success": false,
  "error": "Internal server error"
}
```

---

## Data Types

### NotificationStatus
```
PENDING  - Notification created, not yet sent
SENT     - Notification sent to user
READ     - User has read the notification
FAILED   - Delivery failed
```

### NotificationType
```
EVENT            - Event-triggered notifications (meetings, attendance, groups)
MINISTRY         - Ministry-related notifications (announcements, assignments, prayers)
ADMINISTRATIVE   - System/admin notifications (password reset, verification, maintenance)
```

### ChannelType
```
APP      - In-app notification
EMAIL    - Email notification
WHATSAPP - WhatsApp message
```

---

## Pagination

The list endpoint supports Spring Data pagination:

- **page**: 0-indexed page number (default: 0)
- **size**: Number of items per page (default: 20)
- **sort**: Sort criteria in format `field,direction`
  - Example: `sort=createdAt,desc` (newest first)
  - Example: `sort=title,asc` (alphabetical)

#### Pagination Metadata in Response
```json
"totalElements": 42,      // Total number of notifications
"totalPages": 3,          // Total pages available
"currentPage": 0,         // Current page number
"pageSize": 20,           // Items per page
"hasNextPage": true,      // Whether more pages exist
"isFirstPage": true,      // Whether this is the first page
"isLastPage": false       // Whether this is the last page
```

---

## Usage Examples

### JavaScript/TypeScript (Fetch API)
```javascript
// List notifications
const response = await fetch('/notifications?page=0&size=10', {
  credentials: 'include'  // Include cookies
});
const data = await response.json();
console.log(data.data.notifications);

// Get unread count
const countResponse = await fetch('/notifications/unread/count', {
  credentials: 'include'
});
const count = await countResponse.json();
console.log(`Unread: ${count.data}`);

// Mark as read
await fetch('/notifications/{id}/read', {
  method: 'PATCH',
  credentials: 'include'
});

// Mark all as read
const result = await fetch('/notifications/mark-all-read', {
  method: 'PATCH',
  credentials: 'include'
});
const markedCount = await result.json();
console.log(`Marked ${markedCount.data} notifications as read`);
```

### Spring RestTemplate (Java Backend)
```java
// Get notifications
ResponseEntity<ApiResponse<NotificationListResponseDto>> response = 
  restTemplate.getForEntity(
    "http://api/notifications?page=0&size=20",
    ApiResponse.class
  );

NotificationListResponseDto data = response.getBody().getData();
List<UserNotificationResponseDto> notifications = data.getNotifications();

// Mark as read
restTemplate.patchForObject(
  "http://api/notifications/{id}/read",
  null,
  Void.class,
  notificationId
);

// Mark all as read
Integer markedCount = restTemplate.patchForObject(
  "http://api/notifications/mark-all-read",
  null,
  Integer.class
);
```

### Python (Requests)
```python
import requests

# List notifications
response = requests.get(
    'http://localhost:8080/notifications?page=0&size=10',
    cookies={'access_token': jwt_token}
)
data = response.json()['data']

# Get unread count
count_resp = requests.get(
    'http://localhost:8080/notifications/unread/count',
    cookies={'access_token': jwt_token}
)
unread_count = count_resp.json()['data']

# Mark notification as read
requests.patch(
    f'http://localhost:8080/notifications/{notification_id}/read',
    cookies={'access_token': jwt_token}
)

# Mark all as read
result = requests.patch(
    'http://localhost:8080/notifications/mark-all-read',
    cookies={'access_token': jwt_token}
)
marked = result.json()['data']
```

---

## Implementation Notes

- **User Filtering**: All endpoints automatically filter results by the authenticated user's ID extracted from the JWT token
- **Pagination Default**: If no pagination parameters provided, defaults to page 0, size 20
- **DTO Mapping**: `UserNotification` entities are automatically mapped to `UserNotificationResponseDto` with full notification details
- **Transactional**: All state-changing operations (mark as read) are transactional
- **Logging**: All operations include detailed logging for debugging and monitoring
- **Response Format**: All responses wrapped in `ApiResponse<T>` standard format

---

## Related Documentation

- **Notification Domain Models**: See `Notification.java`, `UserNotification.java`
- **Service Layer**: See `NotificationApplicationService.java`
- **Request/Response DTOs**: See `UserNotificationResponseDto.java`, `NotificationListResponseDto.java`
