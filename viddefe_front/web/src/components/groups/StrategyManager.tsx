import { useState, useCallback } from 'react';
import { Button, Modal, Form, Input } from '../shared';
import {
  useStrategies,
  useCreateStrategy,
  useUpdateStrategy,
  useDeleteStrategy,
  useStrategyRoles,
  useCreateRole,
  useUpdateRole,
  useDeleteRole,
} from '../../hooks';
import RoleTree from './RoleTree';
import RoleFormModal from './RoleFormModal';
import RoleDeleteModal from './RoleDeleteModal';
import type { Strategy, RoleStrategyNode, CreateRoleDto, UpdateRoleDto } from '../../models';
import { FiEdit2, FiTrash2, FiArrowLeft, FiGrid } from 'react-icons/fi';

interface StrategyManagerProps {
  isOpen: boolean;
  onClose: () => void;
}

type ViewMode = 'list' | 'create' | 'edit' | 'delete' | 'roles';

export default function StrategyManager({ isOpen, onClose }: StrategyManagerProps) {
  const { data: strategies = [], isLoading } = useStrategies();

  const createStrategy = useCreateStrategy();
  const updateStrategy = useUpdateStrategy();
  const deleteStrategy = useDeleteStrategy();

  const [mode, setMode] = useState<ViewMode>('list');
  const [selectedStrategy, setSelectedStrategy] = useState<Strategy | null>(null);
  const [strategyName, setStrategyName] = useState('');
  const [error, setError] = useState('');

  // ========== ROLES STATE ==========
  // Query de roles para la estrategia seleccionada
  const { data: roles = [], isLoading: isLoadingRoles } = useStrategyRoles(
    selectedStrategy?.id
  );

  // Hooks de mutación para roles
  const createRoleMutation = useCreateRole(selectedStrategy?.id || '');
  const updateRoleMutation = useUpdateRole(selectedStrategy?.id || '');
  const deleteRoleMutation = useDeleteRole(selectedStrategy?.id || '');

  // Estado para modales de rol
  const [roleModal, setRoleModal] = useState<{
    isOpen: boolean;
    mode: 'create' | 'edit';
    parentRole: RoleStrategyNode | null;
    editingRole: RoleStrategyNode | null;
  }>({
    isOpen: false,
    mode: 'create',
    parentRole: null,
    editingRole: null,
  });

  const [deleteRoleModal, setDeleteRoleModal] = useState<{
    isOpen: boolean;
    role: RoleStrategyNode | null;
  }>({
    isOpen: false,
    role: null,
  });

  // ========== STRATEGY HANDLERS ==========
  const resetState = () => {
    setMode('list');
    setSelectedStrategy(null);
    setStrategyName('');
    setError('');
  };

  const handleCreate = () => {
    if (!strategyName.trim()) {
      setError('El nombre es requerido');
      return;
    }
    createStrategy.mutate(
      { name: strategyName.trim() },
      {
        onSuccess: () => resetState(),
        onError: () => setError('Error al crear la estrategia'),
      }
    );
  };

  const handleUpdate = () => {
    if (!selectedStrategy || !strategyName.trim()) {
      setError('El nombre es requerido');
      return;
    }
    updateStrategy.mutate(
      { id: selectedStrategy.id, data: { name: strategyName.trim() } },
      {
        onSuccess: () => resetState(),
        onError: () => setError('Error al actualizar la estrategia'),
      }
    );
  };

  const handleDelete = () => {
    if (!selectedStrategy) return;
    deleteStrategy.mutate(selectedStrategy.id, {
      onSuccess: () => resetState(),
      onError: () => setError('Error al eliminar la estrategia'),
    });
  };

  const openCreate = () => {
    setMode('create');
    setStrategyName('');
    setError('');
  };

  const openEdit = (strategy: Strategy) => {
    setSelectedStrategy(strategy);
    setStrategyName(strategy.name);
    setMode('edit');
    setError('');
  };

  const openDelete = (strategy: Strategy) => {
    setSelectedStrategy(strategy);
    setMode('delete');
    setError('');
  };

  const openRoles = (strategy: Strategy) => {
    setSelectedStrategy(strategy);
    setMode('roles');
    setError('');
  };

  // ========== ROLE HANDLERS ==========

  // Abrir modal para crear rol raíz
  const handleCreateRootRole = useCallback(() => {
    setRoleModal({
      isOpen: true,
      mode: 'create',
      parentRole: null,
      editingRole: null,
    });
  }, []);

  // Abrir modal para crear sub-rol
  const handleCreateChildRole = useCallback((parentNode: RoleStrategyNode) => {
    setRoleModal({
      isOpen: true,
      mode: 'create',
      parentRole: parentNode,
      editingRole: null,
    });
  }, []);

  // Abrir modal para editar rol
  const handleEditRole = useCallback((node: RoleStrategyNode) => {
    setRoleModal({
      isOpen: true,
      mode: 'edit',
      parentRole: null,
      editingRole: node,
    });
  }, []);

  // Abrir modal de confirmación para eliminar
  const handleDeleteRoleClick = useCallback((node: RoleStrategyNode) => {
    setDeleteRoleModal({
      isOpen: true,
      role: node,
    });
  }, []);

  // Cerrar modal de rol
  const handleCloseRoleModal = useCallback(() => {
    setRoleModal({
      isOpen: false,
      mode: 'create',
      parentRole: null,
      editingRole: null,
    });
  }, []);

  // Cerrar modal de eliminar
  const handleCloseDeleteRoleModal = useCallback(() => {
    setDeleteRoleModal({
      isOpen: false,
      role: null,
    });
  }, []);

  // Guardar rol (crear o editar)
  const handleSaveRole = useCallback(
    (roleData: CreateRoleDto | UpdateRoleDto) => {
      if (roleModal.mode === 'create') {
        createRoleMutation.mutate(roleData as CreateRoleDto, {
          onSuccess: () => handleCloseRoleModal(),
        });
      } else if (roleModal.editingRole) {
        updateRoleMutation.mutate(
          {
            roleId: roleModal.editingRole.id,
            data: roleData as UpdateRoleDto,
          },
          {
            onSuccess: () => handleCloseRoleModal(),
          }
        );
      }
    },
    [roleModal, createRoleMutation, updateRoleMutation, handleCloseRoleModal]
  );

  // Confirmar eliminación de rol
  const handleConfirmDeleteRole = useCallback(() => {
    if (deleteRoleModal.role) {
      deleteRoleMutation.mutate(deleteRoleModal.role.id, {
        onSuccess: () => handleCloseDeleteRoleModal(),
      });
    }
  }, [deleteRoleModal.role, deleteRoleMutation, handleCloseDeleteRoleModal]);

  const isMutating =
    createStrategy.isPending ||
    updateStrategy.isPending ||
    deleteStrategy.isPending;

  // ========== RENDER HELPERS ==========

  const renderActions = () => {
    switch (mode) {
      case 'list':
        return (
          <div className="flex gap-2">
            <Button variant="primary" onClick={openCreate}>
              + Nueva Estrategia
            </Button>
            <Button variant="secondary" onClick={onClose}>
              Cerrar
            </Button>
          </div>
        );
      case 'create':
        return (
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleCreate} disabled={isMutating}>
              {createStrategy.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
            <Button variant="secondary" onClick={resetState}>
              Cancelar
            </Button>
          </div>
        );
      case 'edit':
        return (
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleUpdate} disabled={isMutating}>
              {updateStrategy.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
            <Button variant="secondary" onClick={resetState}>
              Cancelar
            </Button>
          </div>
        );
      case 'delete':
        return (
          <div className="flex gap-2">
            <Button variant="danger" onClick={handleDelete} disabled={isMutating}>
              {deleteStrategy.isPending ? 'Eliminando...' : 'Eliminar'}
            </Button>
            <Button variant="secondary" onClick={resetState}>
              Cancelar
            </Button>
          </div>
        );
      case 'roles':
        return (
          <div className="flex gap-2">
            <Button variant="secondary" onClick={resetState}>
              <span className="flex items-center gap-2">
                <FiArrowLeft size={14} />
                Volver
              </span>
            </Button>
          </div>
        );
      default:
        return null;
    }
  };

  const renderContent = () => {
    // Vista de roles de una estrategia
    if (mode === 'roles' && selectedStrategy) {
      return (
        <div className="space-y-4">
          {/* Header con nombre de estrategia */}
          <div className="flex items-center gap-3 pb-4 border-b border-neutral-200">
            <div className="w-10 h-10 rounded-lg bg-violet-100 flex items-center justify-center">
              <FiGrid className="text-violet-600" size={20} />
            </div>
            <div>
              <h4 className="font-semibold text-neutral-800">
                {selectedStrategy.name}
              </h4>
              <p className="text-sm text-neutral-500">
                Configurar estructura de roles
              </p>
            </div>
          </div>

          {/* Árbol de roles */}
          {isLoadingRoles ? (
            <div className="flex justify-center items-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
              <span className="ml-2 text-neutral-600">Cargando roles...</span>
            </div>
          ) : (
            <div className="border border-neutral-200 rounded-lg p-4 bg-neutral-50/50 min-h-[250px]">
              <RoleTree
                hierarchy={roles}
                emptyMessage="Esta estrategia no tiene roles. ¡Crea el primero!"
                onCreateRoot={handleCreateRootRole}
                onCreateChild={handleCreateChildRole}
                onEdit={handleEditRole}
                onDelete={handleDeleteRoleClick}
              />
            </div>
          )}

          {/* Info sobre roles */}
          <div className="p-3 bg-blue-50 border border-blue-200 rounded-lg">
            <p className="text-sm text-blue-800">
              <strong>Nota:</strong> Los roles definidos aquí son la estructura que usarán
              todos los grupos que utilicen esta estrategia. Las personas se asignan
              desde cada grupo individual.
            </p>
          </div>
        </div>
      );
    }

    // Vista de lista
    if (mode === 'list') {
      if (isLoading) {
        return (
          <div className="flex justify-center items-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
            <span className="ml-2 text-neutral-600">Cargando estrategias...</span>
          </div>
        );
      }

      if (strategies.length === 0) {
        return (
          <div className="text-center py-8 text-neutral-500">
            No hay estrategias creadas. ¡Crea la primera!
          </div>
        );
      }

      return (
        <ul className="divide-y divide-neutral-200">
          {strategies.map((strategy) => (
            <li
              key={strategy.id}
              className="flex items-center justify-between py-3 px-2 hover:bg-neutral-50 rounded-lg transition-colors"
            >
              <span className="font-medium text-neutral-800">{strategy.name}</span>
              <div className="flex gap-1">
                <button
                  onClick={() => openRoles(strategy)}
                  className="px-3 py-1.5 text-sm text-violet-600 hover:text-violet-800 hover:bg-violet-50 rounded-lg transition-colors font-medium"
                  title="Configurar Roles"
                >
                  Roles
                </button>
                <button
                  onClick={() => openEdit(strategy)}
                  className="p-2 text-primary-600 hover:text-primary-800 hover:bg-primary-50 rounded-lg transition-colors"
                  title="Editar nombre"
                >
                  <FiEdit2 size={16} />
                </button>
                <button
                  onClick={() => openDelete(strategy)}
                  className="p-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-colors"
                  title="Eliminar"
                >
                  <FiTrash2 size={16} />
                </button>
              </div>
            </li>
          ))}
        </ul>
      );
    }

    // Vista de crear/editar
    if (mode === 'create' || mode === 'edit') {
      return (
        <Form>
          <Input
            label="Nombre de la Estrategia"
            placeholder="Ej: Discipulado"
            value={strategyName}
            onChange={(e) => {
              setStrategyName(e.target.value);
              if (error) setError('');
            }}
            error={error}
            autoFocus
          />
        </Form>
      );
    }

    // Vista de eliminar
    if (mode === 'delete' && selectedStrategy) {
      return (
        <div className="text-center py-4">
          <div className="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
            <svg
              className="h-6 w-6 text-red-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
          </div>
          <p className="text-neutral-700">
            ¿Estás seguro de que deseas eliminar la estrategia{' '}
            <strong className="text-primary-800">{selectedStrategy.name}</strong>?
          </p>
          <p className="text-neutral-500 text-sm mt-2">
            Los grupos asociados a esta estrategia quedarán sin estrategia.
          </p>
          {error && <p className="text-red-600 text-sm mt-2">{error}</p>}
        </div>
      );
    }

    return null;
  };

  const getTitle = () => {
    switch (mode) {
      case 'roles':
        return 'Configurar Roles';
      default:
        return 'Gestionar Estrategias';
    }
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        title={getTitle()}
        onClose={() => {
          resetState();
          onClose();
        }}
        actions={renderActions()}
      >
        {renderContent()}
      </Modal>

      {/* Modal para crear/editar rol */}
      <RoleFormModal
        isOpen={roleModal.isOpen}
        mode={roleModal.mode}
        initialData={
          roleModal.editingRole
            ? { name: roleModal.editingRole.name, parentRoleId: null }
            : undefined
        }
        parentRole={roleModal.parentRole}
        strategyId={selectedStrategy?.id || ''}
        availableRoles={roles}
        onSave={handleSaveRole}
        onClose={handleCloseRoleModal}
        isSaving={createRoleMutation.isPending || updateRoleMutation.isPending}
      />

      {/* Modal de confirmación para eliminar rol */}
      <RoleDeleteModal
        isOpen={deleteRoleModal.isOpen}
        role={deleteRoleModal.role}
        onConfirm={handleConfirmDeleteRole}
        onClose={handleCloseDeleteRoleModal}
        isDeleting={deleteRoleMutation.isPending}
      />
    </>
  );
}
