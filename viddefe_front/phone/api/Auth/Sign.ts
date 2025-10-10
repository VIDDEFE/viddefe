import { ApiResponse } from "@/types/api";
import { User } from "@/types/User";
import api from "..";

export async function signUp(user: User):Promise<void> {
    try {
        await api.post<ApiResponse<User>>("/auth/sign-up",user);
    } catch (error) {
        console.error(`El usuario con correo ${user.email} no puedo ser creado`)
    }
}

export async function signIn(user: Partial<User>):Promise<void> {
    try {
        await api.post<User>("/auth/sign-in",user);
    } catch (error) {
        console.error(`El usuario con correo ${user.email} no puedo ser logueado`)
    }
}