import { useState } from 'react';
import { useAppContext } from '../../context/AppContext';
import { Card, Button, Input, Avatar, PageHeader } from '../../components/shared';
import { 
  FiUser, 
  FiMail, 
  FiPhone, 
  FiCalendar, 
  FiMapPin, 
  FiShield, 
  FiEdit2, 
  FiSave, 
  FiX,
  FiLock,
  FiLogOut,
  FiCheck
} from 'react-icons/fi';
import {IconChurch} from '../../components/icons';
import { PERMISSION_CATEGORIES } from '../../services/userService';
import { formatDateForDisplay } from '../../utils/helpers';

export default function Account() {
  const { user, permissions, logout } = useAppContext();
  const [isEditing, setIsEditing] = useState(false);
  const [showChangePassword, setShowChangePassword] = useState(false);

  // Datos editables (por ahora solo visualización)
  const [editData, setEditData] = useState({
    firstName: user?.person?.firstName || '',
    lastName: user?.person?.lastName || '',
    phone: user?.person?.phone || '',
  });

  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const handleSaveProfile = () => {
    // TODO: Implementar actualización de perfil
    setIsEditing(false);
  };

  const handleChangePassword = () => {
    // TODO: Implementar cambio de contraseña
    setShowChangePassword(false);
    setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
  };

  const handleLogout = () => {
    logout();
  };

  // Agrupar permisos por categoría
  const permissionsByCategory = permissions.reduce((acc, permission) => {
    // Buscar la categoría del permiso
    let category = 'other';
    for (const key of Object.keys(PERMISSION_CATEGORIES)) {
      if (permission.toLowerCase().includes(key.replace('-', '_'))) {
        category = key;
        break;
      }
    }
    if (!acc[category]) {
      acc[category] = [];
    }
    acc[category].push(permission);
    return acc;
  }, {} as Record<string, string[]>);

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-100">
        <p className="text-neutral-500">No hay información de usuario disponible</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Mi Cuenta"
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Tarjeta de perfil principal */}
        <div className="lg:col-span-2 space-y-6">
          {/* Info básica */}
          <Card className="overflow-hidden">
            <div className="bg-linear-to-r from-primary-500 to-primary-600 px-6 py-8 text-white">
              <div className="flex items-center gap-6">
                <Avatar
                  src={user.person?.avatar}
                  name={`${user.person?.firstName} ${user.person?.lastName}`}
                  size="lg"
                  className="ring-3 ring-white/30"
                />
                <div className="flex-1">
                  <h2 className="text-2xl font-bold">
                    {user.person?.firstName} {user.person?.lastName}
                  </h2>
                  <p className="text-primary-100 flex items-center gap-2 mt-1">
                    <FiShield className="w-4 h-4" />
                    {user.rolUser?.name || 'Usuario'}
                  </p>
                  {user.church && (
                    <p className="text-primary-100 flex items-center gap-2 mt-1">
                      <IconChurch />
                      {user.church.name}
                    </p>
                  )}
                </div>
                {!isEditing ? (
                  <Button
                    variant="secondary"
                    onClick={() => setIsEditing(true)}
                    className="bg-white/10 border-white/20 text-white hover:bg-white/20"
                  >
                    <FiEdit2 className="w-4 h-4 mr-2" />
                    Editar
                  </Button>
                ) : (
                  <div className="flex gap-2">
                    <Button
                      variant="primary"
                      onClick={handleSaveProfile}
                      className="bg-white text-primary-600 hover:bg-primary-50"
                    >
                      <FiSave className="w-4 h-4 mr-2" />
                      Guardar
                    </Button>
                    <Button
                      variant="secondary"
                      onClick={() => setIsEditing(false)}
                      className="bg-white/10 border-white/20 text-white hover:bg-white/20"
                    >
                      <FiX className="w-4 h-4" />
                    </Button>
                  </div>
                )}
              </div>
            </div>

            <div className="p-6">
              <h3 className="text-lg font-semibold text-neutral-900 mb-4 flex items-center gap-2">
                <FiUser className="w-5 h-5 text-primary-500" />
                Información Personal
              </h3>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {isEditing ? (
                  <>
                    <div>
                      <label className="text-sm font-medium text-neutral-700 mb-2 block">
                        Nombre
                      </label>
                      <Input
                        value={editData.firstName}
                        onChange={(e) => setEditData({ ...editData, firstName: e.target.value })}
                        placeholder="Tu nombre"
                      />
                    </div>
                    <div>
                      <label className="text-sm font-medium text-neutral-700 mb-2 block">
                        Apellido
                      </label>
                      <Input
                        value={editData.lastName}
                        onChange={(e) => setEditData({ ...editData, lastName: e.target.value })}
                        placeholder="Tu apellido"
                      />
                    </div>
                    <div>
                      <label className="text-sm font-medium text-neutral-700 mb-2 block">
                        Teléfono / WhatsApp
                      </label>
                      <Input
                        value={editData.phone}
                        onChange={(e) => setEditData({ ...editData, phone: e.target.value })}
                        placeholder="+57 300 123 4567"
                      />
                    </div>
                  </>
                ) : (
                  <>
                    <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                      <div className="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
                        <FiUser className="w-5 h-5 text-primary-600" />
                      </div>
                      <div>
                        <p className="text-xs text-neutral-500">Nombre completo</p>
                        <p className="font-medium text-neutral-900">
                          {user.person?.firstName} {user.person?.lastName}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                      <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                        <FiMail className="w-5 h-5 text-blue-600" />
                      </div>
                      <div>
                        <p className="text-xs text-neutral-500">Correo electrónico</p>
                        <p className="font-medium text-neutral-900">
                          {user.user || 'No registrado'}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                      <div className="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center">
                        <FiPhone className="w-5 h-5 text-green-600" />
                      </div>
                      <div>
                        <p className="text-xs text-neutral-500">Teléfono / WhatsApp</p>
                        <p className="font-medium text-neutral-900">
                          {user.person?.phone || 'No registrado'}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                      <div className="w-10 h-10 rounded-full bg-purple-100 flex items-center justify-center">
                        <FiCalendar className="w-5 h-5 text-purple-600" />
                      </div>
                      <div>
                        <p className="text-xs text-neutral-500">Fecha de nacimiento</p>
                        <p className="font-medium text-neutral-900">
                          {user.person?.birthDate 
                            ? formatDateForDisplay(user.person.birthDate, 'date')
                            : 'No registrada'
                          }
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                      <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center">
                        <FiMapPin className="w-5 h-5 text-amber-600" />
                      </div>
                      <div>
                        <p className="text-xs text-neutral-500">Documento (CC)</p>
                        <p className="font-medium text-neutral-900">
                          {user.person?.cc || 'No registrado'}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-3 p-3 bg-neutral-50 rounded-lg">
                      <div className="w-10 h-10 rounded-full bg-indigo-100 flex items-center justify-center">
                        <FiShield className="w-5 h-5 text-indigo-600" />
                      </div>
                      <div>
                        <p className="text-xs text-neutral-500">Tipo de persona</p>
                        <p className="font-medium text-neutral-900">
                          {user.person?.typePerson?.name || 'No especificado'}
                        </p>
                      </div>
                    </div>
                  </>
                )}
              </div>
            </div>
          </Card>

          {/* Cambiar contraseña */}
          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
                  <FiLock className="w-5 h-5 text-primary-500" />
                  Seguridad
                </h3>
                {!showChangePassword && (
                  <Button
                    variant="secondary"
                    onClick={() => setShowChangePassword(true)}
                  >
                    Cambiar contraseña
                  </Button>
                )}
              </div>

              {showChangePassword ? (
                <div className="space-y-4">
                  <div className="bg-amber-50 border border-amber-200 rounded-lg p-4 text-sm text-amber-800">
                    <p className="font-medium mb-1">⚠️ Importante</p>
                    <p>Asegúrate de usar una contraseña segura con al menos 8 caracteres, incluyendo mayúsculas, minúsculas y números.</p>
                  </div>

                  <div>
                    <label className="text-sm font-medium text-neutral-700 mb-2 block">
                      Contraseña actual
                    </label>
                    <Input
                      type="password"
                      value={passwordData.currentPassword}
                      onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                      placeholder="••••••••"
                    />
                  </div>

                  <div>
                    <label className="text-sm font-medium text-neutral-700 mb-2 block">
                      Nueva contraseña
                    </label>
                    <Input
                      type="password"
                      value={passwordData.newPassword}
                      onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                      placeholder="••••••••"
                    />
                  </div>

                  <div>
                    <label className="text-sm font-medium text-neutral-700 mb-2 block">
                      Confirmar nueva contraseña
                    </label>
                    <Input
                      type="password"
                      value={passwordData.confirmPassword}
                      onChange={(e) => setPasswordData({ ...passwordData, confirmPassword: e.target.value })}
                      placeholder="••••••••"
                    />
                  </div>

                  <div className="flex gap-2 pt-2">
                    <Button variant="primary" onClick={handleChangePassword}>
                      <FiCheck className="w-4 h-4 mr-2" />
                      Actualizar contraseña
                    </Button>
                    <Button variant="secondary" onClick={() => {
                      setShowChangePassword(false);
                      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
                    }}>
                      Cancelar
                    </Button>
                  </div>
                </div>
              ) : (
                <p className="text-neutral-500 text-sm">
                  Por seguridad, te recomendamos cambiar tu contraseña periódicamente.
                </p>
              )}
            </div>
          </Card>
        </div>

        {/* Sidebar - Permisos y acciones */}
        <div className="space-y-6">
          {/* Iglesia */}
          {user.church && (
            <Card>
              <div className="p-6">
                <h3 className="text-lg font-semibold text-neutral-900 flex items-center gap-2 mb-4">
                  <IconChurch />
                  Mi Iglesia
                </h3>
                <div className="space-y-3">
                  <div className="bg-primary-50 rounded-lg p-4">
                    <p className="font-semibold text-primary-900">{user.church.name}</p>
                    {user.church.city && (
                      <p className="text-sm text-primary-700 mt-1 flex items-center gap-1">
                        <FiMapPin className="w-3 h-3" />
                        {user.church.city.name}
                      </p>
                    )}
                    {user.church.states && (
                      <p className="text-sm text-primary-600 flex items-center gap-1">
                        {user.church.states.name}
                      </p>
                    )}
                  </div>
                </div>
              </div>
            </Card>
          )}

          {/* Permisos */}
          <Card>
            <div className="p-6">
              <h3 className="text-lg font-semibold text-neutral-900 flex items-center gap-2 mb-4">
                <FiShield className="w-5 h-5 text-primary-500" />
                Mis Permisos
              </h3>
              
              {permissions.length === 0 ? (
                <p className="text-neutral-500 text-sm">No tienes permisos asignados</p>
              ) : (
                <div className="space-y-3">
                  {Object.entries(permissionsByCategory).map(([category, perms]) => {
                    const categoryInfo = PERMISSION_CATEGORIES[category as keyof typeof PERMISSION_CATEGORIES];
                    return (
                      <div key={category} className="bg-neutral-50 rounded-lg p-3">
                        <p className="font-medium text-neutral-700 flex items-center gap-2 mb-2">
                          <span>{categoryInfo?.icon || '📋'}</span>
                          {categoryInfo?.label || category}
                        </p>
                        <div className="flex flex-wrap gap-1">
                          {perms.map((perm) => (
                            <span 
                              key={perm}
                              className="text-xs bg-white border border-neutral-200 text-neutral-600 px-2 py-1 rounded-full"
                            >
                              {perm.split('_').pop()?.toLowerCase()}
                            </span>
                          ))}
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </Card>

          {/* Cerrar sesión */}
          <Card className="border-red-200 bg-red-50">
            <div className="p-6">
              <h3 className="text-lg font-semibold text-red-900 flex items-center gap-2 mb-2">
                <FiLogOut className="w-5 h-5" />
                Cerrar Sesión
              </h3>
              <p className="text-red-700 text-sm mb-4">
                Cierra tu sesión actual en este dispositivo.
              </p>
              <Button
                variant="primary"
                onClick={handleLogout}
                className="w-full bg-red-600 hover:bg-red-700 border-red-600"
              >
                <FiLogOut className="w-4 h-4 mr-2" />
                Cerrar Sesión
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
