import { useState, useEffect } from 'react';
import { Input } from './Form';
import DropDown from './DropDown';
import { useStates } from '../../hooks';

export interface PersonFormData {
  cc: string;
  firstName: string;
  lastName: string;
  phone: string;
  avatar: string;
  birthDate: string;
  typePersonId: number;
  stateId: number;
  churchId?: string;
}

interface PersonFormProps {
  value: PersonFormData;
  onChange: (data: PersonFormData) => void;
  disabled?: boolean;
  showTypeSelector?: boolean;
  showChurchSelector?: boolean;
  errors?: Partial<Record<keyof PersonFormData, string>>;
  disabledTypePerson?: boolean;
}

const TYPE_PERSON_OPTIONS = [
  { value: '1', label: 'Oveja' },
  { value: '2', label: 'Voluntario' },
  { value: '3', label: 'Pastor' },
];

export function PersonForm({
  value,
  onChange,
  disabled = false,
  showTypeSelector = true,
  errors = {},
  disabledTypePerson = false,
}: PersonFormProps) {
  const { data: states } = useStates();
  const [, setSelectedStateId] = useState<number | undefined>(value.stateId || undefined);

  useEffect(() => {
    setSelectedStateId(value.stateId || undefined);
  }, [value.stateId]);

  const handleChange = (field: keyof PersonFormData, fieldValue: string | number) => {
    const processedValue = field === 'stateId' || field === 'typePersonId' 
      ? Number(fieldValue) 
      : fieldValue;
    
    onChange({
      ...value,
      [field]: processedValue,
    });
  };

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Número de Cédula"
          placeholder="12345678"
          value={value.cc}
          onChange={(e) => handleChange('cc', e.target.value)}
          disabled={disabled}
          error={errors.cc}
        />
        <Input
          label="Nombres"
          placeholder="Juan"
          value={value.firstName}
          onChange={(e) => handleChange('firstName', e.target.value)}
          disabled={disabled}
          error={errors.firstName}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Apellidos"
          placeholder="Pérez García"
          value={value.lastName}
          onChange={(e) => handleChange('lastName', e.target.value)}
          disabled={disabled}
          error={errors.lastName}
        />
        <Input
          label="Teléfono"
          placeholder="+569 1234 5678"
          value={value.phone}
          onChange={(e) => handleChange('phone', e.target.value)}
          disabled={disabled}
          error={errors.phone}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Fecha de Nacimiento"
          type="date"
          value={value.birthDate}
          onChange={(e) => handleChange('birthDate', e.target.value)}
          disabled={disabled}
          error={errors.birthDate}
        />

        {showTypeSelector && (
          <DropDown
            label="Tipo de Persona"
            options={TYPE_PERSON_OPTIONS}
            value={String(value.typePersonId)||String(3)}
            onChangeValue={(val: string) => handleChange('typePersonId', val)}
            searchKey="label"
            disabled={disabledTypePerson}
          />
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <DropDown
          label="Departamento/Estado"
          options={(states ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
          value={value.stateId ? String(value.stateId) : ''}
          onChangeValue={(val: string) => {
            const id = val ? Number(val) : 0;
            setSelectedStateId(id);
            handleChange('stateId', id);
          }}
          searchKey="label"
        />
      </div>
    </div>
  );
}

export const initialPersonFormData: PersonFormData = {
  cc: '',
  firstName: '',
  lastName: '',
  phone: '',
  avatar: '',
  birthDate: '',
  typePersonId: 1,
  stateId: 0,
  churchId: '',
};

export const initialPersonPastorFormData: PersonFormData = {
  cc: '',
  firstName: '',
  lastName: '',
  phone: '',
  avatar: '',
  birthDate: '',
  typePersonId: 3,
  stateId: 0,
  churchId: '',
};