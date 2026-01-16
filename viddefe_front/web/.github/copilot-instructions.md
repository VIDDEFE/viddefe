# VIDDEFE Frontend - Copilot Instructions

## Project Overview
Church management system frontend built with **React 18 + TypeScript + Vite + TanStack Query + Tailwind CSS 4**. Backend is Spring Boot (Java) with paginated REST APIs.

## Architecture Layers

```
src/
├── services/     # API calls (axios) - one per entity (churchService, personService...)
├── hooks/        # TanStack Query wrappers - useChurches, usePeople, etc.
├── models/       # TypeScript interfaces matching backend DTOs
├── views/        # Page components (route targets)
├── components/   # Reusable UI (shared/) and feature-specific (churches/, groups/...)
├── context/      # AppContext for auth state & permissions
└── utils/        # Date/timezone helpers (critical for backend compatibility)
```

## Key Patterns

### Data Flow: Service → Hook → View
```typescript
// 1. Service (src/services/churchService.ts) - raw API calls
export const churchService = {
  getAll: (params?: PageableRequest) => apiService.get<Pageable<ChurchSummary>>(`/churches${queryParams}`),
};

// 2. Hook (src/hooks/useChurches.ts) - TanStack Query wrapper
export function useChurches(params?: PageableRequest) {
  return useQuery({ queryKey: ['churches', params], queryFn: () => churchService.getAll(params) });
}

// 3. View - consumes hook
const { data, isLoading } = useChurches({ page: 0, size: 10 });
```

### API Response Format (backend contract)
All API responses follow `ApiResponse<T>` wrapper - the interceptor in [api.ts](src/services/api.ts) unwraps `.data` automatically:
```typescript
// Backend returns: { success: true, data: T, message: string, timestamp: string }
// After interceptor: axios.get() returns just T
```

### Pagination (Spring Boot compatible)
```typescript
type PageableRequest = { page: number; size: number; sort?: { field: string; direction: 'asc' | 'desc' } };
type Pageable<T> = { content: T[]; totalElements: number; totalPages: number; number: number; size: number };
```

### Permissions System
Check permissions via `useAppContext()`:
```typescript
const { hasPermission } = useAppContext();
const canEdit = hasPermission(ChurchPermission.EDIT);
```

## Component Conventions

### Shared Components (src/components/shared/)
Always import from barrel: `import { Button, Modal, Table, Form, Input } from '../../components/shared';`

**Modal Pattern** - standard structure for CRUD modals:
```tsx
<Modal isOpen={isOpen} title="..." onClose={close} actions={<Button>Save</Button>}>
  <Form>{/* fields */}</Form>
</Modal>
```

**Table** supports both manual (backend) and auto (frontend) pagination/sorting:
```tsx
<Table data={items} columns={columns} pagination={{ mode: 'manual', currentPage, totalPages, onPageChange }} />
```

### Feature Components
Named `{Feature}FormModal.tsx`, `{Feature}DeleteModal.tsx`, `{Feature}ViewModal.tsx` for consistency.

## Critical: Date/Timezone Handling
Backend **requires** ISO-8601 with timezone offset. Use helpers from [utils/helpers.ts](src/utils/helpers.ts):
```typescript
// ALWAYS use these for backend communication:
toISOStringWithOffset("2026-01-15T10:00")  // → "2026-01-15T10:00:00-05:00"
fromUTCToLocalDatetime("2026-01-15T15:00:00Z")  // → "2026-01-15T10:00" (for inputs)
```

## Commands
```bash
npm run dev      # Start dev server (Vite)
npm run build    # TypeScript check + production build
npm run lint     # ESLint
```

## Environment
Set `VITE_API_URL` for backend (default: `http://localhost:8080/api/v1`)

## Naming Conventions
- Views: `PascalCase.tsx` in domain folders (`views/churches/Churches.tsx`)
- Hooks: `use{Entity}.ts` with CRUD operations (`useChurches`, `useCreateChurch`, `useDeleteChurch`)
- Services: `{entity}Service.ts` with object literal pattern
- Types: Define in `models/types.ts`, re-export from `models/index.ts`
