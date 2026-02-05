import type { Pageable } from "../services";
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
  pastor?: string;
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
  attendanceQuality?: AttendaceQualityDto;
}

export type AttendaceQualityDto = {
  id?: number;
  name: string;
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

// Persona resumida para responsable de grupo
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
  manager: PersonSummary | null;
  strategy: Strategy | null;
}

// DTO para crear un grupo
export interface CreateHomeGroupDto {
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  managerId: string;
  strategyId: string;
}

// DTO para actualizar un grupo
export interface UpdateHomeGroupDto {
  name?: string;
  description?: string;
  latitude?: number;
  longitude?: number;
  managerId?: string;
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

// DTO para asignar/remover personas a un rol
export interface RolePeopleDto {
  peopleIds: string[];
}

// Mantener Group legacy para compatibilidad
/** @deprecated Usar HomeGroup en su lugar */
export interface Group extends BaseEntity {
  name: string;
  description: string;
  churchId: string;
  type: GroupType;
  manager: string;
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


// Registro de asistencia individual
export interface WorshipAttendance {
  people: Person;
  status: 'PRESENT' | 'ABSENT' | string;
}

// Worship Meeting (Culto) - respuesta básica de lista
export interface Worship {
  id: string;
  name: string;
  description?: string;
  creationDate: string;
  scheduledDate: string;
  type: MeetingType;
}

// Worship Detail - respuesta detallada (sin lista de asistencia, solo conteos)
// La asistencia se obtiene de forma paginada en /worship/meeting/{id}/attendance
export interface WorshipDetail extends Worship {
  totalAttendance: number;
  presentCount: number;
  absentCount: number;
  date?: string; // Campo adicional que puede venir en la respuesta
}

// DTO para crear un culto
// IMPORTANTE: scheduledDate debe incluir timezone offset (ISO-8601)
// Ejemplo: "2026-01-15T10:00:00-05:00"
export interface CreateWorshipDto {
  name: string;
  description?: string;
  scheduledDate: string; // ISO-8601 con offset obligatorio
  meetingTypeId: number;
}

// DTO para actualizar un culto
// IMPORTANTE: scheduledDate debe incluir timezone offset si se provee
export interface UpdateWorshipDto {
  name?: string;
  description?: string;
  scheduledDate?: string; // ISO-8601 con offset obligatorio si se incluye
  meetingTypeId?: number;
}

// Offering Type (Tipo de Ofrenda)
export interface OfferingType {
  id: number;
  name: string;
}


// Ofrenda - respuesta del backend
export interface Offering {
  id?: string; // El id puede venir del backend o no
  eventId: string;
  amount: number;
  people: Person | null;
  type: OfferingType;
}

export interface OfferingAnalytics {
  code: string;
  name: string;
  amount: number;
  count: number;
}

export interface OfferingList {
  offerings: Pageable<Offering>;
  analitycs: OfferingAnalytics[] | null;
}

// DTO para crear una ofrenda
export interface CreateOfferingDto {
  eventId: string;
  amount: number;
  peopleId?: string; // Opcional si es ofrenda anónima
  offeringTypeId: number;
}

// DTO para actualizar una ofrenda
export interface UpdateOfferingDto {
  id: string;
  eventId: string;
  amount: number;
  peopleId?: string;
  offeringTypeId: number;
}

// ============================================================================
// MEETINGS (Reuniones de Grupo)
// ============================================================================

// Tipo de Reunión
export interface MeetingType {
  id: number;
  name: string;
}

// Meeting (Reunión) - respuesta del backend según nuevo contrato API
export interface Meeting {
  id: string;
  name: string;
  description?: string;
  scheduledDate: string; // ISO-8601 con timezone
  creationDate: string;  // ISO-8601 con timezone
  type: MeetingType;
  // Campos adicionales para detalle (pueden no venir en lista)
  totalAttendance?: number;
  presentCount?: number;
  absentCount?: number;
}

// Persona en la asistencia de reunión (reutilizamos estructura existente)


// Registro de asistencia individual a reunión
export interface MeetingAttendance {
  people: Person;
  status: 'PRESENT' | 'ABSENT' | string;
}

// DTO para crear una reunión (unificado para worship y group meeting)
// IMPORTANTE: scheduledDate debe incluir timezone offset (ISO-8601)
// Ejemplo: "2026-01-15T10:00:00-05:00"
export interface CreateMeetingDto {
  name: string;
  description?: string;
  scheduledDate: string; // ISO-8601 con offset obligatorio
  meetingTypeId: number; // ID del tipo de reunión (unificado)
  meetingType: string; // 'WORSHIP' | 'GROUP_MEETING'
}

// DTO para actualizar una reunión (unificado para worship y group meeting)
// IMPORTANTE: scheduledDate debe incluir timezone offset si se provee
export interface UpdateMeetingDto {
  name?: string;
  description?: string;
  scheduledDate?: string; // ISO-8601 con offset obligatorio si se incluye
  meetingTypeId?: number; // ID del tipo de reunión (unificado)
  meetingType?: string; // 'WORSHIP' | 'GROUP_MEETING'
}

// ============================================================================
// MINISTRY FUNCTIONS (Funciones Ministeriales)
// ============================================================================

// Tipo de evento para ministry functions
export type EventType = 'TEMPLE_WORHSIP' | 'GROUP_MEETING';

// Rol en la función ministerial
export interface MinistryRole {
  id: number;
  name: string;
}

// Función Ministerial - respuesta del backend
export interface MinistryFunction {
  id: string;
  people: PersonSummary;
  role: MinistryRole;
}

// DTO para crear/actualizar una función ministerial
export interface CreateMinistryFunctionDto {
  peopleId: string;
  roleId: number;
}

// DTO para actualizar (mismo que crear)
export interface UpdateMinistryFunctionDto {
  peopleId: string;
  roleId: number;
}

// ============================================================================
// METRICS (Métricas de Asistencia)
// ============================================================================

/**
 * Métricas base que aplica a todos los tipos de reunión
 */
export interface BaseMetrics {
  newAttendees: number;
  totalPeopleAttended: number;
  totalPeople: number;
  attendanceRate: number;
  absenceRate: number;
  totalMeetings: number;
  averageAttendancePerMeeting: number;
}

/**
 * Métricas adicionales para worship/temple worship
 * Incluye desglose entre métricas de grupos y métricas de iglesia
 */
export interface WorshipMetrics extends BaseMetrics {
  totalGroups: number;
  groupMetrics: BaseMetrics;
  churchMetrics: BaseMetrics;
}
