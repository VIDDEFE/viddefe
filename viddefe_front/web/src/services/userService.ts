import { apiService } from './api';

// =============================================================================
// CONSTANTES DE PERMISOS (usando objetos const en lugar de enums)
// =============================================================================

/** Permisos relacionados con la gestión de personas */
export const PeoplePermission = {
  ADD: 'PEOPLE_ADD_PEOPLE',
  VIEW: 'PEOPLE_VIEW_PEOPLE',
  EDIT: 'PEOPLE_EDIT_PEOPLE',
  DELETE: 'PEOPLE_DELETE_PEOPLE',
} as const;

export type PeoplePermissionType = typeof PeoplePermission[keyof typeof PeoplePermission];

/** Permisos relacionados con la gestión de iglesias hijas */
export const ChurchPermission = {
  ADD: 'CHURCH_ADD_CHILDREN',
  VIEW: 'CHURCH_VIEW_CHILDREN',
  EDIT: 'CHURCH_EDIT_CHILDREN',
  DELETE: 'CHURCH_DELETE_CHILDREN',
} as const;

export type ChurchPermissionType = typeof ChurchPermission[keyof typeof ChurchPermission];

/** Permisos relacionados con la gestión de cultos */
export const WorshipPermission = {
  ADD: 'WORSHIP_ADD_MEETING',
  VIEW: 'WORSHIP_VIEW_MEETING',
  EDIT: 'WORSHIP_EDIT_MEETING',
  DELETE: 'WORSHIP_DELETE_MEETING',
} as const;

export type WorshipPermissionType = typeof WorshipPermission[keyof typeof WorshipPermission];

/** Permisos relacionados con la gestión de usuarios */
export const UserPermission = {
  INVITATION: 'INVITATION_PERMISSION',
  DELETE: 'DELETE_USER',
} as const;

export type UserPermissionType = typeof UserPermission[keyof typeof UserPermission];

/** Permisos relacionados con reuniones de grupos de hogar */
export const GroupMeetingPermission = {
  ADD: 'GROUP_MEETING_ADD',
  VIEW: 'GROUP_MEETING_VIEW',
  EDIT: 'GROUP_MEETING_EDIT',
  DELETE: 'GROUP_MEETING_DELETE',
} as const;

export type GroupMeetingPermissionType = typeof GroupMeetingPermission[keyof typeof GroupMeetingPermission];

/** Permisos relacionados con la gestión de grupos */
export const GroupPermission = {
  CREATE: 'CREATE_GROUP',
  VIEW: 'VIEW_GROUP',
  EDIT: 'EDIT_GROUP',
  DELETE: 'DELETE_GROUP',
} as const;

export type GroupPermissionType = typeof GroupPermission[keyof typeof GroupPermission];

// Tipo union de todos los permisos
export type PermissionKey = PeoplePermissionType | ChurchPermissionType | WorshipPermissionType | UserPermissionType | GroupMeetingPermissionType | GroupPermissionType;

// Array con todos los valores de permisos
export const ALL_PERMISSIONS = [
  ...Object.values(PeoplePermission),
  ...Object.values(ChurchPermission),
  ...Object.values(WorshipPermission),
  ...Object.values(UserPermission),
  ...Object.values(GroupMeetingPermission),
  ...Object.values(GroupPermission),
] as const;

export interface Permission {
  key: PermissionKey;
  label: string;
  description: string;
  category: 'churches' | 'people' | 'worships' | 'users' | 'group-meetings' | 'groups';
}

// Respuesta del backend para permisos
export interface PermissionResponse {
  name: string;
}

// Mapeo de permisos para la UI (fallback si el endpoint falla)
export const PERMISSION_MAP: Record<PermissionKey, { label: string; description: string; category: 'churches' | 'people' | 'worships' | 'users' | 'group-meetings' | 'groups' }> = {
  // Personas
  [PeoplePermission.ADD]: { label: 'Agregar personas', description: 'Permite agregar nuevos registros de personas', category: 'people' },
  [PeoplePermission.VIEW]: { label: 'Ver personas', description: 'Permite ver información de personas', category: 'people' },
  [PeoplePermission.EDIT]: { label: 'Editar personas', description: 'Permite editar registros de personas', category: 'people' },
  [PeoplePermission.DELETE]: { label: 'Eliminar personas', description: 'Permite eliminar registros de personas', category: 'people' },
  // Iglesias - Hijos
  [ChurchPermission.ADD]: { label: 'Agregar hijos', description: 'Permite agregar iglesias hijas', category: 'churches' },
  [ChurchPermission.VIEW]: { label: 'Ver hijos', description: 'Permite ver iglesias hijas', category: 'churches' },
  [ChurchPermission.EDIT]: { label: 'Editar hijos', description: 'Permite editar iglesias hijas', category: 'churches' },
  [ChurchPermission.DELETE]: { label: 'Eliminar hijos', description: 'Permite eliminar iglesias hijas', category: 'churches' },
  // Cultos
  [WorshipPermission.ADD]: { label: 'Agregar cultos', description: 'Permite agregar nuevos cultos', category: 'worships' },
  [WorshipPermission.VIEW]: { label: 'Ver cultos', description: 'Permite ver información de cultos', category: 'worships' },
  [WorshipPermission.EDIT]: { label: 'Editar cultos', description: 'Permite editar cultos', category: 'worships' },
  [WorshipPermission.DELETE]: { label: 'Eliminar cultos', description: 'Permite eliminar cultos', category: 'worships' },
  // Usuarios
  [UserPermission.INVITATION]: { label: 'Enviar invitaciones', description: 'Permite enviar invitaciones para crear usuarios', category: 'users' },
  [UserPermission.DELETE]: { label: 'Eliminar usuarios', description: 'Permite eliminar usuarios del sistema', category: 'users' },
  // Reuniones de grupos
  [GroupMeetingPermission.ADD]: { label: 'Agregar reuniones de grupo', description: 'Permite agregar nuevas reuniones de grupo', category: 'group-meetings' },
  [GroupMeetingPermission.VIEW]: { label: 'Ver reuniones de grupo', description: 'Permite ver reuniones de grupo', category: 'group-meetings' },
  [GroupMeetingPermission.EDIT]: { label: 'Editar reuniones de grupo', description: 'Permite editar reuniones de grupo', category: 'group-meetings' },
  [GroupMeetingPermission.DELETE]: { label: 'Eliminar reuniones de grupo', description: 'Permite eliminar reuniones de grupo', category: 'group-meetings' },
  // Grupos
  [GroupPermission.CREATE]: { label: 'Crear grupos', description: 'Permite crear nuevos grupos de hogar', category: 'groups' },
  [GroupPermission.VIEW]: { label: 'Ver grupos', description: 'Permite ver grupos de hogar', category: 'groups' },
  [GroupPermission.EDIT]: { label: 'Editar grupos', description: 'Permite editar grupos de hogar', category: 'groups' },
  [GroupPermission.DELETE]: { label: 'Eliminar grupos', description: 'Permite eliminar grupos de hogar', category: 'groups' },
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
  worships: { label: 'Cultos', icon: '🙏' },
  users: { label: 'Usuarios', icon: '🔐' },
  'group-meetings': { label: 'Reuniones de Grupo', icon: '📅' },
  groups: { label: 'Grupos', icon: '🏠' },
};

// Función para convertir nombre del backend a PermissionKey
export const parsePermissionName = (name: string): PermissionKey | null => {
  if (name in PERMISSION_MAP) {
    return name as PermissionKey;
  }
  return null;
};

// Función helper para verificar si un string es un permiso válido
export const isValidPermission = (value: string): value is PermissionKey => {
  return ALL_PERMISSIONS.includes(value as PermissionKey);
};

// Canales disponibles para enviar invitación
export type InvitationChannel = 'email' | 'whatsapp';

export const INVITATION_CHANNELS: { value: InvitationChannel; label: string; icon: string }[] = [
  { value: 'email', label: 'Correo electrónico', icon: '📧' },
  { value: 'whatsapp', label: 'WhatsApp', icon: '💬' },
];

// Request para enviar invitación
export interface InvitationRequest {
  email?: string;
  phone?: string;
  personId: string;
  role: string;
  permissions: string[];
  channel: InvitationChannel;
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
