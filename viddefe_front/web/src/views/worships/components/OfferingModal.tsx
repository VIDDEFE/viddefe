import { memo, useCallback, useMemo } from 'react';
import { Modal, Button, DropDown } from '../../../components/shared';
import { useInfinitePeople, usePerson } from '../../../hooks';
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
}: Readonly<OfferingModalProps>) {
  
  // Cargar la persona seleccionada por ID (si existe)
  const { data: selectedPerson } = usePerson(formData.peopleId || undefined);
  
  // Cargar lista con paginación infinita para el dropdown de personas
  const { 
    data: peoplePages, 
    fetchNextPage, 
    hasNextPage, 
    isFetchingNextPage 
  } = useInfinitePeople({ size: 20 });
  
  // Aplanar todas las páginas en una sola lista
  const peopleList = useMemo(() => {
    if (!peoplePages?.pages) return [];
    return peoplePages.pages.flatMap(page => page.content ?? []);
  }, [peoplePages]);
  
  const handleAmountChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    onFormChange({ ...formData, amount: e.target.value });
  }, [formData, onFormChange]);

  const handleTypeChange = useCallback((value: string) => {
    onFormChange({ ...formData, offeringTypeId: value });
  }, [formData, onFormChange]);

  const handlePersonChange = useCallback((value: string) => {
    onFormChange({ ...formData, peopleId: value });
  }, [formData, onFormChange]);

  const handleLoadMorePeople = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const typeOptions = offeringTypes.map((type) => ({ 
    value: String(type.id), 
    label: type.name 
  }));

  const personOptions = useMemo(() => [
    { value: '', label: 'Anónimo' },
    ...peopleList.map((person) => ({
      value: person.id,
      label: `${person.firstName} ${person.lastName}`
    }))
  ], [peopleList]);

  const isDisabled = !formData.amount || !formData.offeringTypeId || isSaving;

  // Mostrar el nombre de la persona seleccionada (si no está en la lista paginada)
  const selectedPersonLabel = selectedPerson 
    ? `${selectedPerson.firstName} ${selectedPerson.lastName}` 
    : undefined;

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
        
        {/* Persona seleccionada (si existe y no está en la lista) */}
        {selectedPersonLabel && formData.peopleId && (
          <div className="flex items-center gap-2 p-2 bg-primary-50 rounded-lg text-sm">
            <span className="text-primary-700">Persona actual:</span>
            <span className="font-medium text-primary-800">{selectedPersonLabel}</span>
          </div>
        )}
        
        {/* Persona (opcional) */}
        <DropDown
          label="Persona (opcional - dejar vacío para anónimo)"
          value={formData.peopleId}
          onChangeValue={handlePersonChange}
          options={personOptions}
          placeholder="Anónimo"
          hasMore={hasNextPage}
          isLoadingMore={isFetchingNextPage}
          onLoadMore={handleLoadMorePeople}
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
