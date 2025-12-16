import type { Cities, States } from '../../services/stateCitiesService';
import { Modal, Button } from '../shared';
import ChurchForm, { type ChurchFormData } from './ChurchForm';

interface ChurchFormModalProps {
  isOpen: boolean;
  mode: 'create' | 'edit';
  formData: ChurchFormData;
  onFormChange: (data: ChurchFormData) => void;
  onSave: () => void;
  onClose: () => void;
  isLoading?: boolean;
  isSaving?: boolean;
  states?: States[];
  cities?: Cities[];
}

export default function ChurchFormModal({
  isOpen,
  mode,
  formData,
  onFormChange,
  onSave,
  onClose,
  isLoading = false,
  isSaving = false,
  states = [],
  cities = [],
}: ChurchFormModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      title={mode === 'create' ? 'Agregar Nueva Iglesia' : 'Editar Iglesia'}
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          <Button
            variant="primary"
            onClick={onSave}
            disabled={isSaving || isLoading}
          >
            {isSaving ? 'Guardando...' : 'Guardar'}
          </Button>
          <Button variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
        </div>
      }
    >
      {mode === 'edit' && isLoading ? (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          <span className="ml-2 text-neutral-600">Cargando datos...</span>
        </div>
      ) : (
        <ChurchForm
          value={formData}
          onChange={onFormChange}
          states={states}
          cities={cities}
        />
      )}
    </Modal>
  );
}
