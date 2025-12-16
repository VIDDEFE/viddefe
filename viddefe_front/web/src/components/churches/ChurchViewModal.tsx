import { Modal, Button } from '../shared';
import MapPicker from '../shared/MapPicker';
import type { ChurchSummary, ChurchDetail } from '../../models';
import { formatDate } from '../../utils';
import { useEffect, useState } from 'react';

interface ChurchViewModalProps {
  isOpen: boolean;
  church: ChurchSummary;
  churchDetails: ChurchDetail;
  isLoading: boolean;
  onEdit: () => void;
  onClose: () => void;
}

export default function ChurchViewModal({
  isOpen,
  church,
  churchDetails,
  isLoading,
  onEdit,
  onClose,
}: ChurchViewModalProps) {
  const getPastorName = (): string => {
    if (churchDetails?.pastor) {
      return `${churchDetails.pastor.firstName} ${churchDetails.pastor.lastName}`;
    }
    if (typeof church?.pastor === 'object' && church?.pastor !== null) {
      return `${church.pastor.firstName} ${church.pastor.lastName}`;
    }
    return '-';
  };

  const [isLoadingMap, setIsLoadingMap] =  useState<boolean>(false);
  const [mapPosition, setMapPosition] = useState<{lat:number,lng:number} | null>(null);
  const constructPosition = () => {
      const position = church?.latitude !== undefined && church?.longitude !== undefined
        ?  { lat: church?.latitude, lng: church?.longitude } // 👈 swap
        :  null;
      return position;
  }
  useEffect(()=>{
    setIsLoadingMap(true);
    const pos = constructPosition();
    setIsLoadingMap(false);
    setMapPosition(pos);
  },[church?.latitude, church?.longitude])

  return (
    <Modal
      isOpen={isOpen}
      title="Detalles de la Iglesia"
      onClose={onClose}
      actions={
        <div className="flex gap-2">
          <Button variant="primary" onClick={onEdit}>
            Editar
          </Button>
          <Button variant="secondary" onClick={onClose}>
            Cerrar
          </Button>
        </div>
      }
    >
      {isLoading ? (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          <span className="ml-2 text-neutral-600">Cargando datos...</span>
        </div>
      ) : (
        church && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium text-neutral-500">Nombre</label>
                <p className="text-lg font-semibold text-primary-900">
                  {church?.name || church.name}
                </p>
              </div>
              <div>
                <label className="text-sm font-medium text-neutral-500">Pastor</label>
                <p className="text-lg text-neutral-800">{getPastorName()}</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium text-neutral-500">Departamento</label>
                <p className="text-neutral-800">
                  {church?.states?.name || church.states?.name || '-'}
                </p>
              </div>
              <div>
                <label className="text-sm font-medium text-neutral-500">Ciudad</label>
                <p className="text-neutral-800">
                  {church?.city?.name || church.city?.name || '-'}
                </p>
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
                    <p className="text-neutral-800">
                      {formatDate(churchDetails?.foundationDate) || churchDetails.foundedYear || '-'}
                    </p>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-neutral-500">Miembros</label>
                    <p className="text-neutral-800">{churchDetails.memberCount || 0}</p>
                  </div>
                </div>
              </>
            )}

            {!isLoadingMap && (
              <div>
                <label className="text-sm font-medium text-neutral-500 mb-2 block">
                  Ubicación
                </label>

                <MapPicker
                  position={mapPosition}
                  height={300}
                  onChange={() => {}}
                />
              </div>
            )}

          </div>
        )
      )}
    </Modal>
  );
}
