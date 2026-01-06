import { memo } from 'react';
import { Modal, Button } from '../shared';
import { FiCalendar, FiClock, FiFileText, FiTag } from 'react-icons/fi';
import type { Meeting } from '../../models';

interface MeetingViewModalProps {
  readonly isOpen: boolean;
  readonly meeting: Meeting | null;
  readonly onClose: () => void;
  readonly onEdit?: () => void;
  readonly onDelete?: () => void;
  readonly onViewAttendance?: () => void;
}

function MeetingViewModal({
  isOpen,
  meeting,
  onClose,
  onEdit,
  onDelete,
  onViewAttendance,
}: MeetingViewModalProps) {
  if (!meeting) return null;

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Detalle de Reunión">
      <div className="space-y-6">
        {/* Nombre */}
        <div>
          <h3 className="text-xl font-semibold text-neutral-800">{meeting.name}</h3>
          <span className="inline-flex items-center gap-1 mt-2 px-2 py-1 bg-violet-100 text-violet-700 rounded text-sm">
            <FiTag size={14} />
            {meeting.type?.name || 'Sin tipo'}
          </span>
        </div>

        {/* Información */}
        <div className="space-y-4">
          {/* Fecha */}
          <div className="flex items-start gap-3">
            <FiCalendar className="text-primary-600 mt-1" size={18} />
            <div>
              <p className="text-sm font-medium text-neutral-500">Fecha</p>
              <p className="text-neutral-800">{formatDate(meeting.date)}</p>
            </div>
          </div>

          {/* Hora */}
          <div className="flex items-start gap-3">
            <FiClock className="text-primary-600 mt-1" size={18} />
            <div>
              <p className="text-sm font-medium text-neutral-500">Hora</p>
              <p className="text-neutral-800">{formatTime(meeting.date)}</p>
            </div>
          </div>

          {/* Descripción */}
          {meeting.description && (
            <div className="flex items-start gap-3">
              <FiFileText className="text-primary-600 mt-1" size={18} />
              <div>
                <p className="text-sm font-medium text-neutral-500">Descripción</p>
                <p className="text-neutral-700 whitespace-pre-wrap">{meeting.description}</p>
              </div>
            </div>
          )}
        </div>

        {/* Acciones */}
        <div className="flex flex-wrap gap-3 pt-4 border-t border-neutral-200">
          {onViewAttendance && (
            <Button variant="primary" onClick={onViewAttendance}>
              Ver Asistencia
            </Button>
          )}
          {onEdit && (
            <Button variant="secondary" onClick={onEdit}>
              Editar
            </Button>
          )}
          {onDelete && (
            <Button variant="danger" onClick={onDelete}>
              Eliminar
            </Button>
          )}
          <Button variant="secondary" onClick={onClose}>
            Cerrar
          </Button>
        </div>
      </div>
    </Modal>
  );
}

export default memo(MeetingViewModal);
