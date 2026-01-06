import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  useMyHomeGroup, 
  useAssignPeopleToRole,
  useRemovePeopleFromRole
} from '../../hooks';
import { Card, Button, PageHeader } from '../../components/shared';
import RoleTree from '../../components/groups/RoleTree';
import RolePeopleAssignmentModal from '../../components/groups/RolePeopleAssignmentModal';
import { FiMapPin, FiUser, FiGrid, FiUsers } from 'react-icons/fi';
import type { RoleStrategyNode } from '../../models';

export default function MyGroup() {
  const navigate = useNavigate();
  const { data, isLoading, error } = useMyHomeGroup();

  // Hooks de mutación para asignación de personas
  const groupId = data?.homeGroup?.id || '';
  const assignPeopleMutation = useAssignPeopleToRole(groupId);
  const removePeopleMutation = useRemovePeopleFromRole(groupId);

  // Estado para modal de asignación de personas
  const [peopleModal, setPeopleModal] = useState<{
    isOpen: boolean;
    role: RoleStrategyNode | null;
  }>({
    isOpen: false,
    role: null,
  });

  // Abrir modal para gestionar personas de un rol
  const handleManagePeople = useCallback((node: RoleStrategyNode) => {
    setPeopleModal({
      isOpen: true,
      role: node,
    });
  }, []);

  // Cerrar modal de personas
  const handleClosePeopleModal = useCallback(() => {
    setPeopleModal({
      isOpen: false,
      role: null,
    });
  }, []);

  // Asignar personas a un rol
  const handleAssignPeople = useCallback((personIds: string[]) => {
    if (peopleModal.role && personIds.length > 0) {
      assignPeopleMutation.mutate(
        {
          roleId: peopleModal.role.id,
          peopleIds: personIds,
        },
        {
          onSuccess: () => {
            handleClosePeopleModal();
          },
        }
      );
    }
  }, [peopleModal.role, assignPeopleMutation, handleClosePeopleModal]);

  // Remover persona de un rol (desde el badge o modal)
  const handleRemovePerson = useCallback((roleId: string, personId: string) => {
    removePeopleMutation.mutate({ roleId, peopleIds: [personId] });
  }, [removePeopleMutation]);

  // Estado de carga
  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando tu grupo...</p>
          </div>
        </div>
      </div>
    );
  }

  // Estado de error o sin grupo
  if (error || !data) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FiUsers className="text-amber-600 text-2xl" />
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">
              No perteneces a ningún grupo
            </h2>
            <p className="text-neutral-600 mb-4">
              {error?.message || 'Aún no has sido asignado a un grupo de hogar'}
            </p>
            <Button variant="secondary" onClick={() => navigate('/groups')}>
              Ver todos los grupos
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const { homeGroup, strategy, hierarchy } = data;

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title="Mi Grupo"
        subtitle={homeGroup.name}
        actions={
          <Button variant="secondary" onClick={() => navigate('/groups')}>
            Ver todos los grupos
          </Button>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fadeIn">
        {/* Columna izquierda: Info básica + Estrategia */}
        <div className="space-y-6">
          {/* Información básica */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiGrid className="text-primary-600" />
              Información General
            </h3>

            <div className="space-y-4">
              {/* Nombre */}
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                  Nombre
                </span>
                <p className="text-neutral-800 font-medium mt-1">{homeGroup.name}</p>
              </div>

              {/* Descripción */}
              {homeGroup.description && (
                <div>
                  <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                    Descripción
                  </span>
                  <p className="text-neutral-700 mt-1 whitespace-pre-wrap">
                    {homeGroup.description}
                  </p>
                </div>
              )}

              {/* Ubicación */}
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider block">
                  Ubicación
                </span>
                <div className="flex items-center gap-2 mt-1">
                  <FiMapPin className="text-neutral-400" size={16} />
                  <span className="text-neutral-700 text-sm font-mono">
                    {homeGroup.latitude.toFixed(6)}, {homeGroup.longitude.toFixed(6)}
                  </span>
                </div>
                <a
                  href={`https://www.google.com/maps?q=${homeGroup.latitude},${homeGroup.longitude}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-primary-600 hover:text-primary-700 text-sm mt-1 inline-block"
                >
                  Ver en Google Maps →
                </a>
              </div>
            </div>
          </Card>

          {/* Líder */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
              <FiUser className="text-primary-600" />
              Líder del Grupo
            </h3>

            {homeGroup.leader ? (
              <div className="flex items-center gap-3">
                {homeGroup.leader.avatar ? (
                  <img
                    src={homeGroup.leader.avatar}
                    alt={`${homeGroup.leader.firstName} ${homeGroup.leader.lastName}`}
                    className="w-12 h-12 rounded-full object-cover"
                  />
                ) : (
                  <div className="w-12 h-12 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-semibold text-lg">
                    {homeGroup.leader.firstName?.[0]}
                    {homeGroup.leader.lastName?.[0]}
                  </div>
                )}
                <div>
                  <p className="font-medium text-neutral-800">
                    {homeGroup.leader.firstName} {homeGroup.leader.lastName}
                  </p>
                  {homeGroup.leader.phone && (
                    <p className="text-sm text-neutral-500">{homeGroup.leader.phone}</p>
                  )}
                </div>
              </div>
            ) : (
              <p className="text-neutral-500 text-center py-4">
                Sin líder asignado
              </p>
            )}
          </Card>

          {/* Estrategia */}
          <Card>
            <h3 className="text-lg font-semibold text-neutral-800 mb-4">
              Estrategia
            </h3>

            {strategy ? (
              <div className="inline-flex items-center px-4 py-2 bg-violet-50 border border-violet-200 rounded-lg">
                <span className="w-3 h-3 bg-violet-500 rounded-full mr-3" />
                <span className="font-medium text-violet-800">{strategy.name}</span>
              </div>
            ) : (
              <p className="text-neutral-500 text-center py-4">
                Sin estrategia asignada
              </p>
            )}
          </Card>
        </div>

        {/* Columna derecha: Jerarquía de roles (2 columnas) */}
        <div className="lg:col-span-2">
          <Card className="h-full">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-800">
                Estructura de Roles
              </h3>
              {strategy && (
                <p className="text-sm text-neutral-500">
                  Estrategia: <span className="font-medium text-violet-700">{strategy.name}</span>
                </p>
              )}
            </div>

            {/* Info sobre gestión de personas */}
            {strategy && (
              <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg">
                <p className="text-sm text-green-800">
                  Haz clic en el ícono de personas en cada rol para asignar o remover miembros.
                </p>
              </div>
            )}

            <div className="border border-neutral-200 rounded-lg p-4 bg-neutral-50/50 min-h-75">
              <RoleTree
                hierarchy={hierarchy}
                emptyMessage={
                  strategy
                    ? 'Esta estrategia no tiene roles definidos. Configúralos desde "Gestionar Estrategias".'
                    : 'Asigna una estrategia al grupo para poder ver y asignar roles'
                }
                // Acciones de asignación de personas
                onManagePeople={strategy ? handleManagePeople : undefined}
                onRemovePerson={strategy ? handleRemovePerson : undefined}
              />
            </div>

            {/* Leyenda */}
            {hierarchy && hierarchy.length > 0 && (
              <div className="mt-4 pt-4 border-t border-neutral-200">
                <p className="text-xs text-neutral-500 mb-2">Leyenda:</p>
                <div className="flex flex-wrap gap-4 text-xs text-neutral-600">
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-primary-500" />
                    <span>Nivel 1</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-violet-500" />
                    <span>Nivel 2</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-blue-500" />
                    <span>Nivel 3</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 border-l-2 border-emerald-500" />
                    <span>Nivel 4+</span>
                  </div>
                </div>
              </div>
            )}
          </Card>
        </div>
      </div>

      {/* ========== MODALES ========== */}

      {/* Modal para asignar personas a un rol */}
      <RolePeopleAssignmentModal
        isOpen={peopleModal.isOpen}
        role={peopleModal.role}
        onAssign={handleAssignPeople}
        onRemove={(personId) => {
          if (peopleModal.role) {
            handleRemovePerson(peopleModal.role.id, personId);
          }
        }}
        onClose={handleClosePeopleModal}
        isSaving={assignPeopleMutation.isPending || removePeopleMutation.isPending}
      />
    </div>
  );
}
