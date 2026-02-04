import { Modal, Button } from '../shared';
import type { WorshipDetail } from '../../models';
import { FiUsers } from 'react-icons/fi';
import { formatDateForDisplay } from '../../utils/helpers';

interface WorshipViewModalProps {
  isOpen: boolean;
  worship: WorshipDetail | null;
  isLoading?: boolean;
  onEdit?: () => void;
  onClose: () => void;
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
                {worship.type?.name}
              </span>
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Fecha Programada
              </label>
              <p className="text-neutral-700 mt-1">
                {formatDateForDisplay(worship.scheduledDate, 'full')}
              </p>
            </div>

            <div>
              <label className="font-semibold text-primary-900 text-sm">
                Fecha de Creación
              </label>
              <p className="text-neutral-700 mt-1">
                {formatDateForDisplay(worship.creationDate, 'full')}
              </p>
            </div>
          </div>

          {/* Estadísticas de asistencia */}
          <div className="border-t border-neutral-200 pt-4 mt-4">
            <div className="flex items-center gap-2 mb-3">
              <FiUsers className="text-primary-600" size={18} />
              <label className="font-semibold text-primary-900 text-sm">
                Asistencia
              </label>
            </div>
            
            <div className="grid grid-cols-3 gap-3 mb-4">
              <div className="bg-neutral-50 rounded-lg p-3 text-center">
                <p className="text-2xl font-bold text-neutral-800">{worship.totalAttendance ?? 0}</p>
                <p className="text-xs text-neutral-500">Total</p>
              </div>
              <div className="bg-green-50 rounded-lg p-3 text-center">
                <p className="text-2xl font-bold text-green-600">{worship.presentCount ?? 0}</p>
                <p className="text-xs text-green-600">Presentes</p>
              </div>
              <div className="bg-red-50 rounded-lg p-3 text-center">
                <p className="text-2xl font-bold text-red-600">{worship.absentCount ?? 0}</p>
                <p className="text-xs text-red-600">Ausentes</p>
              </div>
            </div>

            <p className="text-neutral-500 text-sm text-center py-2 bg-neutral-50 rounded-lg">
              Para ver la lista completa de asistencia, accede al detalle del culto
            </p>
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
