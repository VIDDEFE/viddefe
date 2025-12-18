import { useMutation, useQueryClient } from '@tanstack/react-query';
import { userService, type CreateUserRequest } from '../services/userService';

export function useCreateUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateUserRequest) => userService.create(data),
    onSuccess() {
      // Invalidar la lista de personas para reflejar que ahora tienen usuario
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
