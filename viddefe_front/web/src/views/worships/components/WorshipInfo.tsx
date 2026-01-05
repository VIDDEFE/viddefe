import { memo } from 'react';
import { Card } from '../../../components/shared';
import { FiFileText } from 'react-icons/fi';
import type { WorshipInfoProps } from './types';

function WorshipInfo({ worship }: Readonly<WorshipInfoProps>) {
  return (
    <Card>
      <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
        <FiFileText className="text-primary-600" />
        Información General
      </h3>

      <div className="space-y-4">
        {/* Nombre */}
        <div>
          <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
            Nombre
          </span>
          <p className="text-neutral-800 font-medium mt-1">{worship.name}</p>
        </div>

        {/* Descripción */}
        {worship.description && (
          <div>
            <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
              Descripción
            </span>
            <p className="text-neutral-700 mt-1 whitespace-pre-wrap">{worship.description}</p>
          </div>
        )}

        {/* Tipo de culto */}
        <div>
          <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
            Tipo de Culto
          </span>
          <p className="mt-1">
            <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-primary-100 text-primary-800">
              {worship.worshipType?.name}
            </span>
          </p>
        </div>
      </div>
    </Card>
  );
}

export default memo(WorshipInfo);
