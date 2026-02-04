import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { 
  userService, 
  type InvitationRequest,
  AVAILABLE_PERMISSIONS,
  PERMISSION_MAP,
  type Permission,
  type PermissionKey
} from '../services/userService';

// Hook para obtener permisos del backend
export function usePermissions() {
  return useQuery({
    queryKey: ['permissions'],
    queryFn: async () => {
      try {
        const response = await userService.getPermissions();
        // Mapear la respuesta del backend a nuestro formato
        const permissions: Permission[] = response.map(p => {
          const key = p.name as PermissionKey;
          const mapped = PERMISSION_MAP[key];
          if (mapped) {
            return {
              key,
              label: mapped.label,
              description: mapped.description,
              category: mapped.category,
            };
          }
          // Si el permiso no está en nuestro mapa, crear uno genérico
          return {
            key,
            label: p.name.replace(/_/g, ' ').toLowerCase(),
            description: `Permiso: ${p.name}`,
            category: 'people' as const,
          };
        });
        return permissions;
      } catch {
        // Fallback a permisos locales si el endpoint falla
        return AVAILABLE_PERMISSIONS;
      }
    },
    staleTime: 1000 * 60 * 10, // 10 minutos de cache
  });
}

// Hook para enviar invitación
export function useSendInvitation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: InvitationRequest) => userService.sendInvitation(data),
    onSuccess() {
      // Invalidar la lista de personas para reflejar que ahora tienen invitación pendiente
      qc.invalidateQueries({ queryKey: ['people'] });
    },
  });
}

// Legacy hooks (mantener por compatibilidad)
export function useCreateUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: any) => userService.create(data),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['people'] });
    },
  });
}

export function useDeleteUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => userService.delete(id),
    onSuccess() {
      qc.invalidateQueries({ queryKey: ['people'] });
    },
  });
}
