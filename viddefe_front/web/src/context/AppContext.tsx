import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from 'react';
import type { Church, Person, Service, Group, Event } from '../models';
import {
  authService,
  type SignInResponse,
  type UserInfoInterface,
} from '../services/authService';
import { 
  STORAGE_KEYS, 
  registerAuthCallbacks, 
  clearAuthCallbacks,
} from '../services';
import type { PermissionKey } from '../services/userService';

interface AppContextType {
  // Auth
  isHydrated: boolean;
  user: UserInfoInterface | null;
  permissions: PermissionKey[];
  login: (identifier: string, password: string, method: 'email' | 'phone') => Promise<SignInResponse>;
  logout: () => void;
  setUser: (user: UserInfoInterface | null) => void;
  setPermissions: (permissions: PermissionKey[]) => void;
  hasPermission: (permission: PermissionKey) => boolean;
  hasAnyPermission: (permissions: PermissionKey[]) => boolean;
  hasAllPermissions: (permissions: PermissionKey[]) => boolean;

  // Churches
  churches: Church[];
  addChurch: (church: Church) => void;
  updateChurch: (id: string, church: Partial<Church>) => void;
  deleteChurch: (id: string) => void;

  // People
  people: Person[];
  addPerson: (person: Person) => void;
  updatePerson: (id: string, person: Partial<Person>) => void;
  deletePerson: (id: string) => void;

  // Services
  services: Service[];
  addService: (service: Service) => void;
  updateService: (id: string, service: Partial<Service>) => void;
  deleteService: (id: string) => void;

  // Groups
  groups: Group[];
  addGroup: (group: Group) => void;
  updateGroup: (id: string, group: Partial<Group>) => void;
  deleteGroup: (id: string) => void;

  // Events
  events: Event[];
  addEvent: (event: Event) => void;
  updateEvent: (id: string, event: Partial<Event>) => void;
  deleteEvent: (id: string) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

// Hook personalizado para la hidratación persistente
const usePersistedState = <T,>(
  key: string,
  defaultValue: T,
  storage = sessionStorage
): [T, (value: T) => void] => {
  const [state, setState] = useState<T>(() => {
    try {
      const item = storage.getItem(key);
      return item ? JSON.parse(item) : defaultValue;
    } catch (error) {
      console.warn(`Error reading ${key} from storage:`, error);
      return defaultValue;
    }
  });

  const setPersistedState = useCallback((value: T) => {
    try {
      setState(value);
      if (value === null || value === undefined) {
        storage.removeItem(key);
      } else {
        storage.setItem(key, JSON.stringify(value));
      }
    } catch (error) {
      console.warn(`Error saving ${key} to storage:`, error);
    }
  }, [key, storage]);

  return [state, setPersistedState];
};

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  // =======================
  // ESTADO PERSISTENTE (sessionStorage)
  // =======================
  const [user, setUser] = usePersistedState<UserInfoInterface | null>(
    STORAGE_KEYS.USER,
    null
  );
  const [permissions, setPermissions] = usePersistedState<PermissionKey[]>(
    STORAGE_KEYS.PERMISSIONS,
    []
  );
  const [isHydrated, setIsHydrated] = useState(false);

  // =======================
  // SINCRONIZAR TOKEN CON sessionStorage
  // =======================
  useEffect(() => {
    // Inicializar token desde sessionStorage
    setIsHydrated(true);
  }, []);


  // =======================
  // REGISTRAR CALLBACKS PARA API SERVICE
  // =======================
  useEffect(() => {
    // Registrar callbacks para que apiService pueda actualizar el estado
    registerAuthCallbacks({
      onUnauthorized: () => {
        handleLogout();
      },
      onLogout: () => {
        handleLogout();
      },
      setUser: (newUser: UserInfoInterface | null) => {
        setUser(newUser);
      },
      setPermissions: (newPermissions: PermissionKey[]) => {
        setPermissions(newPermissions);
      }
    });

    // Limpiar callbacks al desmontar
    return () => {
      clearAuthCallbacks();
    };
  }, [setUser, setPermissions]);

  // =======================
  // FUNCIÓN DE LOGOUT CENTRALIZADA
  // =======================
  const handleLogout = useCallback(() => {
    
    // Limpiar estado
    setUser(null);
    setPermissions([]);
    
    // Limpiar almacenamiento (aunque usePersistedState ya lo hace)
    sessionStorage.removeItem(STORAGE_KEYS.USER);
    sessionStorage.removeItem(STORAGE_KEYS.PERMISSIONS);
    
    // Opcional: Limpiar sessionStorage también por si acaso
    sessionStorage.clear();
    
    // Opcional: Redirigir a login
    // if (window.location.pathname !== '/login') {
    //   window.location.href = '/login';
    // }
  }, [setUser, setPermissions]);

  // =======================
  // AUTH ACTIONS
  // =======================
  const login = async (identifier: string, password: string, method: 'email' | 'phone' = 'email'): Promise<SignInResponse> => {
    const payload = method === 'email' 
      ? { email: identifier, password }
      : { phone: identifier, password };
    
    const response: SignInResponse = await authService.signIn(payload);

    if (!response) throw new Error('Sign in failed');

    // Asumimos que el token viene en la respuesta
    const userInfo = await authService.me();
    if (!userInfo) throw new Error('Failed to retrieve user info');

    setUser(userInfo);
    return response;
  };

  const logout = () => {
    handleLogout();
  };

  // =======================
  // PERMISSIONS HELPERS
  // =======================
  const hasPermission = useCallback(
    (permission: PermissionKey) => permissions.includes(permission),
    [permissions],
  );

  const hasAnyPermission = useCallback(
    (required: PermissionKey[]) =>
      required.some(p => permissions.includes(p)),
    [permissions],
  );

  const hasAllPermissions = useCallback(
    (required: PermissionKey[]) =>
      required.every(p => permissions.includes(p)),
    [permissions],
  );

  // =======================
  // DOMAIN STATE (no persistente por defecto)
  // =======================
  const [churches, setChurches] = useState<Church[]>([]);
  const [people, setPeople] = useState<Person[]>([]);
  const [services, setServices] = useState<Service[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [events, setEvents] = useState<Event[]>([]);

  // Churches
  const addChurch = (church: Church) =>
    setChurches(prev => [...prev, church]);
  const updateChurch = (id: string, updates: Partial<Church>) =>
    setChurches(prev =>
      prev.map(c => (c.id === id ? { ...c, ...updates } : c)),
    );
  const deleteChurch = (id: string) =>
    setChurches(prev => prev.filter(c => c.id !== id));

  // People
  const addPerson = (person: Person) =>
    setPeople(prev => [...prev, person]);
  const updatePerson = (id: string, updates: Partial<Person>) =>
    setPeople(prev =>
      prev.map(p => (p.id === id ? { ...p, ...updates } : p)),
    );
  const deletePerson = (id: string) =>
    setPeople(prev => prev.filter(p => p.id !== id));

  // Services
  const addService = (service: Service) =>
    setServices(prev => [...prev, service]);
  const updateService = (id: string, updates: Partial<Service>) =>
    setServices(prev =>
      prev.map(s => (s.id === id ? { ...s, ...updates } : s)),
    );
  const deleteService = (id: string) =>
    setServices(prev => prev.filter(s => s.id !== id));

  // Groups
  const addGroup = (group: Group) =>
    setGroups(prev => [...prev, group]);
  const updateGroup = (id: string, updates: Partial<Group>) =>
    setGroups(prev =>
      prev.map(g => (g.id === id ? { ...g, ...updates } : g)),
    );
  const deleteGroup = (id: string) =>
    setGroups(prev => prev.filter(g => g.id !== id));

  // Events
  const addEvent = (event: Event) =>
    setEvents(prev => [...prev, event]);
  const updateEvent = (id: string, updates: Partial<Event>) =>
    setEvents(prev =>
      prev.map(e => (e.id === id ? { ...e, ...updates } : e)),
    );
  const deleteEvent = (id: string) =>
    setEvents(prev => prev.filter(e => e.id !== id));


  return (
    <AppContext.Provider
      value={{
        isHydrated,
        user,
        permissions,
        login,
        logout,
        setUser,
        setPermissions,
        hasPermission,
        hasAnyPermission,
        hasAllPermissions,
        churches,
        addChurch,
        updateChurch,
        deleteChurch,
        people,
        addPerson,
        updatePerson,
        deletePerson,
        services,
        addService,
        updateService,
        deleteService,
        groups,
        addGroup,
        updateGroup,
        deleteGroup,
        events,
        addEvent,
        updateEvent,
        deleteEvent,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useAppContext = () => {
  const ctx = useContext(AppContext);
  if (!ctx) {
    throw new Error('useAppContext must be used inside AppProvider');
  }
  return ctx;
};

// Hook auxiliar para acceso rápido a la autenticación
export const useAuth = () => {
  const ctx = useAppContext();
  return {
    user: ctx.user,
    isAuthenticated: !!ctx.user,
    login: ctx.login,
    logout: ctx.logout,
    hasPermission: ctx.hasPermission,
    hasAnyPermission: ctx.hasAnyPermission,
    hasAllPermissions: ctx.hasAllPermissions,
  };
};