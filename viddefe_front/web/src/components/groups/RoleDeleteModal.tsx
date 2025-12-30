import { memo } from 'react';
import { Modal, Button } from '../shared';
import type { RoleStrategyNode } from '../../models';
import { FiAlertTriangle, FiUsers } from 'react-icons/fi';

interface RoleDeleteModalProps {
  isOpen: boolean;
  role: RoleStrategyNode | null;
  onConfirm: () => void;
  onClose: () => void;
  isDeleting?: boolean;
}

// Contar total de personas en el árbol recursivamente
function countPeopleInTree(node: RoleStrategyNode): number {
  let count = node.people?.length || 0;
  if (node.children) {
    for (const child of node.children) {
      count += countPeopleInTree(child);
    }
  }
  return count;
}

// Contar total de sub-roles recursivamente
function countSubRoles(node: RoleStrategyNode): number {
  let count = node.children?.length || 0;
  if (node.children) {
    for (const child of node.children) {
      count += countSubRoles(child);
    }
  }
  return count;
}

export default memo(function RoleDeleteModal({
  isOpen,
  role,
  onConfirm,
  onClose,
  isDeleting = false,
}: RoleDeleteModalProps) {
  if (!role) return null;

  const totalPeople = countPeopleInTree(role);
  const totalSubRoles = countSubRoles(role);
  const hasContent = totalPeople > 0 || totalSubRoles > 0;

  return (
    <Modal
      isOpen={isOpen}
      title="Eliminar Rol"
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          <Button variant="secondary" onClick={onClose} disabled={isDeleting}>
            Cancelar
          </Button>
          <Button
            variant="danger"
            onClick={onConfirm}
            disabled={isDeleting}
          >
            {isDeleting ? 'Eliminando...' : 'Eliminar'}
          </Button>
        </div>
      }
    >
      <div className="space-y-4">
        {/* Icono de advertencia */}
        <div className="flex justify-center">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center">
            <FiAlertTriangle className="w-8 h-8 text-red-600" />
          </div>
        </div>

        {/* Mensaje principal */}
        <div className="text-center">
          <h3 className="text-lg font-semibold text-neutral-800 mb-2">
            ¿Eliminar el rol "{role.name}"?
          </h3>
          <p className="text-neutral-600">
            Esta acción no se puede deshacer.
          </p>
        </div>

        {/* Advertencias si tiene contenido */}
        {hasContent && (
          <div className="bg-amber-50 border border-amber-200 rounded-lg p-4">
            <div className="flex items-start gap-3">
              <FiAlertTriangle className="w-5 h-5 text-amber-600 mt-0.5 shrink-0" />
              <div className="text-sm">
                <p className="font-semibold text-amber-800 mb-2">
                  Advertencia: Este rol tiene contenido asociado
                </p>
                <ul className="space-y-1 text-amber-700">
                  {totalSubRoles > 0 && (
                    <li className="flex items-center gap-2">
                      <span className="w-2 h-2 bg-amber-500 rounded-full" />
                      {totalSubRoles} sub-rol{totalSubRoles !== 1 ? 'es' : ''} que también será{totalSubRoles !== 1 ? 'n' : ''} eliminado{totalSubRoles !== 1 ? 's' : ''}
                    </li>
                  )}
                  {totalPeople > 0 && (
                    <li className="flex items-center gap-2">
                      <FiUsers className="w-4 h-4" />
                      {totalPeople} persona{totalPeople !== 1 ? 's' : ''} asignada{totalPeople !== 1 ? 's' : ''}
                    </li>
                  )}
                </ul>
              </div>
            </div>
          </div>
        )}

        {/* Info del rol a eliminar */}
        <div className="bg-neutral-50 border border-neutral-200 rounded-lg p-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-neutral-200 rounded-lg flex items-center justify-center">
              <span className="text-neutral-600 font-bold text-lg">
                {role.name[0]?.toUpperCase()}
              </span>
            </div>
            <div>
              <p className="font-medium text-neutral-800">{role.name}</p>
              <p className="text-sm text-neutral-500">
                {role.people?.length || 0} persona{role.people?.length !== 1 ? 's' : ''} • {' '}
                {role.children?.length || 0} sub-rol{role.children?.length !== 1 ? 'es' : ''}
              </p>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  );
});
