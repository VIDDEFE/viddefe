import { Modal, Button } from '../shared';

interface HomeGroupDeleteModalProps {
  isOpen: boolean;
  groupName: string;
  onConfirm: () => void;
  onClose: () => void;
  isDeleting?: boolean;
}

export default function HomeGroupDeleteModal({
  isOpen,
  groupName,
  onConfirm,
  onClose,
  isDeleting = false,
}: HomeGroupDeleteModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      title="Eliminar Grupo"
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          <Button
            variant="danger"
            onClick={onConfirm}
            disabled={isDeleting}
          >
            {isDeleting ? 'Eliminando...' : 'Eliminar'}
          </Button>
          <Button variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
        </div>
      }
    >
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
          ¿Estás seguro de que deseas eliminar el grupo{' '}
          <strong className="text-primary-800">{groupName}</strong>?
        </p>
        <p className="text-neutral-500 text-sm mt-2">
          Esta acción no se puede deshacer.
        </p>
      </div>
    </Modal>
  );
}
