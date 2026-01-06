import { memo } from 'react';
import { Modal, Button } from '../../../components/shared';
import type { DeleteOfferingModalProps } from './types';

function DeleteOfferingModal({
  isOpen,
  onClose,
  onConfirm,
  isDeleting,
}: DeleteOfferingModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Eliminar Ofrenda"
    >
      <div className="space-y-4">
        <p className="text-neutral-600">
          ¿Estás seguro de que deseas eliminar esta ofrenda? Esta acción no se puede deshacer.
        </p>
        <div className="flex justify-end gap-3">
          <Button variant="secondary" onClick={onClose}>
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
      </div>
    </Modal>
  );
}

export default memo(DeleteOfferingModal);
