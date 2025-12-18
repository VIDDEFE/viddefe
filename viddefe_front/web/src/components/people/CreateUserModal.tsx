import { useState, useEffect, useMemo } from 'react';
import type { Person } from '../../models';
import { Modal, Button, Input, DropDown, Avatar } from '../shared';
import { 
  AVAILABLE_ROLES, 
  AVAILABLE_PERMISSIONS, 
  PERMISSION_CATEGORIES,
  type PermissionKey 
} from '../../services/userService';
import { useCreateUser } from '../../hooks/useUsers';
import { FiUser, FiMail, FiShield, FiCheck } from 'react-icons/fi';

interface CreateUserModalProps {
  isOpen: boolean;
  person: Person | null;
  onClose: () => void;
  onSuccess: () => void;
}

export default function CreateUserModal({
  isOpen,
  person,
  onClose,
  onSuccess,
}: CreateUserModalProps) {
  const [email, setEmail] = useState('');
  const [roleId, setRoleId] = useState<number>(2);
  const [selectedPermissions, setSelectedPermissions] = useState<Set<PermissionKey>>(new Set());
  const [error, setError] = useState('');

  const createUser = useCreateUser();

  // Resetear estado al abrir el modal
  useEffect(() => {
    if (isOpen) {
      setEmail('');
      setRoleId(2);
      setSelectedPermissions(new Set());
      setError('');
    }
  }, [isOpen]);

  // Agrupar permisos por categoría
  const permissionsByCategory = useMemo(() => {
    return AVAILABLE_PERMISSIONS.reduce((acc, permission) => {
      if (!acc[permission.category]) {
        acc[permission.category] = [];
      }
      acc[permission.category].push(permission);
      return acc;
    }, {} as Record<string, typeof AVAILABLE_PERMISSIONS>);
  }, []);

  // Conteo de permisos seleccionados por categoría
  const permissionCountByCategory = useMemo(() => {
    const counts: Record<string, number> = {};
    for (const category of Object.keys(PERMISSION_CATEGORIES)) {
      counts[category] = AVAILABLE_PERMISSIONS.filter(
        p => p.category === category && selectedPermissions.has(p.key)
      ).length;
    }
    return counts;
  }, [selectedPermissions]);

  const togglePermission = (key: PermissionKey) => {
    setSelectedPermissions(prev => {
      const newSet = new Set(prev);
      if (newSet.has(key)) {
        newSet.delete(key);
      } else {
        newSet.add(key);
      }
      return newSet;
    });
  };

  const toggleAllInCategory = (category: string) => {
    const categoryPermissions = permissionsByCategory[category] || [];
    const allSelected = categoryPermissions.every(p => selectedPermissions.has(p.key));
    
    setSelectedPermissions(prev => {
      const newSet = new Set(prev);
      if (allSelected) {
        // Deseleccionar todos
        categoryPermissions.forEach(p => newSet.delete(p.key));
      } else {
        // Seleccionar todos
        categoryPermissions.forEach(p => newSet.add(p.key));
      }
      return newSet;
    });
  };

  const validateForm = (): boolean => {
    if (!email.trim()) {
      setError('El correo electrónico es requerido');
      return false;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError('Ingresa un correo electrónico válido');
      return false;
    }
    if (!roleId) {
      setError('Selecciona un rol para el usuario');
      return false;
    }
    if (selectedPermissions.size === 0) {
      setError('Selecciona al menos un permiso para el usuario');
      return false;
    }
    return true;
  };

  const handleSubmit = async () => {
    setError('');
    if (!person?.id || !validateForm()) return;

    try {
      await createUser.mutateAsync({
        peopleId: person.id,
        email: email.trim(),
        roleId,
        permissions: Array.from(selectedPermissions),
      });
      onSuccess();
    } catch (err: any) {
      if (err?.message?.includes('email') || err?.message?.includes('correo')) {
        setError('Este correo electrónico ya está en uso');
      } else if (err?.message?.includes('permiso') || err?.message?.includes('permission')) {
        setError('No tienes permisos para crear usuarios');
      } else {
        setError(err?.message || 'Error al crear el usuario. Intenta de nuevo.');
      }
    }
  };

  if (!person) return null;

  const isLoading = createUser.isPending;
  const totalPermissions = selectedPermissions.size;

  return (
    <Modal
      isOpen={isOpen}
      title="Crear Usuario"
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          <Button
            variant="primary"
            onClick={handleSubmit}
            disabled={isLoading}
          >
            {isLoading ? 'Creando...' : 'Crear Usuario'}
          </Button>
          <Button variant="secondary" onClick={onClose} disabled={isLoading}>
            Cancelar
          </Button>
        </div>
      }
    >
      <div className="space-y-6">
        {/* Error message */}
        {error && (
          <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm flex items-center gap-2">
            <svg className="w-5 h-5 shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
            {error}
          </div>
        )}

        {/* Información de la persona (no editable) */}
        <div className="bg-neutral-50 rounded-lg p-4 border border-neutral-200">
          <div className="flex items-center gap-4">
            <Avatar
              src={(person as any).avatar}
              name={`${person.firstName} ${person.lastName}`}
              size="md"
            />
            <div className="flex-1">
              <h3 className="font-semibold text-neutral-900">
                {person.firstName} {person.lastName}
              </h3>
              <div className="flex items-center gap-4 text-sm text-neutral-600 mt-1">
                <span className="flex items-center gap-1">
                  <FiUser className="w-4 h-4" />
                  {(person as any).cc || 'Sin CC'}
                </span>
                {person.phone && (
                  <span className="flex items-center gap-1">
                    📞 {person.phone}
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Email y Rol */}
        <div className="space-y-4">
          <div>
            <label className="flex items-center gap-2 text-sm font-medium text-neutral-700 mb-2">
              <FiMail className="w-4 h-4" />
              Correo Electrónico <span className="text-red-500">*</span>
            </label>
            <Input
              type="email"
              placeholder="usuario@ejemplo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={isLoading}
            />
          </div>

          <div>
            <label className="flex items-center gap-2 text-sm font-medium text-neutral-700 mb-2">
              <FiShield className="w-4 h-4" />
              Rol del Usuario <span className="text-red-500">*</span>
            </label>
            <DropDown
              options={AVAILABLE_ROLES.map((role) => ({
                value: String(role.id),
                label: role.name,
              }))}
              value={String(roleId)}
              onChangeValue={(val) => setRoleId(Number(val))}
              searchKey="label"
            />
          </div>
        </div>

        {/* Permisos por categoría */}
        <div className="space-y-4">
          <h4 className="font-semibold text-neutral-900 flex items-center gap-2">
            🔑 Permisos <span className="text-red-500">*</span>
          </h4>

          {Object.entries(PERMISSION_CATEGORIES).map(([categoryKey, categoryInfo]) => {
            const categoryPermissions = permissionsByCategory[categoryKey] || [];
            const allSelected = categoryPermissions.every(p => selectedPermissions.has(p.key));
            const someSelected = categoryPermissions.some(p => selectedPermissions.has(p.key));

            return (
              <div key={categoryKey} className="border border-neutral-200 rounded-lg overflow-hidden">
                {/* Header de categoría */}
                <div 
                  className="bg-neutral-50 px-4 py-3 flex items-center justify-between cursor-pointer hover:bg-neutral-100 transition-colors"
                  onClick={() => toggleAllInCategory(categoryKey)}
                >
                  <div className="flex items-center gap-2">
                    <span className="text-lg">{categoryInfo.icon}</span>
                    <span className="font-medium text-neutral-800">{categoryInfo.label}</span>
                    <span className="text-xs text-neutral-500 bg-neutral-200 px-2 py-0.5 rounded-full">
                      {permissionCountByCategory[categoryKey]}/{categoryPermissions.length}
                    </span>
                  </div>
                  <div className={`w-5 h-5 rounded border-2 flex items-center justify-center transition-colors ${
                    allSelected 
                      ? 'bg-primary-600 border-primary-600' 
                      : someSelected 
                        ? 'bg-primary-200 border-primary-400' 
                        : 'border-neutral-300'
                  }`}>
                    {(allSelected || someSelected) && (
                      <FiCheck className={`w-3 h-3 ${allSelected ? 'text-white' : 'text-primary-600'}`} />
                    )}
                  </div>
                </div>

                {/* Lista de permisos */}
                <div className="divide-y divide-neutral-100">
                  {categoryPermissions.map((permission) => {
                    const isSelected = selectedPermissions.has(permission.key);
                    return (
                      <label
                        key={permission.key}
                        className="flex items-center gap-3 px-4 py-3 cursor-pointer hover:bg-neutral-50 transition-colors"
                      >
                        <input
                          type="checkbox"
                          checked={isSelected}
                          onChange={() => togglePermission(permission.key)}
                          disabled={isLoading}
                          className="w-4 h-4 text-primary-600 border-neutral-300 rounded focus:ring-primary-500 cursor-pointer"
                        />
                        <div className="flex-1">
                          <span className="text-sm font-medium text-neutral-800">
                            {permission.label}
                          </span>
                          <p className="text-xs text-neutral-500">{permission.description}</p>
                        </div>
                      </label>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>

        {/* Recuento de permisos seleccionados */}
        <div className={`rounded-lg p-4 border ${
          totalPermissions > 0 
            ? 'bg-green-50 border-green-200' 
            : 'bg-neutral-50 border-neutral-200'
        }`}>
          <h5 className="font-semibold text-neutral-900 mb-2 flex items-center gap-2">
            📊 Resumen de permisos
          </h5>
          
          {totalPermissions === 0 ? (
            <p className="text-sm text-neutral-500">No has seleccionado ningún permiso</p>
          ) : (
            <div className="space-y-2">
              <p className="text-sm text-neutral-700">
                <span className="font-semibold text-green-700">{totalPermissions}</span> permiso{totalPermissions !== 1 ? 's' : ''} seleccionado{totalPermissions !== 1 ? 's' : ''}:
              </p>
              <div className="flex flex-wrap gap-2">
                {Array.from(selectedPermissions).map(key => {
                  const permission = AVAILABLE_PERMISSIONS.find(p => p.key === key);
                  const category = PERMISSION_CATEGORIES[permission?.category as keyof typeof PERMISSION_CATEGORIES];
                  return (
                    <span 
                      key={key}
                      className="inline-flex items-center gap-1 text-xs bg-white border border-green-200 text-green-800 px-2 py-1 rounded-full"
                    >
                      <span>{category?.icon}</span>
                      {permission?.label}
                    </span>
                  );
                })}
              </div>
            </div>
          )}
        </div>

        {/* Info adicional */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 text-sm text-blue-800">
          <p className="font-medium mb-1">📋 Información importante:</p>
          <ul className="list-disc list-inside space-y-1 text-blue-700">
            <li>La contraseña será generada automáticamente por el servidor</li>
            <li>El usuario recibirá sus credenciales por correo electrónico</li>
            <li>Los permisos pueden ser modificados posteriormente</li>
          </ul>
        </div>
      </div>
    </Modal>
  );
}
