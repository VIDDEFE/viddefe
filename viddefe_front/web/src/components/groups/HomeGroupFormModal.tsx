import { Modal, Button } from '../shared';
import HomeGroupForm, { type HomeGroupFormData } from './HomeGroupForm';
import type { Strategy, Person } from '../../models';

interface HomeGroupFormModalProps {
  isOpen: boolean;
  mode: 'create' | 'edit';
  formData: HomeGroupFormData;
  onFormChange: (patch: Partial<HomeGroupFormData>) => void;
  onSave: () => void;
  onClose: () => void;
  isLoading?: boolean;
  isSaving?: boolean;
  strategies?: Strategy[];
  people?: Person[];
  isLoadingStrategies?: boolean;
  isLoadingPeople?: boolean;
  errors?: Partial<Record<keyof HomeGroupFormData, string>>;
}

export default function HomeGroupFormModal({
  isOpen,
  mode,
  formData,
  onFormChange,
  onSave,
  onClose,
  isLoading = false,
  isSaving = false,
  strategies = [],
  people = [],
  isLoadingStrategies = false,
  isLoadingPeople = false,
  errors = {},
}: HomeGroupFormModalProps) {
  return (
    <Modal
      isOpen={isOpen}
      title={mode === 'create' ? 'Agregar Nuevo Grupo' : 'Editar Grupo'}
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
        <HomeGroupForm
          value={formData}
          onChange={onFormChange}
          strategies={strategies}
          people={people}
          isLoadingStrategies={isLoadingStrategies}
          isLoadingPeople={isLoadingPeople}
          errors={errors}
        />
      )}
    </Modal>
  );
}
