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

// Grupo
export interface Group extends BaseEntity {
  name: string;
  description: string;
  churchId: string;
  type: GroupType;
  leader: string; // ID de persona
  members: string[]; // IDs de personas
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
