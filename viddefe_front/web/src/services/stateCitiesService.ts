import { apiService } from "./api";

export type States = {
    id: number;
    name: string;
}

export type Cities = {
    cityId: number;
    name: string;
}

export const stateCitiesService = {
  getStates: () => apiService.get<States[]>("/states"),
  getCitiesByState: (state: number) =>
    apiService.get<Cities[]>(`/states/${state}/cities`),
};

