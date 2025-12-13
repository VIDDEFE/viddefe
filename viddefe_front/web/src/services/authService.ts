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

export interface SignInResponse {
  token: string;
  user: {
    id: string;
    email: string;
    name: string;
    peopleId?: string;
  };
}

export const authService = {
  createPerson: async (data: PersonRequest): Promise<PersonResponse> => {
    return apiService.post<PersonResponse>("/people", data);
  },

  signUp: async (data: SignUpRequest): Promise<SignUpResponse> => {
    return apiService.post<SignUpResponse>("/auth/sign-up", data);
  },

  signIn: async (data: SignInRequest): Promise<SignInResponse> => {
    return apiService.post<SignInResponse>("/auth/sign-in", data);
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },
};
