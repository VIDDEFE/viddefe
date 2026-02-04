import { Modal, Button } from '../shared';
import type { HomeGroup } from '../../models';

interface HomeGroupViewModalProps {
  isOpen: boolean;
  group: HomeGroup | null;
  isLoading?: boolean;
  onEdit?: () => void;
  onClose: () => void;
}

export default function HomeGroupViewModal({
  isOpen,
  group,
  isLoading = false,
  onEdit,
  onClose,
}: HomeGroupViewModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      title="Detalles del Grupo"
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          {onEdit && (
            <Button variant="primary" onClick={onEdit}>
              Editar
            </Button>
          )}
          <Button variant="secondary" onClick={onClose}>
            Cerrar
          </Button>
        </div>
      }
    >
      {isLoading ? (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
          <span className="ml-2 text-neutral-600">Cargando datos...</span>
        </div>
      ) : group ? (
        <div className="space-y-4">
          <div>
            <label className="font-semibold text-primary-900 text-sm">
              Nombre
            </label>
            <p className="text-neutral-700 mt-1">{group.name}</p>
          </div>

          {group.description && (
            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Descripción
              </label>
              <p className="text-neutral-700 mt-1 whitespace-pre-wrap">
                {group.description}
              </p>
            </div>
          )}

          <div>
            <label className="font-semibold text-primary-900 text-sm">
              Estrategia
            </label>
            <p className="text-neutral-700 mt-1">
              {group.strategy ? (
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                  {group.strategy.name}
                </span>
              ) : (
                <span className="text-neutral-400">Sin estrategia asignada</span>
              )}
            </p>
          </div>

          <div>
            <label className="font-semibold text-primary-900 text-sm">
              Responsable
            </label>
            <p className="text-neutral-700 mt-1">
              {group.manager ? (
                <span className="inline-flex items-center gap-2">
                  <span className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-800 font-medium text-sm">
                    {group.manager.firstName?.[0]}{group.manager.lastName?.[0]}
                  </span>
                  {group.manager.firstName} {group.manager.lastName}
                </span>
              ) : (
                <span className="text-neutral-400">Sin responsable asignado</span>
              )}
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Latitud
              </label>
              <p className="text-neutral-700 mt-1">{group.latitude}</p>
            </div>

            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Longitud
              </label>
              <p className="text-neutral-700 mt-1">{group.longitude}</p>
            </div>
          </div>
        </div>
      ) : (
        <p className="text-neutral-500 text-center py-8">
          No se encontró información del grupo
        </p>
      )}
    </Modal>
  );
}
