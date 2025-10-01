import { ApiResponse } from "@/types/api";
import { Cities, States } from "@/types/StatesGeo";
import api from "..";

export async function getStatesGeo(): Promise<States[] | null> {
    try {
        const response = await api.get<ApiResponse<States[]>>("/states");
        return response.data.data;
    } catch (error) {
       console.error("Error fetching states:", error);
       return null; 
    }
}

export async function getCitiesByStateId(stateId: Number): Promise<Cities[] | null> {
    try {
        const response = await api.get<ApiResponse<Cities[]>>(`/states/${stateId}/cities`);
        return response.data.data as Cities[];
    } catch (error) {
       return null; 
    }
}