import { memo, useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Modal, Button, DropDown } from '../shared';
import type { Meeting, MeetingType, CreateMeetingDto, UpdateMeetingDto } from '../../models';
import { toDatetimeLocal, toISOStringWithOffset } from '../../utils/helpers';

interface MeetingFormModalProps {
  readonly isOpen: boolean;
  readonly meeting?: Meeting | null;
  readonly meetingTypes: MeetingType[];
  readonly onClose: () => void;
  readonly onSave: (data: CreateMeetingDto | UpdateMeetingDto) => void;
  readonly isSaving: boolean;
}

interface FormData {
  name: string;
  description: string;
  datetime: string; // datetime-local format: "YYYY-MM-DDTHH:mm"
  meetingTypeId: string;
}

const initialFormData: FormData = {
  name: '',
  description: '',
  datetime: '',
  meetingTypeId: '',
};

function MeetingFormModal({
  isOpen,
  meeting,
  meetingTypes,
  onClose,
  onSave,
  isSaving,
}: MeetingFormModalProps) {
  const [formData, setFormData] = useState<FormData>(initialFormData);
  const [errors, setErrors] = useState<Partial<Record<keyof FormData, string>>>({});

  const isEditing = !!meeting;

  // Inicializar formulario cuando se abre el modal
  useEffect(() => {
    if (isOpen) {
      if (meeting) {
        // Convertir UTC del backend a datetime-local (hora local)
        setFormData({
          name: meeting.name,
          description: meeting.description || '',
          datetime: toDatetimeLocal(meeting.scheduledDate),
          meetingTypeId: meeting.type?.id.toString() || '',
        });
      } else {
        setFormData({
          ...initialFormData,
          meetingTypeId: meetingTypes[0]?.id.toString() || '',
        });
      }
      setErrors({});
    }
  }, [isOpen, meeting, meetingTypes]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Limpiar error al cambiar
    if (errors[name as keyof FormData]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }));
    }
  };

  const handleDropdownChange = (name: string, value: string) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Limpiar error al cambiar
    if (errors[name as keyof FormData]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }));
    }
  };

  const validate = (): boolean => {
    const newErrors: Partial<Record<keyof FormData, string>> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'El nombre es requerido';
    }
    if (!formData.datetime) {
      newErrors.datetime = 'La fecha y hora son requeridas';
    }
    if (!formData.meetingTypeId) {
      newErrors.meetingTypeId = 'El tipo es requerido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    // Validar la fecha
    const localDate = new Date(formData.datetime);
    const now = new Date();
    
    if (Number.isNaN(localDate.getTime())) {
      setErrors((prev) => ({ ...prev, datetime: 'Fecha u hora inválidas' }));
      toast.error('Fecha u hora inválidas');
      return;
    }
    
    if (!isEditing && localDate.getTime() <= now.getTime()) {
      setErrors((prev) => ({ ...prev, datetime: 'La fecha debe ser futura' }));
      toast.error('La fecha de la reunión no puede ser en el pasado');
      return;
    }
    
    // Convertir a ISO-8601 con offset de timezone para el backend
    // El backend REQUIERE el offset (ej: "2026-01-15T10:00:00-05:00")
    const data: CreateMeetingDto | UpdateMeetingDto = {
      name: formData.name.trim(),
      description: formData.description.trim() || undefined,
      scheduledDate: toISOStringWithOffset(formData.datetime), // Con offset de timezone
      meetingTypeId: Number.parseInt(formData.meetingTypeId, 10),
      meetingType: 'GROUP_MEETING',
    };

    onSave(data);
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isEditing ? 'Editar Reunión' : 'Nueva Reunión'}
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Nombre */}
        <div>
          <label htmlFor="name" className="block text-sm font-medium text-neutral-700 mb-1">
            Nombre *
          </label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
              errors.name ? 'border-red-500' : 'border-neutral-300'
            }`}
            placeholder="Nombre de la reunión"
          />
          {errors.name && <p className="mt-1 text-sm text-red-500">{errors.name}</p>}
        </div>

        {/* Tipo de reunión */}
        <div>
          <DropDown
            label="Tipo de Reunión *"
            placeholder="Selecciona un tipo"
            options={meetingTypes.map((type) => ({
              value: type.id.toString(),
              label: type.name,
              id: type.id,
              name: type.name,
            }))}
            value={formData.meetingTypeId}
            onChangeValue={(value) => handleDropdownChange('meetingTypeId', value)}
            error={errors.meetingTypeId}
            labelKey="label"
            valueKey="value"
          />
        </div>

        {/* Fecha y Hora */}
        <div>
          <label htmlFor="datetime" className="block text-sm font-medium text-neutral-700 mb-1">
            Fecha y Hora *
          </label>
          <input
            type="datetime-local"
            id="datetime"
            name="datetime"
            value={formData.datetime}
            onChange={handleChange}
            className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
              errors.datetime ? 'border-red-500' : 'border-neutral-300'
            }`}
          />
          {errors.datetime && <p className="mt-1 text-sm text-red-500">{errors.datetime}</p>}
        </div>

        {/* Descripción */}
        <div>
          <label htmlFor="description" className="block text-sm font-medium text-neutral-700 mb-1">
            Descripción
          </label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows={3}
            className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="Descripción opcional de la reunión"
          />
        </div>

        {/* Botones */}
        <div className="flex justify-end gap-3 pt-4">
          <Button type="button" variant="secondary" onClick={onClose} disabled={isSaving}>
            Cancelar
          </Button>
          <Button type="submit" variant="primary" disabled={isSaving}>
            {isSaving ? 'Guardando...' : isEditing ? 'Guardar Cambios' : 'Crear Reunión'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

export default memo(MeetingFormModal);
