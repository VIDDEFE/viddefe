import { useState, useEffect } from 'react';
import type { Church, ChurchSummary, ChurchDetail } from '../../models';
import { Button, PageHeader, Table, Modal, Form, Input, DropDown, PastorSelector } from '../../components/shared';
import MapPicker from '../../components/shared/MapPicker';
import { useChurchChildren, useStates, useCities, useCreateChildrenChurch, useUpdateChurch, useDeleteChurch, useChurch } from '../../hooks';
import { useAppContext } from '../../context/AppContext';
import type { Cities, States } from '../../services/stateCitiesService';

type ModalMode = 'create' | 'edit' | 'view' | 'delete' | null;

export default function Churches() {
  const { user } = useAppContext();
  const churchId = user?.church?.id;
  
  const { data: churches, isLoading } = useChurchChildren(churchId);
  
  const [modalMode, setModalMode] = useState<ModalMode>(null);
  const [selectedChurch, setSelectedChurch] = useState<ChurchSummary | null>(null);
  const [formData, setFormData] = useState<Partial<Church>>({});

  // Hooks para CRUD
  const createChurch = useCreateChildrenChurch(churchId);
  const updateChurch = useUpdateChurch();
  const deleteChurch = useDeleteChurch();
  
  // Hook para obtener detalles de la iglesia seleccionada
  const { data: churchDetails, isLoading: isLoadingDetails } = useChurch(selectedChurch?.id);

  const { data: states } = useStates();
  const [selectedStateId, setSelectedStateId] = useState<number | undefined>(undefined);
  const { data: cities } = useCities(selectedStateId);
  const [selectedCityId, setSelectedCityId] = useState<number | undefined>(undefined);
  const [selectedPastorId, setSelectedPastorId] = useState<string>('');

  // Cargar datos del churchDetails cuando se obtienen (para edición)
  useEffect(() => {
    if (churchDetails && (modalMode === 'edit' || modalMode === 'view')) {
      setFormData({
        name: churchDetails.name,
        email: churchDetails.email,
        phone: churchDetails.phone,
        foundedYear: churchDetails.foundedYear,
        foundationDate: churchDetails.foundedDate,
        memberCount: churchDetails.memberCount,
        latitude: churchDetails.latitude,
        longitude: churchDetails.longitude,
      });
      setSelectedStateId(churchDetails.states?.id);
      setSelectedCityId(churchDetails.city?.cityId);
      setSelectedPastorId(churchDetails.pastor?.id || '');
    }
  }, [churchDetails, modalMode]);

  const resetForm = () => {
    setFormData({});
    setSelectedStateId(undefined);
    setSelectedCityId(undefined);
    setSelectedPastorId('');
    setSelectedChurch(null);
  };

  const openCreateModal = () => {
    resetForm();
    setModalMode('create');
  };

  const openEditModal = (church: ChurchSummary) => {
    setSelectedChurch(church);
    // Los datos detallados se cargarán vía useEffect cuando churchDetails esté listo
    setModalMode('edit');
  };

  const openViewModal = (church: ChurchSummary) => {
    setSelectedChurch(church);
    setModalMode('view');
  };

  const openDeleteModal = (church: ChurchSummary) => {
    setSelectedChurch(church);
    setModalMode('delete');
  };

  const closeModal = () => {
    setModalMode(null);
    resetForm();
  };

  // Helper para obtener nombre completo del pastor
  const getPastorName = (details: ChurchDetail | undefined, summary: ChurchSummary | null) => {
    if (details?.pastor) {
      return `${details.pastor.firstName} ${details.pastor.lastName}`;
    }
    return summary?.pastor || '-';
  };

  const handleCreate = () => {
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
        foundationDate: formData.foundationDate || undefined,
        memberCount: formData.memberCount || 0,
        longitude: formData.longitude || 0,
        latitude: formData.latitude || 0,
      },
      {
        onSuccess() {
          closeModal();
        },
      }
    );
  };

  const handleUpdate = () => {
    if (!selectedChurch?.id || !formData.name) return;
    updateChurch.mutate(
      {
        id: selectedChurch.id,
        data: {
          name: formData.name,
          cityId: selectedCityId,
          phone: formData.phone,
          email: formData.email,
          pastor: formData.pastor,
          pastorId: selectedPastorId || undefined,
          foundedYear: formData.foundedYear,
          foundationDate: formData.foundationDate,
          longitude: formData.longitude,
          latitude: formData.latitude,
        },
      },
      {
        onSuccess() {
          closeModal();
        },
      }
    );
  };

  const handleDelete = () => {
    if (!selectedChurch?.id) return;
    deleteChurch.mutate(selectedChurch.id, {
      onSuccess() {
        closeModal();
      },
    });
  };

  const columns = [
    { key: 'name' as const, label: 'Nombre' },
    { key: 'pastor' as const, label: 'Pastor' },
    { key: 'state' as const, label: 'Departamento', render: (_value: string | number | States | Cities, item: ChurchSummary) => item.state?.name || '-' },
    { key: 'city' as const, label: 'Ciudad', render: (_value: string | number | States | Cities, item: ChurchSummary) => item.city?.name || '-' },
  ];

  const tableActions = [
    {
      icon: 'view' as const,
      label: 'Ver detalles',
      onClick: openViewModal,
      variant: 'secondary' as const,
    },
    {
      icon: 'edit' as const,
      label: 'Editar',
      onClick: openEditModal,
      variant: 'primary' as const,
    },
    {
      icon: 'delete' as const,
      label: 'Eliminar',
      onClick: openDeleteModal,
      variant: 'danger' as const,
    },
  ];

  const isFormModalOpen = modalMode === 'create' || modalMode === 'edit';
  const isMutating = createChurch.isPending || updateChurch.isPending || deleteChurch.isPending;

  return (
    <div className="container mx-auto px-2">
      <PageHeader
        title="Iglesias Hijas"
        subtitle="Gestiona todas las iglesias hijas de tu organización desde este panel."
        actions={<Button variant="primary" onClick={openCreateModal}>+ Nueva Iglesia</Button>}
      />

      <Table<ChurchSummary>
        data={Array.isArray(churches) ? churches : (churches?.content ?? [])}
        columns={columns}
        actions={tableActions}
        loading={isLoading}
      />

      {/* Modal de Crear/Editar */}
      <Modal
        isOpen={isFormModalOpen}
        title={modalMode === 'create' ? 'Agregar Nueva Iglesia' : 'Editar Iglesia'}
        onClose={closeModal}
        actions={
          <div className="flex gap-2">
            <Button 
              variant="primary" 
              onClick={modalMode === 'create' ? handleCreate : handleUpdate} 
              disabled={isMutating || (modalMode === 'edit' && isLoadingDetails)}
            >
              {isMutating ? 'Guardando...' : 'Guardar'}
            </Button>
            <Button variant="secondary" onClick={closeModal}>
              Cancelar
            </Button>
          </div>
        }
      >
        {modalMode === 'edit' && isLoadingDetails ? (
          <div className="flex justify-center items-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            <span className="ml-2 text-neutral-600">Cargando datos...</span>
          </div>
        ) : (
          <Form>
          <Input
            label="Nombre"
            placeholder="Nombre de la iglesia"
            value={formData.name || ''}
            onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
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
            value={formData.foundationDate || ''}
            onChange={(e) => setFormData(prev => ({ ...prev, foundationDate: e.target.value }))}          
          />
          <Input
            label="Email"
            type="email"
            placeholder="correo@iglesia.com"
            value={formData.email || ''}
            onChange={(e) => setFormData(prev => ({ ...prev, email: e.target.value }))}
          />
          <Input
            label="Teléfono"
            placeholder="Teléfono"
            value={formData.phone || ''}
            onChange={(e) => setFormData(prev => ({ ...prev, phone: e.target.value }))}
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
        )}
      </Modal>

      {/* Modal de Ver Detalles */}
      <Modal
        isOpen={modalMode === 'view'}
        title="Detalles de la Iglesia"
        onClose={closeModal}
        actions={
          <div className="flex gap-2">
            <Button variant="primary" onClick={() => selectedChurch && openEditModal(selectedChurch)}>
              Editar
            </Button>
            <Button variant="secondary" onClick={closeModal}>
              Cerrar
            </Button>
          </div>
        }
      >
        {isLoadingDetails ? (
          <div className="flex justify-center items-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            <span className="ml-2 text-neutral-600">Cargando datos...</span>
          </div>
        ) : (
          selectedChurch && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-neutral-500">Nombre</label>
                  <p className="text-lg font-semibold text-primary-900">{churchDetails?.name || selectedChurch.name}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-neutral-500">Pastor</label>
                  <p className="text-lg text-neutral-800">{getPastorName(churchDetails, selectedChurch)}</p>
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium text-neutral-500">Departamento</label>
                  <p className="text-neutral-800">{churchDetails?.states?.name || selectedChurch.state?.name || '-'}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-neutral-500">Ciudad</label>
                  <p className="text-neutral-800">{churchDetails?.city?.name || selectedChurch.city?.name || '-'}</p>
                </div>
              </div>

              {churchDetails && (
                <>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="text-sm font-medium text-neutral-500">Email</label>
                      <p className="text-neutral-800">{churchDetails.email || '-'}</p>
                    </div>
                    <div>
                      <label className="text-sm font-medium text-neutral-500">Teléfono</label>
                      <p className="text-neutral-800">{churchDetails.phone || '-'}</p>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="text-sm font-medium text-neutral-500">Fecha de Fundación</label>
                      <p className="text-neutral-800">{churchDetails.foundedDate || churchDetails.foundedYear || '-'}</p>
                    </div>
                    <div>
                      <label className="text-sm font-medium text-neutral-500">Miembros</label>
                      <p className="text-neutral-800">{churchDetails.memberCount || 0}</p>
                    </div>
                  </div>
                </>
              )}

              {((churchDetails?.latitude && churchDetails?.longitude) || (selectedChurch.latitude && selectedChurch.longitude)) && (
                <div>
                  <label className="text-sm font-medium text-neutral-500 mb-2 block">Ubicación</label>
                  <MapPicker
                    position={{ 
                      lat: churchDetails?.latitude || selectedChurch.latitude, 
                      lng: churchDetails?.longitude || selectedChurch.longitude 
                    }}
                    onChange={() => {}}
                    height={200}
                  />
                </div>
              )}
            </div>
          )
        )}
      </Modal>

      {/* Modal de Eliminar */}
      <Modal
        isOpen={modalMode === 'delete'}
        title="Eliminar Iglesia"
        onClose={closeModal}
        actions={
          <div className="flex gap-2">
            <Button 
              variant="danger" 
              onClick={handleDelete} 
              disabled={deleteChurch.isPending}
            >
              {deleteChurch.isPending ? 'Eliminando...' : 'Eliminar'}
            </Button>
            <Button variant="secondary" onClick={closeModal}>
              Cancelar
            </Button>
          </div>
        }
      >
        <div className="text-center py-4">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <h3 className="text-lg font-semibold text-neutral-900 mb-2">
            ¿Estás seguro de eliminar esta iglesia?
          </h3>
          <p className="text-neutral-600">
            Se eliminará la iglesia <strong>"{selectedChurch?.name}"</strong>. Esta acción no se puede deshacer.
          </p>
        </div>
      </Modal>
    </div>
  );
}
