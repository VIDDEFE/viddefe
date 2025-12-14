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
  typePersonId: number;
  stateId: number;
  churchId?: string;
}

export interface SignUpRequest {
  email: string;
  password: string;
  peopleId: string;
  roleId: number;
}

export interface SignUpResponse {
  peopleId: string;
  email: string;
  password: string;
  roleId: number;
}

export interface SignInRequest {
  email: string;
  password: string;
}

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
  rolUser: RolUserInterface
}

export const authService = {
  createPerson: async (data: PersonRequest): Promise<PersonResponse> => {
    return apiService.post<PersonResponse>("/people", data);
  },

  signUp: async (data: SignUpRequest): Promise<SignUpResponse> => {
    return apiService.post<SignUpResponse>("/auth/sign-up", data);
  },

  signIn: async (data: SignInRequest): Promise<SignInData> => {
    return apiService.post<SignInData>("/auth/sign-in", data);
  },

  me: async (): Promise<UserInfoInterface> => {
    const response = await apiService.get<UserInfoInterface>("/auth/me");
    
    return response;
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },
};
