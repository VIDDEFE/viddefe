import { apiService } from './api';

// Definición de permisos disponibles
export type PermissionKey = 
  // Iglesias
  | 'CHURCH_CREATE'
  | 'CHURCH_VIEW'
  | 'CHURCH_EDIT'
  | 'CHURCH_DELETE'
  // Personas
  | 'PERSON_CREATE'
  | 'PERSON_VIEW'
  | 'PERSON_EDIT'
  | 'PERSON_DELETE'
  // Usuarios
  | 'USER_CREATE'
  | 'USER_VIEW'
  | 'USER_EDIT'
  | 'USER_DELETE';

export interface Permission {
  key: PermissionKey;
  label: string;
  description: string;
  category: 'churches' | 'people' | 'users';
}

// Permisos organizados por categoría
export const AVAILABLE_PERMISSIONS: Permission[] = [
  // Iglesias
  { key: 'CHURCH_CREATE', label: 'Crear iglesias', description: 'Permite crear nuevas iglesias', category: 'churches' },
  { key: 'CHURCH_VIEW', label: 'Ver iglesias', description: 'Permite ver información de iglesias', category: 'churches' },
  { key: 'CHURCH_EDIT', label: 'Editar iglesias', description: 'Permite editar iglesias existentes', category: 'churches' },
  { key: 'CHURCH_DELETE', label: 'Eliminar iglesias', description: 'Permite eliminar iglesias', category: 'churches' },
  // Personas
  { key: 'PERSON_CREATE', label: 'Crear personas', description: 'Permite crear nuevos registros de personas', category: 'people' },
  { key: 'PERSON_VIEW', label: 'Ver personas', description: 'Permite ver información de personas', category: 'people' },
  { key: 'PERSON_EDIT', label: 'Editar personas', description: 'Permite editar registros de personas', category: 'people' },
  { key: 'PERSON_DELETE', label: 'Eliminar personas', description: 'Permite eliminar registros de personas', category: 'people' },
  // Usuarios
  { key: 'USER_CREATE', label: 'Crear usuarios', description: 'Permite crear usuarios del sistema', category: 'users' },
  { key: 'USER_VIEW', label: 'Ver usuarios', description: 'Permite ver usuarios del sistema', category: 'users' },
  { key: 'USER_EDIT', label: 'Editar usuarios', description: 'Permite editar usuarios del sistema', category: 'users' },
  { key: 'USER_DELETE', label: 'Eliminar usuarios', description: 'Permite eliminar usuarios del sistema', category: 'users' },
];

export const PERMISSION_CATEGORIES = {
  churches: { label: 'Iglesias', icon: '⛪' },
  people: { label: 'Personas', icon: '👥' },
  users: { label: 'Usuarios', icon: '🔐' },
};

export interface CreateUserRequest {
  peopleId: string;
  email: string;
  roleId: number;
  permissions: PermissionKey[];
}

export interface UserResponse {
  id: string;
  email: string;
  peopleId: string;
  roleId: number;
  permissions: PermissionKey[];
  createdAt: string;
}

export interface Role {
  id: number;
  name: string;
}

// Roles disponibles en el sistema
export const AVAILABLE_ROLES: Role[] = [
  { id: 1, name: 'Administrador' },
  { id: 2, name: 'Usuario' },
  { id: 3, name: 'Pastor' },
];

export const userService = {
  create: (data: CreateUserRequest) =>
    apiService.post<UserResponse>('/users', data),

  getById: (id: string) =>
    apiService.get<UserResponse>(`/users/${id}`),

  delete: (id: string) =>
    apiService.delete(`/users/${id}`),
};
