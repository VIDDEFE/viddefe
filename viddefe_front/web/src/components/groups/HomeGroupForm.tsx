import { Form, Input, TextArea } from '../shared';
import DropDown from '../shared/DropDown';
import MapPicker, { type Position } from '../shared/MapPicker';
import type { Strategy, Person } from '../../models';

export interface HomeGroupFormData {
  name: string;
  description: string;
  latitude: number | '';
  longitude: number | '';
  leaderId: string;
  strategyId: string;
}

export const initialHomeGroupFormData: HomeGroupFormData = {
  name: '',
  description: '',
  latitude: '',
  longitude: '',
  leaderId: '',
  strategyId: '',
};

interface HomeGroupFormProps {
  value: HomeGroupFormData;
  onChange: (patch: Partial<HomeGroupFormData>) => void;
  strategies?: Strategy[];
  people?: Person[];
  errors?: Partial<Record<keyof HomeGroupFormData, string>>;
  isLoadingStrategies?: boolean;
  isLoadingPeople?: boolean;
}

export default function HomeGroupForm({
  value,
  onChange,
  strategies = [],
  people = [],
  errors = {},
  isLoadingStrategies = false,
  isLoadingPeople = false,
}: HomeGroupFormProps) {
  // Transformar strategies para el DropDown
  const strategyOptions = strategies.map((s) => ({
    id: s.id,
    name: s.name,
  }));

  // Transformar people para el DropDown
  const peopleOptions = people.map((p) => ({
    id: p.id,
    name: `${p.firstName} ${p.lastName}`,
    fullName: `${p.firstName} ${p.lastName}`,
  }));

  // Posición para el MapPicker
  const mapPosition: Position = 
    value.latitude !== '' && value.longitude !== ''
      ? { lat: value.latitude as number, lng: value.longitude as number }
      : null;

  // Handler para cambios en el mapa
  const handleMapChange = (pos: Position) => {
    if (pos) {
      onChange({
        latitude: pos.lat,
        longitude: pos.lng,
      });
    }
  };

  return (
    <Form>
      <Input
        label="Nombre del Grupo"
        placeholder="Ej: Grupo de Hogar Norte"
        value={value.name}
        onChange={(e) => onChange({ name: e.target.value })}
        minLength={3}
        maxLength={100}
        required
        error={errors.name}
      />

      <TextArea
        label="Descripción"
        placeholder="Descripción del grupo (opcional)"
        value={value.description}
        onChange={(e) => onChange({ description: e.target.value })}
        error={errors.description}
      />

      <DropDown
        label="Estrategia"
        options={strategyOptions}
        value={value.strategyId}
        onChangeValue={(val) => onChange({ strategyId: val })}
        placeholder={isLoadingStrategies ? 'Cargando estrategias...' : 'Seleccionar estrategia...'}
        labelKey="name"
        valueKey="id"
        error={errors.strategyId}
        disabled={isLoadingStrategies}
      />

      <DropDown
        label="Líder"
        options={peopleOptions}
        value={value.leaderId}
        onChangeValue={(val) => onChange({ leaderId: val })}
        placeholder={isLoadingPeople ? 'Cargando personas...' : 'Seleccionar líder...'}
        labelKey="name"
        valueKey="id"
        searchKey="fullName"
        error={errors.leaderId}
        disabled={isLoadingPeople}
      />

      {/* MapPicker para ubicación */}
      <div className="space-y-2">
        <label className="font-semibold text-primary-900 text-base">
          Ubicación del Grupo
        </label>
        <p className="text-sm text-neutral-500 mb-2">
          Haz clic en el mapa o arrastra el marcador para seleccionar la ubicación
        </p>
        <MapPicker
          position={mapPosition}
          onChange={handleMapChange}
          height={250}
          mode="operate"
        />
        {(errors.latitude || errors.longitude) && (
          <span className="text-red-600 text-sm">
            {errors.latitude || errors.longitude}
          </span>
        )}
      </div>

      {/* Coordenadas en inputs (opcionales, para edición manual) */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Latitud"
          type="number"
          placeholder="Ej: 4.6097"
          value={value.latitude === '' ? '' : value.latitude}
          onChange={(e) =>
            onChange({
              latitude: e.target.value === '' ? '' : parseFloat(e.target.value),
            })
          }
          min={-90}
          max={90}
          step="any"
          required
          error={errors.latitude}
        />

        <Input
          label="Longitud"
          type="number"
          placeholder="Ej: -74.0817"
          value={value.longitude === '' ? '' : value.longitude}
          onChange={(e) =>
            onChange({
              longitude: e.target.value === '' ? '' : parseFloat(e.target.value),
            })
          }
          min={-180}
          max={180}
          step="any"
          required
          error={errors.longitude}
        />
      </div>
    </Form>
  );
}
