import React, { useState } from 'react';
import DropDown from './DropDown';
import { useStates, useCities } from '../../hooks/useStateCities';

type RenderProps = {
  states: Array<{ id: number; name: string }> | undefined;
  cities: Array<{ id: number; name: string }> | undefined;
  selectedStateId: number | undefined;
  selectedCityId: number | undefined;
  setSelectedStateId: (id?: number) => void;
  setSelectedCityId: (id?: number) => void;
};

interface Props {
  onCityChange?: (cityName: string | null) => void;
  onStateChange?: (stateName: string | null) => void;
  children?: (props: RenderProps) => React.ReactNode;
}

export default function StateCityDropdown({ onCityChange, onStateChange, children }: Props) {
  const { data: states } = useStates();
  const [selectedStateId, setSelectedStateId] = useState<number | undefined>(undefined);
  const { data: cities } = useCities(selectedStateId);
  const [selectedCityId, setSelectedCityId] = useState<number | undefined>(undefined);

  function handleStateChange(val: string) {
    const id = val ? Number(val) : undefined;
    setSelectedStateId(id);
    setSelectedCityId(undefined);
    const name = states?.find((s) => s.id === id)?.name ?? null;
    onStateChange && onStateChange(name);
  }

  function handleCityChange(val: string) {
    const id = val ? Number(val) : undefined;
    setSelectedCityId(id);
    const name = cities?.find((c) => c.id === id)?.name ?? null;
    onCityChange && onCityChange(name);
  }

  if (children) {
    return <>{children({ states, cities, selectedStateId, selectedCityId, setSelectedStateId, setSelectedCityId })}</>;
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
      <DropDown
        label="Estado"
        options={(states ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
        value={selectedStateId ? String(selectedStateId) : ''}
        onChangeValue={handleStateChange}
      />

      <DropDown
        label="Ciudad"
        options={(cities ?? []).map((c) => ({ value: String(c.id), label: c.name }))}
        value={selectedCityId ? String(selectedCityId) : ''}
        onChangeValue={handleCityChange}
        disabled={!selectedStateId}
      />
    </div>
  );
}
