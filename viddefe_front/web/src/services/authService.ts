import type { ChurchSummary } from "../models";
import { apiService } from "./api";

export interface PersonRequest {
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

export interface PersonResponse {
  id: string;
  cc: string;
  firstName: string;
  lastName: string;
  phone: string;
  avatar?: string;
  birthDate: string;
  typePerson: {
    id: number;
    name: string;
  };
  state: {
    id: number;
    name: string;
  };
}

export interface SignUpRequest {
  email: string;
  password: string;
  peopleId: string;
  roleId: number;
}

// Tipos para el flujo de sign-up por pasos
export type SignUpStep = 'CREATION_USER' | 'CREATION_CHURCH' | 'DONE';

// DTO genérico de respuesta del proceso de auth
export interface AuthProcessResponse<T> {
  completed: boolean;
  nextStep: SignUpStep;
  data: T;
}

// Respuestas específicas para cada paso
export interface SignUpPastorResponse extends AuthProcessResponse<PersonResponse> {
  nextStep: 'CREATION_USER';
}

export interface SignUpUserResponse extends AuthProcessResponse<string> {
  nextStep: 'CREATION_CHURCH' | 'DONE';
}

export interface SignUpChurchResponse extends AuthProcessResponse<string> {
  nextStep: 'DONE';
}

// Respuesta de sign-in cuando el proceso está incompleto
export interface SignInIncompleteData {
  peopleId: string;
  userId?: string;
  email?: string;
  person?: PersonResponse;
}

// Respuesta de sign-in cuando está completo
export interface SignInCompleteData {
  email: string;
  rolUserModel: {
    id: number;
    name: string;
  };
  firstName: string;
  lastName: string;
  userId: string;
}

// Respuesta unificada de sign-in
export type SignInResponse = AuthProcessResponse<SignInCompleteData | SignInIncompleteData>;

export interface ChurchRequest {
  name: string;
  cityId: number;
  phone?: string;
  email?: string;
  pastorId?: string;
  foundationDate?: string;
  latitude?: number;
  longitude?: number;
}

export interface SignInRequest {
  email: string;
  password: string;
}

// Legacy - mantener por compatibilidad
export interface SignInData {
  email: string;
  rolUserModel: {
    id: number;
    name: string;
  };
  firstName: string;
  lastName: string;
  personId: string;
  avatar?: string;
}

export interface RolUserInterface {
  id: number;
  name: string;
}

export interface UserInfoInterface {
  user: string;
  person: PersonResponse;
  church: ChurchSummary;
  rolUser: RolUserInterface;
}

// Respuesta completa del signin con meta (permisos)
export interface SignInFullResponse {
  success: boolean;
  status: number;
  message: string;
  data: SignInCompleteData | SignInIncompleteData;
  meta?: {
    permissions: string[];
  };
  timestamp: string;
}

export const authService = {
  // Step 1: Crear pastor (persona)
  signUpPastor: async (data: PersonRequest): Promise<SignUpPastorResponse> => {
    return apiService.post<SignUpPastorResponse>("/auth/sign-up/pastor", data);
  },

  // Step 2: Crear usuario
  signUpUser: async (data: SignUpRequest): Promise<SignUpUserResponse> => {
    return apiService.post<SignUpUserResponse>("/auth/sign-up/user", data);
  },

  // Step 3: Crear iglesia
  signUpChurch: async (data: ChurchRequest): Promise<SignUpChurchResponse> => {
    return apiService.post<SignUpChurchResponse>("/auth/sign-up/church", data);
  },

  // Crear persona (legacy - usado en gestión de personas)
  createPerson: async (data: PersonRequest): Promise<PersonResponse> => {
    return apiService.post<PersonResponse>("/people", data);
  },

  // Sign in - puede devolver proceso incompleto
  signIn: async (data: SignInRequest): Promise<SignInResponse> => {
    return apiService.post<SignInResponse>("/auth/sign-in", data);
  },

  me: async (): Promise<UserInfoInterface> => {
    const response = await apiService.get<UserInfoInterface>("/auth/me");
    return response;
  },

  logout: () => {
    sessionStorage.removeItem("viddefe_token");
    sessionStorage.removeItem("viddefe_user");
    sessionStorage.removeItem("viddefe_permissions");
  },
};
