import { useEffect, useState } from 'react';
import type { Cities, States } from '../../services/stateCitiesService';
import { Form, Input, DropDown, PastorSelector } from '../shared';
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
  onChange: (patch: Partial<ChurchFormData>) => void;
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
  const updateField = <K extends keyof ChurchFormData>(
    field: K,
    val: ChurchFormData[K]
  ) => {
    onChange({ [field]: val });
  };
  const [isLoadingMap, setIsLoadingMap] =  useState<boolean>(false);
  const [mapPosition, setMapPosition] = useState<{lat:number,lng:number} | null>(null);
  const constructPosition = () => {
      const position = value.latitude !== undefined && value.longitude !== undefined
        ?  { lat: value.latitude, lng: value.longitude } // 👈 swap
        :  null;
      return position;
  }
  useEffect(()=>{
    setIsLoadingMap(true);
    const pos = constructPosition();
    setIsLoadingMap(false);
    setMapPosition(pos);
  },[value.latitude, value.longitude])
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
        value={
          value.foundationDate
            ? new Date(value.foundationDate).toISOString().split('T')[0]
            : ''
        }
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

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
        <DropDown
          label="Departamento"
          options={states.map((s) => ({
            value: String(s.id),
            label: s.name,
          }))}
          value={value.stateId != null ? String(value.stateId) : ''}
          onChangeValue={(val) => {
            const id = val ? Number(val) : undefined;
            updateField('stateId', id);
          }}
          searchKey="label"
        />

        <DropDown
          label="Ciudad"
          options={cities.map((c) => ({
            value: String(c.cityId),
            label: c.name,
          }))}
          value={value.cityId != null ? String(value.cityId) : ''}
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

      {!isLoadingMap &&
      <MapPicker
          mode='operate'
          position={mapPosition}
          height={300}
          onChange={(p) => {
            if (!p) return;
            updateField('latitude', p.lat);  // ← latitude = lng
            updateField('longitude', p.lng); // ← longitude = lat
          }}
        />
        }

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3">
        <Input
        label="Latitud"
        placeholder="Latitud"
        value={value.latitude !== undefined ? String(value.latitude) : ''}
        onChange={(e) => {
          const v = e.target.value;
          updateField('latitude', v === '' ? undefined : Number(v));
        }}
        disabled={disabled}
      />

      <Input
        label="Longitud"
        placeholder="Longitud"
        value={value.latitude !== undefined ? String(value.latitude) : ''}
        onChange={(e) => {
          const v = e.target.value;
          updateField('longitude', v === '' ? undefined : Number(v));
        }}
        disabled={disabled}
      />
      </div>
    </Form>
  );
}
