import { memo } from 'react';
import { Modal, Button } from '../shared';
import { FiAlertTriangle } from 'react-icons/fi';

interface MeetingDeleteModalProps {
  readonly isOpen: boolean;
  readonly meetingName?: string;
  readonly onClose: () => void;
  readonly onConfirm: () => void;
  readonly isDeleting: boolean;
}

function MeetingDeleteModal({
  isOpen,
  meetingName,
  onClose,
  onConfirm,
  isDeleting,
}: MeetingDeleteModalProps) {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Eliminar Reunión">
      <div className="text-center py-4">
        <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <FiAlertTriangle className="text-red-600 text-2xl" />
        </div>
        <h3 className="text-lg font-semibold text-neutral-800 mb-2">
          ¿Estás seguro?
        </h3>
        <p className="text-neutral-600 mb-6">
          Esta acción eliminará permanentemente la reunión
          {meetingName && <strong className="block mt-1">"{meetingName}"</strong>}
          y no se podrá deshacer.
        </p>
        <div className="flex justify-center gap-3">
          <Button variant="secondary" onClick={onClose} disabled={isDeleting}>
            Cancelar
          </Button>
          <Button variant="danger" onClick={onConfirm} disabled={isDeleting}>
            {isDeleting ? 'Eliminando...' : 'Eliminar'}
          </Button>
        </div>
      </div>
    </Modal>
  );
}

export default memo(MeetingDeleteModal);
