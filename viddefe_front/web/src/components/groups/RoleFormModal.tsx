import { useState, useEffect, memo } from 'react';
import { Modal, Button, DropDown } from '../shared';
import type { RoleStrategyNode, CreateRoleDto, UpdateRoleDto } from '../../models';
import { FiAlertCircle } from 'react-icons/fi';

// ============================================================================
// TYPES
// ============================================================================

export interface RoleFormData {
  name: string;
  parentRoleId: string | null;
}

interface RoleFormModalProps {
  isOpen: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<RoleFormData>;
  parentRole?: RoleStrategyNode | null; // Rol padre al crear hijo
  strategyId: string;
  availableRoles: RoleStrategyNode[]; // Roles disponibles para seleccionar como padre
  onSave: (data: CreateRoleDto | UpdateRoleDto) => void;
  onClose: () => void;
  isSaving?: boolean;
}

// ============================================================================
// FLATTEN ROLES - Convierte árbol en lista plana para el selector
// ============================================================================

interface FlattenedRole {
  id: string;
  name: string;
  level: number;
  fullPath: string;
}

function flattenRoles(
  nodes: RoleStrategyNode[],
  level = 0,
  parentPath = ''
): FlattenedRole[] {
  const result: FlattenedRole[] = [];

  for (const node of nodes) {
    const fullPath = parentPath ? `${parentPath} > ${node.name}` : node.name;
    result.push({
      id: node.id,
      name: node.name,
      level,
      fullPath,
    });

    if (node.children && node.children.length > 0) {
      result.push(...flattenRoles(node.children, level + 1, fullPath));
    }
  }

  return result;
}

// ============================================================================
// ROLE FORM MODAL
// ============================================================================

export default memo(function RoleFormModal({
  isOpen,
  mode,
  initialData,
  parentRole,
  strategyId,
  availableRoles,
  onSave,
  onClose,
  isSaving = false,
}: RoleFormModalProps) {
  const [formData, setFormData] = useState<RoleFormData>({
    name: '',
    parentRoleId: null,
  });
  const [errors, setErrors] = useState<{ name?: string }>({});

  // Inicializar datos al abrir
  useEffect(() => {
    if (isOpen) {
      if (mode === 'edit' && initialData) {
        setFormData({
          name: initialData.name || '',
          parentRoleId: initialData.parentRoleId || null,
        });
      } else if (mode === 'create' && parentRole) {
        // Si estamos creando un hijo, preset el padre
        setFormData({
          name: '',
          parentRoleId: parentRole.id,
        });
      } else {
        setFormData({
          name: '',
          parentRoleId: null,
        });
      }
      setErrors({});
    }
  }, [isOpen, mode, initialData, parentRole]);

  // Obtener lista plana de roles para el selector
  const flattenedRoles = flattenRoles(availableRoles);

  // Validar formulario
  const validate = (): boolean => {
    const newErrors: { name?: string } = {};

    if (!formData.name.trim()) {
      newErrors.name = 'El nombre del rol es requerido';
    } else if (formData.name.trim().length < 2) {
      newErrors.name = 'El nombre debe tener al menos 2 caracteres';
    } else if (formData.name.trim().length > 100) {
      newErrors.name = 'El nombre no puede exceder 100 caracteres';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Manejar submit
  const handleSubmit = () => {
    if (!validate()) return;

    if (mode === 'create') {
      const createDto: CreateRoleDto = {
        strategyId,
        name: formData.name.trim(),
        parentRoleId: formData.parentRoleId || undefined,
      };
      onSave(createDto);
    } else {
      const updateDto: UpdateRoleDto = {
        name: formData.name.trim(),
        parentRoleId: formData.parentRoleId,
      };
      onSave(updateDto);
    }
  };

  // Título dinámico
  const title =
    mode === 'create'
      ? parentRole
        ? `Agregar Sub-rol a "${parentRole.name}"`
        : 'Agregar Nuevo Rol'
      : 'Editar Rol';

  return (
    <Modal
      isOpen={isOpen}
      title={title}
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          <Button variant="secondary" onClick={onClose} disabled={isSaving}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleSubmit} disabled={isSaving}>
            {isSaving ? 'Guardando...' : 'Guardar'}
          </Button>
        </div>
      }
    >
      <div className="space-y-5">
        {/* Nombre del Rol */}
        <div>
          <label
            htmlFor="roleName"
            className="block text-sm font-semibold text-neutral-700 mb-1"
          >
            Nombre del Rol <span className="text-red-500">*</span>
          </label>
          <input
            id="roleName"
            type="text"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="Ej: Líder de Zona, Coordinador, Anfitrión..."
            className={`
              w-full px-4 py-2.5 rounded-lg border transition-colors
              focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent
              ${errors.name ? 'border-red-500 bg-red-50' : 'border-neutral-300 hover:border-neutral-400'}
            `}
            autoFocus
          />
          {errors.name && (
            <div className="flex items-center gap-1.5 mt-1.5 text-red-600 text-sm">
              <FiAlertCircle size={14} />
              <span>{errors.name}</span>
            </div>
          )}
        </div>

        {/* Rol Padre (solo en modo crear sin parentRole preseleccionado, o en modo editar) */}
        {(mode === 'edit' || (mode === 'create' && !parentRole)) && flattenedRoles.length > 0 && (
          <div>
            <DropDown
              label="Rol Padre"
              value={formData.parentRoleId || ''}
              onChangeValue={(value) =>
                setFormData({
                  ...formData,
                  parentRoleId: value || null,
                })
              }
              options={[
                { value: '', label: 'Sin rol padre (nivel raíz)' },
                ...flattenedRoles.map((role) => ({
                  value: role.id,
                  label: `${'  '.repeat(role.level)}${role.level > 0 ? '└ ' : ''}${role.name}`
                }))
              ]}
              placeholder="Sin rol padre (nivel raíz)"
            />
            <p className="mt-1.5 text-xs text-neutral-500">
              Selecciona un rol padre si este rol debe ser un sub-rol de otro
            </p>
          </div>
        )}

        {/* Info del padre preseleccionado (solo cuando se crea hijo) */}
        {mode === 'create' && parentRole && (
          <div className="p-3 bg-violet-50 border border-violet-200 rounded-lg">
            <p className="text-sm text-violet-800">
              <span className="font-semibold">Rol padre:</span> {parentRole.name}
            </p>
            <p className="text-xs text-violet-600 mt-1">
              El nuevo rol será un sub-rol dentro de la jerarquía
            </p>
          </div>
        )}
      </div>
    </Modal>
  );
});
