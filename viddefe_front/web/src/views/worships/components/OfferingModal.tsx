import { memo, useCallback } from 'react';
import { Modal, Button, DropDown } from '../../../components/shared';
import type { OfferingModalProps } from './types';

function OfferingModal({
  isOpen,
  onClose,
  formData,
  onFormChange,
  onSave,
  isEditing,
  isSaving,
  offeringTypes,
  people,
}: Readonly<OfferingModalProps>) {
  
  const handleAmountChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    onFormChange({ ...formData, amount: e.target.value });
  }, [formData, onFormChange]);

  const handleTypeChange = useCallback((value: string) => {
    onFormChange({ ...formData, offeringTypeId: value });
  }, [formData, onFormChange]);

  const handlePersonChange = useCallback((value: string) => {
    onFormChange({ ...formData, peopleId: value });
  }, [formData, onFormChange]);

  const typeOptions = offeringTypes.map((type) => ({ 
    value: String(type.id), 
    label: type.name 
  }));

  const personOptions = [
    { value: '', label: 'Anónimo' },
    ...people.map((person) => ({
      value: person.id,
      label: `${person.firstName} ${person.lastName}`
    }))
  ];

  const isDisabled = !formData.amount || !formData.offeringTypeId || isSaving;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEditing ? 'Editar Ofrenda' : 'Nueva Ofrenda'}
    >
      <div className="space-y-4">
        {/* Tipo de ofrenda */}
        <DropDown
          label="Tipo de Ofrenda *"
          value={formData.offeringTypeId}
          onChangeValue={handleTypeChange}
          options={typeOptions}
          placeholder="Seleccionar tipo"
        />
        
        {/* Monto */}
        <div>
          <label 
            htmlFor="offering-amount"
            className="block text-sm font-medium text-neutral-700 mb-1"
          >
            Monto *
          </label>
          <div className="relative">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-500">$</span>
            <input
              id="offering-amount"
              type="number"
              step="0.01"
              min="0.01"
              value={formData.amount}
              onChange={handleAmountChange}
              placeholder="0.00"
              className="w-full pl-8 pr-3 py-2 border border-neutral-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            />
          </div>
        </div>
        
        {/* Persona (opcional) */}
        <DropDown
          label="Persona (opcional - dejar vacío para anónimo)"
          value={formData.peopleId}
          onChangeValue={handlePersonChange}
          options={personOptions}
          placeholder="Anónimo"
        />
        
        {/* Botones */}
        <div className="flex justify-end gap-3 pt-4 border-t border-neutral-200">
          <Button variant="secondary" onClick={onClose}>
            Cancelar
          </Button>
          <Button 
            onClick={onSave}
            disabled={isDisabled}
          >
            {isSaving ? 'Guardando...' : 'Guardar'}
          </Button>
        </div>
      </div>
    </Modal>
  );
}

export default memo(OfferingModal);
