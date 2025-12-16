import type { Cities, States } from '../../services/stateCitiesService';
import { Form, Input, DropDown, PastorSelector } from '../shared/index';
import MapPicker from '../shared/MapPicker';

export interface ChurchFormData {
  name: string;
  email: string;
  phone: string;
  foundationDate: string;
  latitude: number | undefined;
  longitude: number | undefined;
  pastorId: string;
  stateId: number | undefined;
  cityId: number | undefined;
}

export const initialChurchFormData: ChurchFormData = {
  name: '',
  email: '',
  phone: '',
  foundationDate: '',
  latitude: undefined,
  longitude: undefined,
  pastorId: '',
  stateId: undefined,
  cityId: undefined,
};

interface ChurchFormProps {
  value: ChurchFormData;
  onChange: (data: ChurchFormData) => void;
  states?: States[];
  cities?: Cities[];
  disabled?: boolean;
  errors?: Partial<Record<keyof ChurchFormData, string>>;
}

export default function ChurchForm({
  value,
  onChange,
  states = [],
  cities = [],
  disabled = false,
  errors = {},
}: ChurchFormProps) {
  const updateField = <K extends keyof ChurchFormData>(field: K, val: ChurchFormData[K]) => {
    onChange({ ...value, [field]: val });
  };
  console.log('ChurchForm render with value:', value);
  return (
    <Form>
      <Input
        label="Nombre"
        placeholder="Nombre de la iglesia"
        value={value.name}
        onChange={(e) => updateField('name', e.target.value)}
        disabled={disabled}
        error={errors.name}
      />

      <PastorSelector
        label="Pastor"
        value={value.pastorId}
        onChangeValue={(val) => updateField('pastorId', val)}
        placeholder="Seleccionar pastor..."
      />

      <Input
        label="Fecha de Fundación"
        type="date"
        value={value.foundationDate ? new Date(value.foundationDate).toISOString().split('T')[0] : ''}
        onChange={(e) => updateField('foundationDate', e.target.value)}
        disabled={disabled}
        error={errors.foundationDate}
      />

      <Input
        label="Email"
        type="email"
        placeholder="correo@iglesia.com"
        value={value.email}
        onChange={(e) => updateField('email', e.target.value)}
        disabled={disabled}
        error={errors.email}
      />

      <Input
        label="Teléfono"
        placeholder="Teléfono"
        value={value.phone}
        onChange={(e) => updateField('phone', e.target.value)}
        disabled={disabled}
        error={errors.phone}
      />

      <div>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
          <DropDown
            label="Departamento"
            options={states.map((s) => ({ value: String(s.id), label: s.name }))}
            value={value.stateId ? String(value.stateId) : ''}
            onChangeValue={(val) => {
              const id = val ? Number(val) : undefined;
              onChange({ ...value, stateId: id, cityId: undefined });
            }}
            searchKey="label"
          />

          <DropDown
            label="Ciudad"
            options={cities.map((c) => ({ value: String(c.cityId), label: c.name }))}
            value={value.cityId ? String(value.cityId) : ''}
            onChangeValue={(val) => {
              const id = val ? Number(val) : undefined;
              updateField('cityId', id);
            }}
            searchKey="label"
          />
        </div>

        <label className="font-semibold text-primary-900 mb-2 text-base block">
          Mapa (click en el mapa para colocar marcador)
        </label>
        <MapPicker
          position={
            value?.latitude != null && value?.longitude != null
              ? {
                  lat: value.latitude,
                  lng: value.longitude,
                }
              : null
          }
          onChange={(p) => {
            if (!p) return;

            onChange({
              ...value,
              latitude: p.lat,
              longitude: p.lng,
            });
          }}
          height={300}
        />

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3">
          <Input
            label="Latitud"
            placeholder="Latitud"
            value={value.latitude ? String(value.latitude) : ''}
            onChange={(e) => updateField('latitude', parseFloat(e.target.value || '0'))}
            disabled={disabled}
          />
          <Input
            label="Longitud"
            placeholder="Longitud"
            value={value.longitude ? String(value.longitude) : ''}
            onChange={(e) => updateField('longitude', parseFloat(e.target.value || '0'))}
            disabled={disabled}
          />
        </div>
      </div>
    </Form>
  );
}
