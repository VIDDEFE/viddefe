import { useMemo, useCallback } from 'react';
import { useInfinitePeople, usePerson } from '../../hooks';
import DropDown from './DropDown';
import Avatar from './Avatar';

interface PersonSelectorProps {
  label?: string;
  value?: string;
  onChangeValue?: (value: string) => void;
  error?: string;
  className?: string;
  placeholder?: string;
  disabled?: boolean;
  showSelectedPreview?: boolean;
  allowEmpty?: boolean;
  emptyLabel?: string;
}

/**
 * Selector de personas con paginación infinita.
 * Carga datos bajo demanda al hacer scroll en el dropdown.
 */
export default function PersonSelector({
  label = 'Persona',
  value,
  onChangeValue,
  error,
  className = '',
  placeholder = 'Seleccionar persona...',
  disabled = false,
  showSelectedPreview = false,
  allowEmpty = false,
  emptyLabel = 'Sin asignar',
}: Readonly<PersonSelectorProps>) {
  // Cargar la persona seleccionada directamente por ID
  const { data: selectedPerson, isLoading: isLoadingPerson } = usePerson(value || undefined);
  
  // Cargar lista con paginación infinita para el dropdown
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
  
  const options = useMemo(() => {
    const baseOptions = peopleList.map((person) => ({
      value: person.id,
      label: `${person.firstName} ${person.lastName}`,
      avatar: person.avatar,
    }));
    
    if (allowEmpty) {
      return [{ value: '', label: emptyLabel }, ...baseOptions];
    }
    
    return baseOptions;
  }, [peopleList, allowEmpty, emptyLabel]);

  const handleLoadMore = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  return (
    <div className={className}>
      {showSelectedPreview && selectedPerson && !isLoadingPerson && (
        <div className="flex items-center gap-2 mb-2 p-2 bg-primary-50 rounded-lg">
          <Avatar
            src={selectedPerson.avatar}
            name={`${selectedPerson.firstName} ${selectedPerson.lastName}`}
            size="sm"
          />
          <span className="text-sm text-primary-800 font-medium">
            {selectedPerson.firstName} {selectedPerson.lastName}
          </span>
        </div>
      )}
      {showSelectedPreview && isLoadingPerson && value && (
        <div className="flex items-center gap-2 mb-2 p-2 bg-primary-50 rounded-lg animate-pulse">
          <div className="w-8 h-8 bg-primary-200 rounded-full" />
          <div className="h-4 w-32 bg-primary-200 rounded" />
        </div>
      )}
      <DropDown
        label={label}
        options={options}
        value={value || ''}
        onChangeValue={onChangeValue}
        placeholder={placeholder}
        error={error}
        searchKey="label"
        disabled={disabled}
        hasMore={hasNextPage}
        isLoadingMore={isFetchingNextPage}
        onLoadMore={handleLoadMore}
      />
    </div>
  );
}
