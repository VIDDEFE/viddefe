// context/AuthContext.tsx
import * as SecureStore from "expo-secure-store";
import React, { createContext, useContext, useEffect, useState } from "react";

type AuthContextType = {
  user: string | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType>({
  user: null,
  loading: true,
  signIn: async () => {},
  signOut: async () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  // 🔹 Restaurar sesión guardada
  useEffect(() => {
    const restoreUser = async () => {
      const savedUser = await SecureStore.getItemAsync("user");
      if (savedUser) setUser(savedUser);
      setLoading(false);
    };
    restoreUser();
  }, []);

  const signIn = async (email: string, password: string) => {
    // ⚠️ Aquí harías fetch a tu backend
    if (email && password) {
      await SecureStore.setItemAsync("user", email);
      setUser(email);
    }
  };

  const signOut = async () => {
    await SecureStore.deleteItemAsync("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
