import { memo } from 'react';
import { Card } from '../../../components/shared';
import { FiCalendar, FiClock } from 'react-icons/fi';
import { formatDate, formatTime } from './helpers';
import type { WorshipInfoProps } from './types';

function WorshipSchedule({ worship }: Readonly<WorshipInfoProps>) {
  return (
    <Card>
      <h3 className="text-lg font-semibold text-neutral-800 mb-4 flex items-center gap-2">
        <FiCalendar className="text-primary-600" />
        Fecha y Hora
      </h3>

      <div className="space-y-4">
        <div>
          <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
            Fecha Programada
          </span>
          <p className="text-neutral-800 mt-1 capitalize">
            {formatDate(worship.scheduledDate)}
          </p>
          <div className="flex items-center gap-2 mt-1 text-neutral-600">
            <FiClock size={14} />
            <span className="text-sm">{formatTime(worship.scheduledDate)}</span>
          </div>
        </div>
      </div>
    </Card>
  );
}

export default memo(WorshipSchedule);
