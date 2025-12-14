import React, { createContext, useContext, useState, useEffect } from 'react';
import type { Church, Person, Service, Group, Event } from '../models';
import { authService, type UserInfoInterface } from '../services/authService';

interface AppContextType {
  // Auth
  isLoggedIn: boolean;
  user: UserInfoInterface | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  setUser: (user: UserInfoInterface | null) => void;

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

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Auth
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUserState] = useState<UserInfoInterface | null>(null);
  const [token, setToken] = useState<string | null>(null);

  // Recuperar datos de localStorage al montar
  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUserState(JSON.parse(savedUser));
      setIsLoggedIn(true);
    }
  }, []);

  const login = async (email: string, password: string) => {
    const response = await authService.signIn({ email, password });
    if (!response && !(response as any).token) return;
    const authToken = (response as any).token;
    localStorage.setItem('token', authToken);
    setToken(authToken);
    
    // Obtener info del usuario
    const userInfo = await authService.me();
    if (!userInfo) return;
    localStorage.setItem('user', JSON.stringify(userInfo));
    setUserState(userInfo);
    setIsLoggedIn(true);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUserState(null);
    setIsLoggedIn(false);
  };

  const setUser = (newUser: UserInfoInterface | null) => {
    setUserState(newUser);
    if (newUser) {
      localStorage.setItem('user', JSON.stringify(newUser));
      setIsLoggedIn(true);
    } else {
      localStorage.removeItem('user');
      setIsLoggedIn(false);
    }
  };

  const [churches, setChurches] = useState<Church[]>([]);
  const [people, setPeople] = useState<Person[]>([]);
  const [services, setServices] = useState<Service[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [events, setEvents] = useState<Event[]>([]);

  // Church methods
  const addChurch = (church: Church) => setChurches([...churches, church]);
  const updateChurch = (id: string, updates: Partial<Church>) => 
    setChurches(churches.map(c => c.id === id ? { ...c, ...updates } : c));
  const deleteChurch = (id: string) => setChurches(churches.filter(c => c.id !== id));

  // Person methods
  const addPerson = (person: Person) => setPeople([...people, person]);
  const updatePerson = (id: string, updates: Partial<Person>) => 
    setPeople(people.map(p => p.id === id ? { ...p, ...updates } : p));
  const deletePerson = (id: string) => setPeople(people.filter(p => p.id !== id));

  // Service methods
  const addService = (service: Service) => setServices([...services, service]);
  const updateService = (id: string, updates: Partial<Service>) => 
    setServices(services.map(s => s.id === id ? { ...s, ...updates } : s));
  const deleteService = (id: string) => setServices(services.filter(s => s.id !== id));

  // Group methods
  const addGroup = (group: Group) => setGroups([...groups, group]);
  const updateGroup = (id: string, updates: Partial<Group>) => 
    setGroups(groups.map(g => g.id === id ? { ...g, ...updates } : g));
  const deleteGroup = (id: string) => setGroups(groups.filter(g => g.id !== id));

  // Event methods
  const addEvent = (event: Event) => setEvents([...events, event]);
  const updateEvent = (id: string, updates: Partial<Event>) => 
    setEvents(events.map(e => e.id === id ? { ...e, ...updates } : e));
  const deleteEvent = (id: string) => setEvents(events.filter(e => e.id !== id));

  const value: AppContextType = {
    isLoggedIn,
    user,
    token,
    login,
    logout,
    setUser,
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
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export const useAppContext = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useAppContext debe ser usado dentro de AppProvider');
  }
  return context;
};
