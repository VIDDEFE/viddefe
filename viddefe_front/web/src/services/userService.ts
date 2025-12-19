import { apiService } from './api';

// Definición de permisos disponibles (basados en el backend)
export type PermissionKey = 
  // Personas
  | 'PEOPLE_ADD_PEOPLE'
  | 'PEOPLE_VIEW_PEOPLE'
  | 'PEOPLE_EDIT_PEOPLE'
  | 'PEOPLE_DELETE_PEOPLE'
  // Iglesias - Hijos
  | 'CHURCH_ADD_CHILDREN'
  | 'CHURCH_VIEW_CHILDREN'
  | 'CHURCH_EDIT_CHILDREN'
  | 'CHURCH_DELETE_CHILDREN';

export interface Permission {
  key: PermissionKey;
  label: string;
  description: string;
  category: 'churches' | 'people';
}

// Respuesta del backend para permisos
export interface PermissionResponse {
  name: string;
}

// Mapeo de permisos para la UI (fallback si el endpoint falla)
export const PERMISSION_MAP: Record<PermissionKey, { label: string; description: string; category: 'churches' | 'people' }> = {
  // Personas
  PEOPLE_ADD_PEOPLE: { label: 'Agregar personas', description: 'Permite agregar nuevos registros de personas', category: 'people' },
  PEOPLE_VIEW_PEOPLE: { label: 'Ver personas', description: 'Permite ver información de personas', category: 'people' },
  PEOPLE_EDIT_PEOPLE: { label: 'Editar personas', description: 'Permite editar registros de personas', category: 'people' },
  PEOPLE_DELETE_PEOPLE: { label: 'Eliminar personas', description: 'Permite eliminar registros de personas', category: 'people' },
  // Iglesias - Hijos
  CHURCH_ADD_CHILDREN: { label: 'Agregar hijos', description: 'Permite agregar iglesias hijas', category: 'churches' },
  CHURCH_VIEW_CHILDREN: { label: 'Ver hijos', description: 'Permite ver iglesias hijas', category: 'churches' },
  CHURCH_EDIT_CHILDREN: { label: 'Editar hijos', description: 'Permite editar iglesias hijas', category: 'churches' },
  CHURCH_DELETE_CHILDREN: { label: 'Eliminar hijos', description: 'Permite eliminar iglesias hijas', category: 'churches' },
};

// Construir lista de permisos desde el mapa
export const AVAILABLE_PERMISSIONS: Permission[] = Object.entries(PERMISSION_MAP).map(
  ([key, value]) => ({
    key: key as PermissionKey,
    ...value,
  })
);

export const PERMISSION_CATEGORIES = {
  people: { label: 'Personas', icon: '👥' },
  churches: { label: 'Iglesias', icon: '⛪' },
};

// Función para convertir nombre del backend a PermissionKey
export const parsePermissionName = (name: string): PermissionKey | null => {
  if (name in PERMISSION_MAP) {
    return name as PermissionKey;
  }
  return null;
};

// Request para enviar invitación
export interface InvitationRequest {
  email: string;
  personId: string;
  role: string;
  permissions: string[];
}

// Response de invitación
export interface InvitationResponse {
  id: string;
  email: string;
  personId: string;
  role: string;
  permissions: { name: string }[];
  status: string;
  createdAt: string;
}

export interface CreateUserRequest {
  peopleId: string;
  email: string;
  roleId: string;
  permissions: PermissionKey[];
}

export interface UserResponse {
  id: string;
  email: string;
  peopleId: string;
  roleId: string;
  permissions: PermissionKey[];
  createdAt: string;
}

export interface Role {
  id: string;
  name: string;
}

// Los roles ahora vendrán del backend, pero mantenemos un fallback
export const DEFAULT_ROLES: Role[] = [
  { id: '1', name: 'Administrador' },
  { id: '2', name: 'Usuario' },
  { id: '3', name: 'Pastor' },
];

export const userService = {
  // Obtener permisos disponibles del backend
  getPermissions: () =>
    apiService.get<PermissionResponse[]>('/auth/permissions'),

  // Enviar invitación para crear usuario
  sendInvitation: (data: InvitationRequest) =>
  {
    console.log('Sending invitation with data:', data);
    return apiService.post<InvitationResponse>('/auth/account/invitations', data);
  },
  // Métodos legacy (por si los necesitas)
  create: (data: CreateUserRequest) =>
    apiService.post<UserResponse>('/users', data),

  getById: (id: string) =>
    apiService.get<UserResponse>(`/users/${id}`),

  delete: (id: string) =>
    apiService.delete(`/users/${id}`),
};
