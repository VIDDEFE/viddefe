import { useMemo, useCallback } from 'react';
import { useInfinitePeople, usePerson } from '../../hooks';
import DropDown from './DropDown';
import Avatar from './Avatar';

interface PastorSelectorProps {
  label?: string;
  value?: string;
  onChangeValue?: (value: string) => void;
  error?: string;
  className?: string;
  placeholder?: string;
}

export default function PastorSelector({
  label = 'Pastor',
  value,
  onChangeValue,
  error,
  className = '',
  placeholder = 'Seleccionar pastor...',
}: Readonly<PastorSelectorProps>) {
  // Cargar el pastor seleccionado directamente por ID
  const { data: selectedPerson, isLoading: isLoadingPerson } = usePerson(value);
  
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
  
  const options = useMemo(() => 
    peopleList.map((person) => ({
      value: person.id,
      label: `${person.firstName} ${person.lastName}`,
      avatar: person.avatar,
    })), [peopleList]);

  const handleLoadMore = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  return (
    <div className={className}>
      {selectedPerson && !isLoadingPerson && (
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
      {isLoadingPerson && value && (
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
        hasMore={hasNextPage}
        isLoadingMore={isFetchingNextPage}
        onLoadMore={handleLoadMore}
      />
    </div>
  );
}
