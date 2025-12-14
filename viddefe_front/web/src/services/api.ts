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
  meta?: Record<string, any>;
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
};

export type PageableRequest = {
  size: number;
  page: number;
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
        const token = localStorage.getItem("token");

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
            // Evitar spamear toasts en respuestas 200 triviales
            if (res.message && !["OK", "Created"].includes(res.message)) {
              toast.success(res.message);
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
        if (error.response) {
          const { status, data } = error.response;

          // ---------------------------
          // 401 - No autorizado
          // ---------------------------
          if (status === 401) {
            toast.error("No tienes permiso para esa acción.");
            localStorage.clear();

            return Promise.reject({
              success: false,
              status: 401,
              message: "No autorizado",
              errorCode: "UNAUTHORIZED",
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

          // Validaciones
          if (apiError.meta?.fields) {
            Object.values(apiError.meta.fields).forEach((msg) => toast.error(String(msg)));
            return Promise.reject(apiError);
          }

          toast.error(apiError.message);
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
  // MÉTODOS SIMPLIFICADOS (ya NO procesan ApiResponse)
  // El interceptor ya dejó response.data = data limpia.
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
}

export const apiService = new ApiService();
