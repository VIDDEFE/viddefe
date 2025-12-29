import { useState, memo, useCallback } from 'react';
import type { Person, RoleStrategyNode } from '../../models';
import { FiChevronRight, FiChevronDown, FiUsers, FiUser, FiPlus, FiEdit2, FiTrash2, FiMoreVertical, FiUserPlus, FiX } from 'react-icons/fi';

// ============================================================================
// PERSON BADGE - Muestra una persona asignada a un rol (con opción de remover)
// ============================================================================
interface PersonBadgeProps {
  person: Person;
  roleId: string;
  onRemove?: (roleId: string, personId: string) => void;
}

const PersonBadge = memo(function PersonBadge({ person, roleId, onRemove }: PersonBadgeProps) {
  const initials = `${person.firstName?.[0] || ''}${person.lastName?.[0] || ''}`.toUpperCase();
  const fullName = `${person.firstName} ${person.lastName}`.trim();

  return (
    <div className="group inline-flex items-center gap-2 px-3 py-1.5 bg-neutral-50 border border-neutral-200 rounded-full text-sm hover:border-neutral-300 transition-colors">
      {person.avatar ? (
        <img
          src={person.avatar}
          alt={fullName}
          className="w-6 h-6 rounded-full object-cover"
        />
      ) : (
        <span className="w-6 h-6 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium text-xs">
          {initials || <FiUser size={12} />}
        </span>
      )}
      <span className="text-neutral-700">{fullName || 'Sin nombre'}</span>
      {onRemove && (
        <button
          onClick={(e) => {
            e.stopPropagation();
            onRemove(roleId, person.id);
          }}
          className="opacity-0 group-hover:opacity-100 p-0.5 -mr-1 text-neutral-400 hover:text-red-500 transition-all"
          title="Remover persona"
        >
          <FiX size={14} />
        </button>
      )}
    </div>
  );
});

// ============================================================================
// ACTION BUTTONS - Botones de acción para cada nodo
// ============================================================================
interface ActionButtonsProps {
  node: RoleStrategyNode;
  onCreateChild?: (node: RoleStrategyNode) => void;
  onEdit?: (node: RoleStrategyNode) => void;
  onDelete?: (node: RoleStrategyNode) => void;
  onManagePeople?: (node: RoleStrategyNode) => void;
}

const ActionButtons = memo(function ActionButtons({
  node,
  onCreateChild,
  onEdit,
  onDelete,
  onManagePeople,
}: ActionButtonsProps) {
  const [isOpen, setIsOpen] = useState(false);

  const handleToggle = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsOpen(!isOpen);
  };

  const handleAction = (e: React.MouseEvent, action: () => void) => {
    e.stopPropagation();
    action();
    setIsOpen(false);
  };

  // Si no hay acciones, no mostrar nada
  if (!onCreateChild && !onEdit && !onDelete && !onManagePeople) return null;

  return (
    <div className="relative">
      <button
        onClick={handleToggle}
        className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-600 hover:bg-neutral-100 transition-colors"
        title="Acciones"
      >
        <FiMoreVertical size={16} />
      </button>

      {isOpen && (
        <>
          {/* Overlay para cerrar al hacer clic fuera */}
          <div
            className="fixed inset-0 z-10"
            onClick={(e) => {
              e.stopPropagation();
              setIsOpen(false);
            }}
          />

          {/* Menú de acciones */}
          <div className="absolute right-0 top-full mt-1 z-20 bg-white border border-neutral-200 rounded-lg shadow-lg py-1 min-w-[180px]">
            {/* Acción principal: Gestionar personas */}
            {onManagePeople && (
              <>
                <button
                  onClick={(e) => handleAction(e, () => onManagePeople(node))}
                  className="w-full flex items-center gap-2 px-3 py-2 text-sm text-neutral-700 hover:bg-primary-50 transition-colors"
                >
                  <FiUserPlus size={14} className="text-primary-600" />
                  Gestionar Personas
                </button>
                {(onCreateChild || onEdit || onDelete) && (
                  <hr className="my-1 border-neutral-200" />
                )}
              </>
            )}
            {onCreateChild && (
              <button
                onClick={(e) => handleAction(e, () => onCreateChild(node))}
                className="w-full flex items-center gap-2 px-3 py-2 text-sm text-neutral-700 hover:bg-neutral-50 transition-colors"
              >
                <FiPlus size={14} className="text-emerald-600" />
                Agregar Sub-rol
              </button>
            )}
            {onEdit && (
              <button
                onClick={(e) => handleAction(e, () => onEdit(node))}
                className="w-full flex items-center gap-2 px-3 py-2 text-sm text-neutral-700 hover:bg-neutral-50 transition-colors"
              >
                <FiEdit2 size={14} className="text-blue-600" />
                Editar Rol
              </button>
            )}
            {onDelete && (
              <>
                {(onCreateChild || onEdit || onManagePeople) && (
                  <hr className="my-1 border-neutral-200" />
                )}
                <button
                  onClick={(e) => handleAction(e, () => onDelete(node))}
                  className="w-full flex items-center gap-2 px-3 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                >
                  <FiTrash2 size={14} />
                  Eliminar Rol
                </button>
              </>
            )}
          </div>
        </>
      )}
    </div>
  );
});

// ============================================================================
// ROLE NODE - Nodo individual del árbol (recursivo)
// ============================================================================
interface RoleNodeProps {
  node: RoleStrategyNode;
  level: number;
  isLastChild?: boolean;
  parentHasSiblings?: boolean;
  onCreateChild?: (node: RoleStrategyNode) => void;
  onEdit?: (node: RoleStrategyNode) => void;
  onDelete?: (node: RoleStrategyNode) => void;
  onManagePeople?: (node: RoleStrategyNode) => void;
  onRemovePerson?: (roleId: string, personId: string) => void;
}

const RoleNode = memo(function RoleNode({ 
  node, 
  level,
  isLastChild = false,
  parentHasSiblings = false,
  onCreateChild,
  onEdit,
  onDelete,
  onManagePeople,
  onRemovePerson,
}: RoleNodeProps) {
  const [isExpanded, setIsExpanded] = useState(true);
  const hasChildren = node.children && node.children.length > 0;
  const hasPeople = node.people && node.people.length > 0;
  const hasContent = hasChildren || hasPeople;

  const toggleExpand = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (hasContent) {
      setIsExpanded((prev) => !prev);
    }
  };

  const hasActions = onCreateChild || onEdit || onDelete || onManagePeople;

  return (
    <div className="relative flex">
      {/* Líneas verticales y horizontales del árbol */}
      {level > 0 && (
        <>
          {/* Línea vertical que conecta con el padre */}
          <div className="absolute top-0 left-4 w-px h-full bg-neutral-300" />
          
          {/* Línea horizontal que conecta al nodo */}
          <div className="absolute top-5 left-4 w-4 h-px bg-neutral-300" />
          
          {/* Ajustar la línea vertical para el último hijo */}
          {isLastChild && (
            <div className="absolute top-0 left-4 w-px h-5 bg-transparent" />
          )}
        </>
      )}

      {/* Contenido del nodo */}
      <div className="flex-1 pl-8">
        {/* Header del nodo */}
        <div
          className={`group flex items-center gap-2 py-3 px-4 rounded-lg transition-colors ${
            hasContent ? 'cursor-pointer hover:bg-neutral-50' : 'hover:bg-neutral-50/50'
          }`}
          onClick={toggleExpand}
        >
          {/* Icono de expansión */}
          <span className="w-5 h-5 flex items-center justify-center text-neutral-400">
            {hasChildren ? (
              isExpanded ? (
                <FiChevronDown size={16} />
              ) : (
                <FiChevronRight size={16} />
              )
            ) : (
              <span className="w-2 h-2 rounded-full bg-neutral-300" />
            )}
          </span>

          {/* Nombre del rol con fondo */}
          <div className="flex-1 flex items-center gap-3">
            <div className="px-3 py-1.5 bg-white border border-neutral-300 rounded-lg shadow-sm hover:shadow transition-shadow">
              <span className="font-medium text-neutral-800">{node.name}</span>
            </div>

            {/* Contador de personas */}
            {hasPeople && (
              <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-primary-100 text-primary-700 rounded-full text-xs font-medium">
                <FiUsers size={12} />
                {node.people.length}
              </span>
            )}

            {/* Contador de sub-roles */}
            {hasChildren && (
              <span className="text-xs text-neutral-400">
                ({node.children.length} sub-rol{node.children.length !== 1 ? 'es' : ''})
              </span>
            )}
          </div>

          {/* Botones de acción - visibles al hover */}
          {hasActions && (
            <div className="opacity-0 group-hover:opacity-100 transition-opacity">
              <ActionButtons
                node={node}
                onCreateChild={onCreateChild}
                onEdit={onEdit}
                onDelete={onDelete}
                onManagePeople={onManagePeople}
              />
            </div>
          )}
        </div>

        {/* Personas asignadas */}
        {isExpanded && hasPeople && (
          <div className="ml-8 mb-3">
            <div className="flex flex-wrap gap-2">
              {node.people.map((person) => (
                <PersonBadge 
                  key={person.id} 
                  person={person} 
                  roleId={node.id}
                  onRemove={onRemovePerson}
                />
              ))}
            </div>
          </div>
        )}

        {/* Hijos recursivos */}
        {isExpanded && hasChildren && (
          <div className="mt-1">
            {node.children.map((child, index) => (
              <RoleNode 
                key={child.id} 
                node={child} 
                level={level + 1}
                isLastChild={index === node.children.length - 1}
                parentHasSiblings={node.children.length > 1}
                onCreateChild={onCreateChild}
                onEdit={onEdit}
                onDelete={onDelete}
                onManagePeople={onManagePeople}
                onRemovePerson={onRemovePerson}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
});

// ============================================================================
// ROLE TREE - Componente principal del árbol
// ============================================================================
interface RoleTreeProps {
  hierarchy: RoleStrategyNode[];
  emptyMessage?: string;
  /** Acciones de estructura (edición de roles) - normalmente solo en configuración */
  onCreateChild?: (node: RoleStrategyNode) => void;
  onEdit?: (node: RoleStrategyNode) => void;
  onDelete?: (node: RoleStrategyNode) => void;
  onCreateRoot?: () => void;
  /** Acciones de asignación de personas - en vista de detalle del grupo */
  onManagePeople?: (node: RoleStrategyNode) => void;
  onRemovePerson?: (roleId: string, personId: string) => void;
}

export default function RoleTree({
  hierarchy,
  emptyMessage = 'No hay roles definidos para este grupo',
  onCreateChild,
  onEdit,
  onDelete,
  onCreateRoot,
  onManagePeople,
  onRemovePerson,
}: RoleTreeProps) {
  const handleCreateRoot = useCallback(() => {
    if (onCreateRoot) {
      onCreateRoot();
    }
  }, [onCreateRoot]);

  if (!hierarchy || hierarchy.length === 0) {
    return (
      <div className="text-center py-12 text-neutral-500">
        <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-neutral-100 flex items-center justify-center">
          <FiUsers className="w-8 h-8 text-neutral-400" />
        </div>
        <p className="mb-4 text-neutral-600">{emptyMessage}</p>
        {onCreateRoot && (
          <button
            onClick={handleCreateRoot}
            className="inline-flex items-center gap-2 px-5 py-2.5 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors text-sm font-medium shadow-sm"
          >
            <FiPlus size={18} />
            Crear Primer Rol
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="p-4">
      {/* Botón para agregar rol raíz */}
      {onCreateRoot && (
        <div className="flex justify-end mb-6">
          <button
            onClick={handleCreateRoot}
            className="inline-flex items-center gap-2 px-4 py-2.5 bg-white border border-neutral-300 text-neutral-700 hover:bg-neutral-50 rounded-lg transition-colors text-sm font-medium shadow-sm hover:shadow"
          >
            <FiPlus size={16} />
            Agregar Rol Raíz
          </button>
        </div>
      )}

      {/* Contenedor del árbol con fondo */}
      <div className="bg-white border border-neutral-200 rounded-xl p-4 shadow-sm">
        {hierarchy.map((node, index) => (
          <RoleNode 
            key={node.id} 
            node={node} 
            level={0}
            isLastChild={index === hierarchy.length - 1}
            onCreateChild={onCreateChild}
            onEdit={onEdit}
            onDelete={onDelete}
            onManagePeople={onManagePeople}
            onRemovePerson={onRemovePerson}
          />
        ))}
      </div>

      {/* Leyenda del árbol */}
      <div className="mt-6 p-3 bg-neutral-50 rounded-lg border border-neutral-200">
        <div className="flex items-center gap-4 text-xs text-neutral-600">
          <div className="flex items-center gap-2">
            <FiChevronDown className="text-neutral-400" size={14} />
            <span>Rol expandido</span>
          </div>
          <div className="flex items-center gap-2">
            <FiChevronRight className="text-neutral-400" size={14} />
            <span>Rol colapsado</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-neutral-300" />
            <span>Rol sin sub-roles</span>
          </div>
        </div>
      </div>
    </div>
  );
}