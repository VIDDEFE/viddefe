import { Person } from "@/types/People";
import api from "..";

export async function addPeople(person: Person, stateId: Number, typePersonId: Number): Promise<Person | null> {
    try{
        const payload = {
            ...person,
            stateId,
            typePersonId
        }
        const response = await api.post<Person>("/people",payload);
        return response.data as Person;
    }catch(error){
        return null;
    }
}