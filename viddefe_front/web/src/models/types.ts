import type { Cities, States } from "../services/stateCitiesService";

// Base interface para entidades con ID
export interface BaseEntity {
  id: string;
  createdAt: Date;
  updatedAt: Date;
}

// Pastor info dentro de Church
export interface ChurchPastor {
  id: string;
  cc: string;
  firstName: string;
  lastName: string;
  phone: string;
  avatar?: string;
  birthDate: string;
  typePersonId: number;
  stateId: number;
  churchId?: string;
}

// Iglesia (para crear/actualizar)
export interface Church extends BaseEntity {
  name: string;
  cityId: number;
  phone: string;
  email: string;
  pastor: string;
  pastorId?: string;
  foundationDate?: string;
  memberCount: number;
  latitude: number;
  longitude: number;
}

// Respuesta detallada de getById
export interface ChurchDetail {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
  phone?: string;
  email?: string;
  foundedYear?: number;
  foundationDate?: string;
  memberCount?: number;
  city: Cities;
  states: States;
  pastor: ChurchPastor | null;
}

export interface ChurchSummary {
  id: string;
  name: string;
  longitude: number;
  pastor: Person | null;
  latitude: number;
  states: States;
  city: Cities;
}

// Persona
export interface Person extends BaseEntity {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  birthDate: Date;
  typePerson: PersonRole;
  churchId: string;
  state: States;
  status: 'active' | 'inactive' | 'suspended';
  // Campos para la gestión de usuarios
  hasUser?: boolean;
  userId?: string;
  avatar?: string;
}

export type PersonRole = {
  id: number;
  name: string;
};

// Servicio/Culto
export interface Service extends BaseEntity {
  name: string;
  description: string;
  churchId: string;
  date: Date;
  startTime: string;
  endTime: string;
  type: ServiceType;
  attendees: string[]; // IDs de personas
  pastor: string; // ID de persona
  location: string;
}

export type ServiceType = 'sunday_service' | 'wednesday_service' | 'prayer_night' | 'special_event' | 'youth_service';

// Estrategia de Grupo
export interface Strategy {
  id: string;
  name: string;
}

// Persona resumida para líder de grupo
export interface PersonSummary {
  id: string;
  cc?: string;
  firstName: string;
  lastName: string;
  phone?: string;
  avatar?: string;
  birthDate?: string;
  typePersonId?: number;
  stateId?: number;
  churchId?: string;
}

// Grupo (Home Group) - respuesta del backend
export interface HomeGroup {
  id: string;
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  leader: PersonSummary | null;
  strategy: Strategy | null;
}

// DTO para crear un grupo
export interface CreateHomeGroupDto {
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  leaderId: string;
  strategyId: string;
}

// DTO para actualizar un grupo
export interface UpdateHomeGroupDto {
  name?: string;
  description?: string;
  latitude?: number;
  longitude?: number;
  leaderId?: string;
  strategyId?: string;
}

// Persona en la jerarquía de roles (respuesta del backend)


// Nodo de la jerarquía de roles/estrategias (estructura de árbol recursiva)
export interface RoleStrategyNode {
  id: string;
  name: string;
  children: RoleStrategyNode[];
  people: Person[];
}

// Respuesta del detalle de un grupo
export interface HomeGroupDetailResponse {
  homeGroup: HomeGroup;
  strategy: Strategy | null;
  hierarchy: RoleStrategyNode[];
}

// DTO para crear un rol en la estrategia (solo estructura, sin personas)
export interface CreateRoleDto {
  strategyId: string;
  name: string;
  parentRoleId?: string;
}

// DTO para actualizar un rol (solo estructura, sin personas)
export interface UpdateRoleDto {
  name?: string;
  parentRoleId?: string | null;
}

// DTO para asignar una persona a un rol (en contexto de grupo)
export interface AssignPersonToRoleDto {
  personId: string;
}

// DTO para asignar múltiples personas a un rol
export interface AssignPeopleToRoleDto {
  personIds: string[];
}

// Mantener Group legacy para compatibilidad
/** @deprecated Usar HomeGroup en su lugar */
export interface Group extends BaseEntity {
  name: string;
  description: string;
  churchId: string;
  type: GroupType;
  leader: string;
  members: string[];
  meetingDay: string;
  meetingTime: string;
  location: string;
}

export type GroupType = 'home_group' | 'youth_group' | 'womens_group' | 'mens_group' | 'prayer_group' | 'study_group';

// Evento
export interface Event extends BaseEntity {
  title: string;
  description: string;
  churchId: string;
  date: Date;
  startTime: string;
  endTime: string;
  location: string;
  organizer: string; // ID de persona
  attendees: string[]; // IDs de personas
  maxCapacity: number;
  status: EventStatus;
}

export type EventStatus = 'planned' | 'in_progress' | 'completed' | 'cancelled';

// Worship Type (Tipo de Culto)
export interface WorshipType {
  id: number;
  name: string;
}

// Worship Meeting (Culto)
export interface Worship {
  id: string;
  name: string;
  description?: string;
  creationDate: string;
  scheduledDate: string;
  worshipType: WorshipType;
}

// DTO para crear un culto
export interface CreateWorshipDto {
  name: string;
  description?: string;
  scheduledDate: string;
  worshipTypeId: number;
}

// DTO para actualizar un culto
export interface UpdateWorshipDto {
  name?: string;
  description?: string;
  scheduledDate?: string;
  worshipTypeId?: number;
}
