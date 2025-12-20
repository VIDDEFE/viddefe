import { useState, useEffect, useMemo } from 'react';
import type { Person } from '../../models';
import { Modal, Button, Input, DropDown, Avatar } from '../shared';
import { 
  DEFAULT_ROLES, 
  PERMISSION_CATEGORIES,
  type PermissionKey 
} from '../../services/userService';
import { useSendInvitation, usePermissions } from '../../hooks/useUsers';
import { FiUser, FiMail, FiShield, FiCheck, FiLoader, FiSend, FiAlertCircle } from 'react-icons/fi';

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
  const [roleId, setRoleId] = useState<string>('2');
  const [selectedPermissions, setSelectedPermissions] = useState<Set<PermissionKey>>(new Set());
  const [error, setError] = useState('');
  const [expandedCategories, setExpandedCategories] = useState<Set<string>>(new Set(['people', 'churches']));
  const [success, setSuccess] = useState(false);

  const sendInvitation = useSendInvitation();
  const { data: permissions = [], isLoading: loadingPermissions } = usePermissions();

  // Resetear estado al abrir el modal
  useEffect(() => {
    if (isOpen) {
      setEmail('');
      setRoleId('2');
      setSelectedPermissions(new Set());
      setError('');
      setSuccess(false);
      setExpandedCategories(new Set(['people', 'churches']));
    }
  }, [isOpen]);

  // Agrupar permisos por categoría
  const permissionsByCategory = useMemo(() => {
    return permissions.reduce((acc, permission) => {
      if (!acc[permission.category]) {
        acc[permission.category] = [];
      }
      acc[permission.category].push(permission);
      return acc;
    }, {} as Record<string, typeof permissions>);
  }, [permissions]);

  // Conteo de permisos seleccionados por categoría
  const permissionCountByCategory = useMemo(() => {
    const counts: Record<string, number> = {};
    for (const category of Object.keys(PERMISSION_CATEGORIES)) {
      counts[category] = permissions.filter(
        p => p.category === category && selectedPermissions.has(p.key)
      ).length;
    }
    return counts;
  }, [selectedPermissions, permissions]);

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
        categoryPermissions.forEach(p => newSet.delete(p.key));
      } else {
        categoryPermissions.forEach(p => newSet.add(p.key));
      }
      return newSet;
    });
  };

  const toggleCategoryExpand = (category: string) => {
    setExpandedCategories(prev => {
      const newSet = new Set(prev);
      if (newSet.has(category)) {
        newSet.delete(category);
      } else {
        newSet.add(category);
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
      await sendInvitation.mutateAsync({
        email: email.trim(),
        personId: person.id,
        role: roleId,
        permissions: Array.from(selectedPermissions),
      });
      setSuccess(true);
      // Esperar un momento para mostrar la animación de éxito
      setTimeout(() => {
        onSuccess();
      }, 1500);
    } catch (err: any) {
      if (err?.message?.includes('email') || err?.message?.includes('correo')) {
        setError('Este correo electrónico ya está en uso');
      } else if (err?.message?.includes('permiso') || err?.message?.includes('permission')) {
        setError('No tienes permisos para crear usuarios');
      } else {
        setError(err?.message || 'Error al enviar la invitación. Intenta de nuevo.');
      }
    }
  };

  if (!person) return null;

  const isLoading = sendInvitation.isPending;
  const totalPermissions = selectedPermissions.size;

  // Vista de éxito con animaciones
  if (success) {
    return (
      <Modal
        isOpen={isOpen}
        title=""
        onClose={onClose}
        actions={null}
      >
        <div className="py-8 text-center">
          <div className="relative mx-auto w-20 h-20 mb-6">
            <div className="absolute inset-0 bg-green-100 rounded-full animate-ping opacity-75" />
            <div 
              className="relative flex items-center justify-center w-20 h-20 bg-green-500 rounded-full"
              style={{ animation: 'scaleIn 0.3s ease-out' }}
            >
              <FiCheck className="w-10 h-10 text-white" />
            </div>
          </div>
          <h3 
            className="text-2xl font-bold text-neutral-900 mb-2"
            style={{ animation: 'slideUp 0.4s ease-out' }}
          >
            ¡Invitación enviada!
          </h3>
          <p 
            className="text-neutral-600"
            style={{ animation: 'slideUp 0.4s ease-out 0.1s both' }}
          >
            Se ha enviado una invitación a <span className="font-semibold">{email}</span>
          </p>
          <p 
            className="text-sm text-neutral-500 mt-2"
            style={{ animation: 'slideUp 0.4s ease-out 0.2s both' }}
          >
            El usuario recibirá un correo con las instrucciones para completar su registro.
          </p>
        </div>
      </Modal>
    );
  }

  return (
    <Modal
      isOpen={isOpen}
      title="Invitar Usuario"
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          <Button
            variant="primary"
            onClick={handleSubmit}
            disabled={isLoading || loadingPermissions}
            className="group relative overflow-hidden"
          >
            <span className={`flex items-center gap-2 transition-transform duration-300 ${isLoading ? 'translate-y-8' : ''}`}>
              <FiSend className="w-4 h-4 transition-transform group-hover:translate-x-1" />
              Enviar Invitación
            </span>
            {isLoading && (
              <span className="absolute inset-0 flex items-center justify-center">
                <FiLoader className="w-5 h-5 animate-spin" />
              </span>
            )}
          </Button>
          <Button variant="secondary" onClick={onClose} disabled={isLoading}>
            Cancelar
          </Button>
        </div>
      }
    >
      <div className="space-y-6">
        {/* Error message con animación */}
        {error && (
          <div 
            className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm flex items-center gap-2"
            style={{ animation: 'shake 0.5s ease-in-out' }}
          >
            <FiAlertCircle className="w-5 h-5 shrink-0" />
            {error}
          </div>
        )}

        {/* Información de la persona */}
        <div className="bg-gradient-to-r from-primary-50 to-indigo-50 rounded-xl p-4 border border-primary-100 transition-all duration-300 hover:shadow-md">
          <div className="flex items-center gap-4">
            <div className="relative">
              <Avatar
                src={(person as any).avatar}
                name={`${person.firstName} ${person.lastName}`}
                size="md"
              />
              <div className="absolute -bottom-1 -right-1 w-5 h-5 bg-green-500 rounded-full flex items-center justify-center ring-2 ring-white">
                <FiUser className="w-3 h-3 text-white" />
              </div>
            </div>
            <div className="flex-1">
              <h3 className="font-semibold text-neutral-900">
                {person.firstName} {person.lastName}
              </h3>
              <div className="flex items-center gap-4 text-sm text-neutral-600 mt-1">
                <span className="flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-primary-400" />
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
          <div className="group">
            <label className="flex items-center gap-2 text-sm font-medium text-neutral-700 mb-2 transition-colors group-focus-within:text-primary-600">
              <FiMail className="w-4 h-4 transition-transform group-focus-within:scale-110" />
              Correo Electrónico <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <Input
                type="email"
                placeholder="usuario@ejemplo.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={isLoading}
                className="transition-all duration-200 focus:ring-2 focus:ring-primary-500/20"
              />
            </div>
          </div>

          <div className="group">
            <label className="flex items-center gap-2 text-sm font-medium text-neutral-700 mb-2 transition-colors group-focus-within:text-primary-600">
              <FiShield className="w-4 h-4 transition-transform group-focus-within:scale-110" />
              Rol del Usuario <span className="text-red-500">*</span>
            </label>
            <DropDown
              options={DEFAULT_ROLES.map((role) => ({
                value: role.id,
                label: role.name,
              }))}
              value={roleId}
              onChangeValue={(val) => setRoleId(val)}
              searchKey="label"
            />
          </div>
        </div>

        {/* Permisos por categoría */}
        <div className="space-y-3">
          <h4 className="font-semibold text-neutral-900 flex items-center gap-2">
            🔑 Permisos <span className="text-red-500">*</span>
            {loadingPermissions && (
              <FiLoader className="w-4 h-4 animate-spin text-primary-500" />
            )}
          </h4>

          {Object.entries(PERMISSION_CATEGORIES).map(([categoryKey, categoryInfo], index) => {
            const categoryPermissions = permissionsByCategory[categoryKey] || [];
            const allSelected = categoryPermissions.length > 0 && categoryPermissions.every(p => selectedPermissions.has(p.key));
            const someSelected = categoryPermissions.some(p => selectedPermissions.has(p.key));
            const isExpanded = expandedCategories.has(categoryKey);

            return (
              <div 
                key={categoryKey} 
                className="border border-neutral-200 rounded-xl overflow-hidden transition-all duration-300 hover:border-primary-200 hover:shadow-sm"
                style={{ animationDelay: `${index * 0.1}s` }}
              >
                {/* Header de categoría */}
                <div 
                  className="bg-gradient-to-r from-neutral-50 to-neutral-100 px-4 py-3 flex items-center justify-between cursor-pointer hover:from-primary-50 hover:to-indigo-50 transition-all duration-300"
                  onClick={() => toggleCategoryExpand(categoryKey)}
                >
                  <div className="flex items-center gap-3">
                    <span className="text-2xl transition-transform hover:scale-110">{categoryInfo.icon}</span>
                    <div>
                      <span className="font-medium text-neutral-800">{categoryInfo.label}</span>
                      <span className="ml-2 text-xs text-neutral-500 bg-white px-2 py-0.5 rounded-full shadow-sm">
                        {permissionCountByCategory[categoryKey] || 0}/{categoryPermissions.length}
                      </span>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        toggleAllInCategory(categoryKey);
                      }}
                      className={`px-2 py-1 text-xs rounded-md transition-all duration-200 ${
                        allSelected 
                          ? 'bg-primary-100 text-primary-700 hover:bg-primary-200' 
                          : 'bg-white text-neutral-600 hover:bg-neutral-100 border border-neutral-200'
                      }`}
                    >
                      {allSelected ? 'Quitar todos' : 'Seleccionar todos'}
                    </button>
                    <div className={`w-6 h-6 rounded-full border-2 flex items-center justify-center transition-all duration-300 ${
                      allSelected 
                        ? 'bg-primary-600 border-primary-600 scale-110' 
                        : someSelected 
                          ? 'bg-primary-200 border-primary-400' 
                          : 'border-neutral-300 bg-white'
                    }`}>
                      {(allSelected || someSelected) && (
                        <FiCheck className={`w-3 h-3 ${allSelected ? 'text-white' : 'text-primary-600'}`} />
                      )}
                    </div>
                    <svg 
                      className={`w-5 h-5 text-neutral-400 transition-transform duration-300 ${isExpanded ? 'rotate-180' : ''}`}
                      fill="none" 
                      viewBox="0 0 24 24" 
                      stroke="currentColor"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                  </div>
                </div>

                {/* Lista de permisos con animación de colapso */}
                <div 
                  className={`overflow-hidden transition-all duration-300 ease-in-out ${
                    isExpanded ? 'max-h-[500px] opacity-100' : 'max-h-0 opacity-0'
                  }`}
                >
                  <div className="divide-y divide-neutral-100">
                    {categoryPermissions.map((permission, permIndex) => {
                      const isSelected = selectedPermissions.has(permission.key);
                      return (
                        <label
                          key={permission.key}
                          className={`flex items-center gap-3 px-4 py-3 cursor-pointer transition-all duration-200 ${
                            isSelected 
                              ? 'bg-primary-50/50 hover:bg-primary-50' 
                              : 'hover:bg-neutral-50'
                          }`}
                          style={{ animationDelay: `${permIndex * 0.05}s` }}
                        >
                          <div className="relative">
                            <input
                              type="checkbox"
                              checked={isSelected}
                              onChange={() => togglePermission(permission.key)}
                              disabled={isLoading}
                              className="peer sr-only"
                            />
                            <div className={`w-5 h-5 rounded-md border-2 flex items-center justify-center transition-all duration-200 ${
                              isSelected 
                                ? 'bg-primary-600 border-primary-600 scale-105' 
                                : 'border-neutral-300 peer-hover:border-primary-400'
                            }`}>
                              {isSelected && (
                                <FiCheck className="w-3 h-3 text-white" style={{ animation: 'scaleIn 0.2s ease-out' }} />
                              )}
                            </div>
                          </div>
                          <div className="flex-1">
                            <span className={`text-sm font-medium transition-colors ${
                              isSelected ? 'text-primary-700' : 'text-neutral-800'
                            }`}>
                              {permission.label}
                            </span>
                            <p className="text-xs text-neutral-500">{permission.description}</p>
                          </div>
                        </label>
                      );
                    })}
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Resumen de permisos con animación */}
        <div className={`rounded-xl p-4 border transition-all duration-300 ${
          totalPermissions > 0 
            ? 'bg-gradient-to-r from-green-50 to-emerald-50 border-green-200 shadow-sm' 
            : 'bg-neutral-50 border-neutral-200'
        }`}>
          <h5 className="font-semibold text-neutral-900 mb-2 flex items-center gap-2">
            📊 Resumen de permisos
            {totalPermissions > 0 && (
              <span className="text-xs bg-green-500 text-white px-2 py-0.5 rounded-full animate-pulse">
                {totalPermissions}
              </span>
            )}
          </h5>
          
          {totalPermissions === 0 ? (
            <p className="text-sm text-neutral-500 flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-neutral-300 animate-pulse" />
              No has seleccionado ningún permiso
            </p>
          ) : (
            <div className="space-y-2">
              <p className="text-sm text-neutral-700">
                <span className="font-semibold text-green-700">{totalPermissions}</span> permiso{totalPermissions !== 1 ? 's' : ''} seleccionado{totalPermissions !== 1 ? 's' : ''}:
              </p>
              <div className="flex flex-wrap gap-2">
                {Array.from(selectedPermissions).map((key, index) => {
                  const permission = permissions.find(p => p.key === key);
                  const category = PERMISSION_CATEGORIES[permission?.category as keyof typeof PERMISSION_CATEGORIES];
                  return (
                    <span 
                      key={key}
                      className="inline-flex items-center gap-1 text-xs bg-white border border-green-200 text-green-800 px-2 py-1 rounded-full shadow-sm hover:scale-105 transition-transform cursor-default"
                      style={{ animation: `fadeIn 0.3s ease-out ${index * 0.05}s both` }}
                    >
                      <span>{category?.icon}</span>
                      {permission?.label}
                      <button
                        onClick={() => togglePermission(key)}
                        className="ml-1 text-green-600 hover:text-red-500 transition-colors"
                      >
                        ×
                      </button>
                    </span>
                  );
                })}
              </div>
            </div>
          )}
        </div>

        {/* Info adicional */}
        <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-xl p-4 text-sm text-blue-800 transition-all duration-300 hover:shadow-md">
          <p className="font-medium mb-2 flex items-center gap-2">
            <span className="text-lg">📋</span> Información importante:
          </p>
          <ul className="space-y-2 text-blue-700">
            <li className="flex items-start gap-2">
              <span className="text-blue-400 mt-0.5">•</span>
              Se enviará un correo de invitación al usuario
            </li>
            <li className="flex items-start gap-2">
              <span className="text-blue-400 mt-0.5">•</span>
              El usuario deberá completar su registro siguiendo el enlace
            </li>
            <li className="flex items-start gap-2">
              <span className="text-blue-400 mt-0.5">•</span>
              Los permisos pueden ser modificados posteriormente
            </li>
          </ul>
        </div>
      </div>
    </Modal>
  );
}
