import { Modal, Button } from '../shared';
import WorshipForm, { type WorshipFormData } from './WorshipForm';
import type { MeetingType } from '../../models';

interface WorshipFormModalProps {
  isOpen: boolean;
  mode: 'create' | 'edit';
  formData: WorshipFormData;
  onFormChange: (patch: Partial<WorshipFormData>) => void;
  onSave: () => void;
  onClose: () => void;
  isLoading?: boolean;
  isSaving?: boolean;
  worshipTypes?: MeetingType[];
  errors?: Partial<Record<keyof WorshipFormData, string>>;
}

export default function WorshipFormModal({
  isOpen,
  mode,
  formData,
  onFormChange,
  onSave,
  onClose,
  isLoading = false,
  isSaving = false,
  worshipTypes = [],
  errors = {},
}: WorshipFormModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      title={mode === 'create' ? 'Agregar Nuevo Culto' : 'Editar Culto'}
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
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
          <span className="ml-2 text-neutral-600">Cargando datos...</span>
        </div>
      ) : (
        <WorshipForm
          value={formData}
          onChange={onFormChange}
          worshipTypes={worshipTypes}
          errors={errors}
        />
      )}
    </Modal>
  );
}
