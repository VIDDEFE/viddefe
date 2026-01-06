import axios, { type AxiosInstance, type AxiosRequestConfig } from "axios";
import { toast } from "sonner";

// -----------------------------------------------------------------------------
// Tipos del backend
// -----------------------------------------------------------------------------
export interface ApiResponse<T = unknown> {
  success: boolean;
  status: number;
  message: string;
  errorCode?: string;
  data?: T;
  metadata?: Record<string, any>;
  timestamp: string;
}

export interface ApiError {
  success: false;
  status: number;
  message: string;
  errorCode: string;
  meta?: Record<string, any>;
  timestamp: string;
}

export type Pageable<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  last?: boolean;
  first?: boolean;
};

// Dirección de ordenamiento compatible con Spring Boot
export type SortDirection = 'asc' | 'desc';

// Configuración de ordenamiento
export type SortConfig = {
  field: string;
  direction: SortDirection;
};

export type PageableRequest = {
  size: number;
  page: number;
  sort?: SortConfig;
};

// Cambiamos a sessionStorage para persistencia
export const STORAGE_KEYS = {
  USER: 'viddefe_user',
  PERMISSIONS: 'viddefe_permissions',
  TOKEN: 'viddefe_token',
  REFRESH_TOKEN: 'viddefe_refresh_token',
} as const;

// -----------------------------------------------------------------------------
// Callbacks para integración con AppContext
// -----------------------------------------------------------------------------
type AuthCallbacks = {
  onUnauthorized?: () => void;
  onLogout?: () => void;
  setUser?: (user: any | null) => void;
  setPermissions?: (permissions: any[]) => void;
};

// Registro global de callbacks
let authCallbacks: AuthCallbacks = {};

export const registerAuthCallbacks = (callbacks: AuthCallbacks) => {
  authCallbacks = { ...authCallbacks, ...callbacks };
};

export const clearAuthCallbacks = () => {
  authCallbacks = {};
};

// Helper para obtener/guardar token
export const getStoredToken = (): string | null => {
  return sessionStorage.getItem(STORAGE_KEYS.TOKEN);
};

export const setStoredToken = (token: string): void => {
  sessionStorage.setItem(STORAGE_KEYS.TOKEN, token);
};

export const removeStoredTokens = (): void => {
  sessionStorage.removeItem(STORAGE_KEYS.TOKEN);
  sessionStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
};

// -----------------------------------------------------------------------------
// Utilidad para decodificar HTML entities
// -----------------------------------------------------------------------------
function decodeHtmlEntities(str: any): any {
  if (typeof str === "string") {
    const textarea = document.createElement("textarea");
    textarea.innerHTML = str;
    return textarea.value;
  }
  if (Array.isArray(str)) return str.map(decodeHtmlEntities);
  if (typeof str === "object" && str !== null)
    return Object.fromEntries(
      Object.entries(str).map(([k, v]) => [k, decodeHtmlEntities(v)])
    );
  return str;
}

// -----------------------------------------------------------------------------
// API BASE URL
// -----------------------------------------------------------------------------
const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api/v1";

// -----------------------------------------------------------------------------
// ApiService
// -----------------------------------------------------------------------------
class ApiService {
  private client: AxiosInstance;

  constructor(baseURL: string = API_BASE_URL) {
    this.client = axios.create({ baseURL, timeout: 10000, withCredentials: true });

    // -------------------------------------------------------------------------
    // REQUEST INTERCEPTOR
    // -------------------------------------------------------------------------
    this.client.interceptors.request.use(
      (config) => {
        // Usamos el token de sessionStorage (persistente)
        const token = getStoredToken();

        if (token) {
          config.headers = config.headers || {};
          config.headers.Authorization = `Bearer ${token}`;
        }

        // Setear Content-Type solo si NO es FormData
        if (!(config.data instanceof FormData) && !config.headers?.["Content-Type"]) {
          config.headers = config.headers || {};
          config.headers["Content-Type"] = "application/json";
        }

        return config;
      },
      (error) => Promise.reject(error)
    );

    // -------------------------------------------------------------------------
    // RESPONSE INTERCEPTOR
    // -------------------------------------------------------------------------
    this.client.interceptors.response.use(
      (response) => {
        // Si viene como blob (descarga), no tocar
        if (response.config.responseType === "blob") {
          return response;
        }

        const res = response.data as ApiResponse<any>;

        // --- Si sigue el formato del backend (ApiResponse) ---
        if (res && typeof res.success === "boolean" && "data" in res) {
          res.data = decodeHtmlEntities(res.data);
          res.message = decodeHtmlEntities(res.message);

          if (res.success) {
            // Solo mostrar toast para operaciones de mutación (no GET)
            const method = response.config.method?.toUpperCase();
            const isMutationRequest = method && ['POST', 'PUT', 'PATCH', 'DELETE'].includes(method);
            
            if (res.message && isMutationRequest) {
              // Mapear mensajes genéricos a mensajes más descriptivos
              const friendlyMessages: Record<string, string> = {
                'OK': 'Cambios guardados exitosamente',
                'Created': 'Registro creado exitosamente',
              };
              const displayMessage = friendlyMessages[res.message] || res.message;
              toast.success(displayMessage);
            }

            // Si hay metadata con permissions, actualizarlos en el AppContext
            if (res.metadata?.permissions && authCallbacks.setPermissions) {
              authCallbacks.setPermissions(res.metadata.permissions);
            }

            // 👌 el interceptor normaliza la salida:
            // axios.get() → response.data = res.data
            return { ...response, data: res.data };
          }

          // Backend devolvió error controlado
          toast.error(res.message || "Error en la operación");
          return Promise.reject(res as ApiError);
        }

        // Si NO cumple ApiResponse, lo devolvemos decodificado
        response.data = decodeHtmlEntities(response.data);
        return response;
      },

      // --- ERROR INTERCEPTOR ---
      (error) => {
        console.log('🔴 API Error Interceptor:', error);
        
        if (error.response) {
          const { status, data } = error.response;
          console.log('🔴 Error status:', status, 'data:', data);

          // ---------------------------
          // 401 - No autorizado
          // ---------------------------
          if (status === 401) {
            toast.error("Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
            
            // Limpiar tokens almacenados
            removeStoredTokens();
            
            // Llamar a callback para actualizar AppContext
            if (authCallbacks.onUnauthorized) {
              authCallbacks.onUnauthorized();
            }
            
            if (authCallbacks.onLogout) {
              authCallbacks.onLogout();
            }

            return Promise.reject({
              success: false,
              status: 401,
              message: "No autorizado",
              errorCode: "UNAUTHORIZED",
              timestamp: new Date().toISOString(),
            } as ApiError);
          }

          // ---------------------------
          // 403 - Prohibido (permisos insuficientes)
          // ---------------------------
          if (status === 403) {
            toast.error("No tienes permisos suficientes para esta acción.");
            
            return Promise.reject({
              success: false,
              status: 403,
              message: "Prohibido",
              errorCode: "FORBIDDEN",
              timestamp: new Date().toISOString(),
            } as ApiError);
          }

          const apiError: ApiError = {
            success: false,
            status,
            message: decodeHtmlEntities(data?.message || "Error del servidor"),
            errorCode: data?.errorCode || "INTERNAL_ERROR",
            meta: decodeHtmlEntities(data?.meta),
            timestamp: data?.timestamp || new Date().toISOString(),
          };

          console.log('🔴 ApiError built:', apiError);

          // Validaciones con campos específicos
          if (apiError.meta?.fields) {
            console.log('🔴 Validation fields:', apiError.meta.fields);
            Object.values(apiError.meta.fields).forEach((msg) => {
              console.log('🔴 Showing field error toast:', msg);
              toast.error(String(msg));
            });
            return Promise.reject(apiError);
          }

          // Mensajes que no deben mostrarse como toast (errores esperados/controlados)
          const silentMessages = ['Zoom to large', 'Zoom too large'];
          const shouldSilence = silentMessages.some(msg => 
            apiError.message?.toLowerCase().includes(msg.toLowerCase())
          );

          if (!shouldSilence) {
            console.log('🔴 Showing generic error toast:', apiError.message);
            toast.error(apiError.message);
          }
          return Promise.reject(apiError);
        }

        // Error de red
        toast.error("No se pudo conectar al servidor");
        return Promise.reject({
          success: false,
          status: 0,
          message: "Network error",
          errorCode: "NETWORK_ERROR",
          timestamp: new Date().toISOString(),
        } as ApiError);
      }
    );
  }

  // -------------------------------------------------------------------------
  // MÉTODOS SIMPLIFICADOS
  // -------------------------------------------------------------------------

  public async get<T>(endpoint: string, config?: AxiosRequestConfig): Promise<T> {
    const res = await this.client.get<T>(endpoint, config);
    return res.data;
  }

  public async post<T>(
    endpoint: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<T> {
    const res = await this.client.post<T>(endpoint, data, config);
    return res.data;
  }

  public async put<T>(
    endpoint: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<T> {
    const res = await this.client.put<T>(endpoint, data, config);
    return res.data;
  }

  public async delete<T>(endpoint: string, config?: AxiosRequestConfig): Promise<T> {
    const res = await this.client.delete<T>(endpoint, config);
    return res.data;
  }

  public async deleteWithBody<T>(endpoint: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const res = await this.client.delete<T>(endpoint, { ...config, data });
    return res.data;
  }
}

export const apiService = new ApiService();