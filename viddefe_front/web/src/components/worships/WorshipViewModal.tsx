import { Modal, Button } from '../shared';
import type { Worship } from '../../models';

interface WorshipViewModalProps {
  isOpen: boolean;
  worship: Worship | null;
  isLoading?: boolean;
  onEdit?: () => void;
  onClose: () => void;
}

// Helper para formatear fecha
function formatDateTime(isoDate: string): string {
  try {
    const date = new Date(isoDate);
    return date.toLocaleString('es-ES', {
      dateStyle: 'long',
      timeStyle: 'short',
    });
  } catch {
    return isoDate;
  }
}

export default function WorshipViewModal({
  isOpen,
  worship,
  isLoading = false,
  onEdit,
  onClose,
}: WorshipViewModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      title="Detalles del Culto"
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
      ) : worship ? (
        <div className="space-y-4">
          <div>
            <label className="font-semibold text-primary-900 text-sm">
              Nombre
            </label>
            <p className="text-neutral-700 mt-1">{worship.name}</p>
          </div>

          {worship.description && (
            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Descripción
              </label>
              <p className="text-neutral-700 mt-1 whitespace-pre-wrap">
                {worship.description}
              </p>
            </div>
          )}

          <div>
            <label className="font-semibold text-primary-900 text-sm">
              Tipo de Culto
            </label>
            <p className="text-neutral-700 mt-1">
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                {worship.worshipType.name}
              </span>
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Fecha Programada
              </label>
              <p className="text-neutral-700 mt-1">
                {formatDateTime(worship.scheduledDate)}
              </p>
            </div>

            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Fecha de Creación
              </label>
              <p className="text-neutral-700 mt-1">
                {formatDateTime(worship.creationDate)}
              </p>
            </div>
          </div>
        </div>
      ) : (
        <p className="text-neutral-500 text-center py-8">
          No se encontró información del culto
        </p>
      )}
    </Modal>
  );
}
