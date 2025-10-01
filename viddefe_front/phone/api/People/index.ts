import { Person } from "@/types/People";
import api from "..";

export async function addPeople(person: Person): Promise<Person | null> {
    try{
        const response = await api.post<Person>("/people",person);
        return response.data as Person;
    }catch(error){
        return null;
    }
}