import { ApiResponse } from "@/types/api";
import { Person } from "@/types/People";
import api from "..";

export async function addPeople(person: Person, stateId: Number, typePersonId: Number): Promise<Person | null> {
    try{
        const payload = {
            ...person,
            stateId,
            typePersonId
        }
        const response = await api.post<ApiResponse<Person>>("/people",payload);
        console.log("resultado: ",response.data)
        return response.data.data as Person;
    }catch(error){
        return null;
    }
}