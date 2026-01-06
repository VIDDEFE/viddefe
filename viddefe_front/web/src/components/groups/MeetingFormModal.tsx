import { memo, useEffect, useState } from 'react';
import { toast } from 'sonner';
import { Modal, Button, DropDown } from '../shared';
import type { Meeting, MeetingType, CreateMeetingDto, UpdateMeetingDto } from '../../models';

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
  date: string;
  time: string;
  groupMeetingTypeId: string;
}

const initialFormData: FormData = {
  name: '',
  description: '',
  date: '',
  time: '',
  groupMeetingTypeId: '',
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
        const date = new Date(meeting.date);
        setFormData({
          name: meeting.name,
          description: meeting.description || '',
          date: date.toISOString().split('T')[0],
          time: date.toTimeString().slice(0, 5),
          groupMeetingTypeId: meeting.type?.id.toString() || '',
        });
      } else {
        setFormData({
          ...initialFormData,
          groupMeetingTypeId: meetingTypes[0]?.id.toString() || '',
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
    if (!formData.date) {
      newErrors.date = 'La fecha es requerida';
    }
    if (!formData.time) {
      newErrors.time = 'La hora es requerida';
    }
    if (!formData.groupMeetingTypeId) {
      newErrors.groupMeetingTypeId = 'El tipo es requerido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    // Combinar fecha y hora
    const dateTime = new Date(`${formData.date}T${formData.time}`);
    const now = new Date();
    if (Number.isNaN(dateTime.getTime())) {
      setErrors((prev) => ({ ...prev, date: 'Fecha u hora inválidas' }));
      toast.error('Fecha u hora inválidas');
      return;
    }
    if (dateTime.getTime() <= now.getTime()) {
      setErrors((prev) => ({ ...prev, date: 'La fecha debe ser futura' }));
      toast.error('Meeting date cannot be in the past');
      return;
    }
    
    const data: CreateMeetingDto | UpdateMeetingDto = {
      name: formData.name.trim(),
      description: formData.description.trim() || undefined,
      date: dateTime.toISOString(),
      groupMeetingTypeId: Number.parseInt(formData.groupMeetingTypeId, 10),
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
            value={formData.groupMeetingTypeId}
            onChangeValue={(value) => handleDropdownChange('groupMeetingTypeId', value)}
            error={errors.groupMeetingTypeId}
            labelKey="label"
            valueKey="value"
          />
        </div>

        {/* Fecha y Hora */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="date" className="block text-sm font-medium text-neutral-700 mb-1">
              Fecha *
            </label>
            <input
              type="date"
              id="date"
              name="date"
              value={formData.date}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.date ? 'border-red-500' : 'border-neutral-300'
              }`}
            />
            {errors.date && <p className="mt-1 text-sm text-red-500">{errors.date}</p>}
          </div>
          <div>
            <label htmlFor="time" className="block text-sm font-medium text-neutral-700 mb-1">
              Hora *
            </label>
            <input
              type="time"
              id="time"
              name="time"
              value={formData.time}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.time ? 'border-red-500' : 'border-neutral-300'
              }`}
            />
            {errors.time && <p className="mt-1 text-sm text-red-500">{errors.time}</p>}
          </div>
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
