import { useState } from 'react';
import type { Church, ChurchSummary } from '../../models';
import { Button, PageHeader, Table, Modal, Form, Input, DropDown, PastorSelector } from '../../components/shared';
import MapPicker from '../../components/shared/MapPicker';
import { useChurchChildren, useStates, useCities, useCreateChildrenChurch } from '../../hooks';
import { useAppContext } from '../../context/AppContext';
import type { Cities, States } from '../../services/stateCitiesService';

export default function Churches() {
  const { user } = useAppContext();
  const churchId = user?.church?.id;
  
  
  const { data: churches, isLoading } = useChurchChildren(churchId);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Partial<Church>>({});

  const createChurch = useCreateChildrenChurch(churchId);
  const { data: states } = useStates();
  const [selectedStateId, setSelectedStateId] = useState<number | undefined>(undefined);
  const { data: cities } = useCities(selectedStateId);
  const [selectedCityId, setSelectedCityId] = useState<number | undefined>(undefined);

  const [selectedPastorId, setSelectedPastorId] = useState<string>('');

  const handleAddChurch = () => {
    if (!formData.name) return;
    createChurch.mutate(
      {
        name: formData.name || '',
        cityId: selectedCityId || 0,
        phone: formData.phone || '',
        email: formData.email || '',
        pastor: formData.pastor || '',
        pastorId: selectedPastorId || undefined,
        foundedYear: formData.foundedYear || new Date().getFullYear(),
        foundedDate: formData.foundedDate || undefined,
        memberCount: formData.memberCount || 0,
        longitude: formData.longitude || 0,
        latitude: formData.latitude || 0,
      },
      {
        onSuccess() {
          setFormData({});
          setIsModalOpen(false);
        },
      }
    )
  };

  const columns = [
    { key: 'name' as const, label: 'Nombre' },
    { key: 'pastor' as const, label: 'Pastor' },
    { key: 'state' as const, label: 'Departamento', render: (_value: string | number | States | Cities, item: ChurchSummary) => item.state.name },
    { key: 'city' as const, label: 'Ciudad', render: (_value: string | number | States | Cities, item: ChurchSummary) => item.city.name },
  ];

  return (
    <div className="container mx-auto px-2">
      <PageHeader
        title="Iglesias Hjas"
        subtitle="Gestiona todas las iglesias hijas de tu organización desde este panel."
        actions={<Button variant="primary" onClick={() => setIsModalOpen(true)}>+ Nueva Iglesia</Button>}
      />

      <Table<ChurchSummary>
        data={Array.isArray(churches) ? churches : (churches?.content ?? [])}
        columns={columns}
      />

      <Modal
        isOpen={isModalOpen}
        title="Agregar Nueva Iglesia"
        onClose={() => setIsModalOpen(false)}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleAddChurch} disabled={isLoading}>
              Guardar
            </Button>
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>
              Cancelar
            </Button>
          </div>
        }
      >
        <Form>
          <Input
            label="Nombre"
            placeholder="Nombre de la iglesia"
            value={formData.name || ''}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          />
          
          <PastorSelector
            label="Pastor"
            value={selectedPastorId}
            onChangeValue={(val) => setSelectedPastorId(val)}
            placeholder="Seleccionar pastor..."
          />

          <Input
            label="Fecha de Fundación"
            type="date"
            value={formData.foundedDate || ''}
            onChange={(e) => setFormData({ ...formData, foundedDate: e.target.value })}          
          />
          <Input
            label="Email"
            type="email"
            placeholder="correo@iglesia.com"
            value={formData.email || ''}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <Input
            label="Teléfono"
            placeholder="Teléfono"
            value={formData.phone || ''}
            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
          />

          <div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-3">
              <DropDown
                label="Departamento"
                options={(states ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
                value={selectedStateId ? String(selectedStateId) : ''}
                onChangeValue={(val) => {
                  const id = val ? Number(val) : undefined;
                  setSelectedStateId(id);
                  setSelectedCityId(undefined);
                  setFormData(prev => ({ ...prev, city: undefined }));
                }}
                searchKey="label"
              />

              <DropDown
                label="Ciudad"
                options={(cities ?? []).map((c) => ({
                  value: String(c.cityId),
                  label: c.name,
                }))}
                value={selectedCityId ? String(selectedCityId) : ''}
                onChangeValue={(val) => {
                  const id = val ? Number(val) : undefined;
                  setSelectedCityId(id);
                  setFormData((prev) => ({ ...prev, city: id }));
                }}
                searchKey="label"
              />
            </div>

            <label className="font-semibold text-primary-900 mb-2 text-base block">Mapa (click en el mapa para colocar marcador)</label>
            <MapPicker
              position={formData.latitude && formData.longitude ? { lat: formData.latitude, lng: formData.longitude } : null}
              onChange={(p) => setFormData(prev => ({ ...prev, latitude: p?.lat ?? 0, longitude: p?.lng ?? 0 }))}
              height={300}
            />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3">
              <Input
                label="Latitud"
                placeholder="Latitud"
                value={formData.latitude ? String(formData.latitude) : ''}
                onChange={(e) => setFormData(prev => ({ ...prev, latitude: parseFloat(e.target.value || '0') }))}
              />
              <Input
                label="Longitud"               
                placeholder="Longitud"
                value={formData.longitude ? String(formData.longitude) : ''}
                onChange={(e) => setFormData(prev => ({ ...prev, longitude: parseFloat(e.target.value || '0') }))}
              />
            </div>
          </div>
        </Form>
      </Modal>
    </div>
  );
}
