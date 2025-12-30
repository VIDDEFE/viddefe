import { Form, Input, TextArea, FormGroup } from '../shared';
import DropDown from '../shared/DropDown';
import type { WorshipType } from '../../models';

export interface WorshipFormData {
  name: string;
  description: string;
  scheduledDate: string;
  worshipTypeId: number | '';
}

export const initialWorshipFormData: WorshipFormData = {
  name: '',
  description: '',
  scheduledDate: '',
  worshipTypeId: '',
};

interface WorshipFormProps {
  value: WorshipFormData;
  onChange: (patch: Partial<WorshipFormData>) => void;
  worshipTypes?: WorshipType[];
  errors?: Partial<Record<keyof WorshipFormData, string>>;
}

export default function WorshipForm({
  value,
  onChange,
  worshipTypes = [],
  errors = {},
}: WorshipFormProps) {
  // Transformar worshipTypes para el DropDown
  const worshipTypeOptions = worshipTypes.map((type) => ({
    id: type.id,
    name: type.name,
  }));

  return (
    <Form>
      <Input
        label="Nombre del Culto"
        placeholder="Ej: Culto Dominical Matutino"
        value={value.name}
        onChange={(e) => onChange({ name: e.target.value })}
        maxLength={120}
        required
        error={errors.name}
      />

      <TextArea
        label="Descripción"
        placeholder="Descripción del culto (opcional)"
        value={value.description}
        onChange={(e) => onChange({ description: e.target.value })}
        maxLength={500}
        error={errors.description}
      />

      <Input
        label="Fecha y Hora Programada"
        type="datetime-local"
        value={value.scheduledDate}
        onChange={(e) => onChange({ scheduledDate: e.target.value })}
        required
        error={errors.scheduledDate}
      />

      <DropDown
        label="Tipo de Culto"
        options={worshipTypeOptions}
        value={value.worshipTypeId === '' ? '' : String(value.worshipTypeId)}
        onChangeValue={(val) =>
          onChange({
            worshipTypeId: val ? Number(val) : '',
          })
        }
        placeholder="Seleccionar tipo de culto..."
        labelKey="name"
        valueKey="id"
        error={errors.worshipTypeId}
      />
    </Form>
  );
}
