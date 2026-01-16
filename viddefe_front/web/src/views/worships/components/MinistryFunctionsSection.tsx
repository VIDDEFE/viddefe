import { memo } from 'react';
import { Card, Button } from '../../../components/shared';
import { FiBriefcase, FiEdit2, FiUser } from 'react-icons/fi';
import type { MinistryFunction } from '../../../models';

// ============================================================================
// TYPES
// ============================================================================

export interface MinistryFunctionsSectionProps {
  ministryFunctions: MinistryFunction[];
  isLoading: boolean;
  onManage: () => void;
}

// ============================================================================
// FUNCTION CARD
// ============================================================================

interface FunctionCardProps {
  ministryFunction: MinistryFunction;
}

const FunctionCard = memo(function FunctionCard({
  ministryFunction,
}: Readonly<FunctionCardProps>) {
  const { people, role } = ministryFunction;

  const fullName = `${people.firstName} ${people.lastName}`.trim();
  const initials = `${people.firstName?.[0] ?? ''}${people.lastName?.[0] ?? ''}`.toUpperCase();

  return (
    <div className="flex flex-col items-center gap-1 py-3 px-0.5 bg-white rounded-lg border border-neutral-200 hover:border-primary-300 hover:shadow-sm transition-all w-auto">
      {people.avatar ? (
        <img
          src={people.avatar}
          alt={fullName}
          className="w-10 h-10 rounded-full object-cover shrink-0"
        />
      ) : (
        <span className="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium shrink-0">
          {initials || <FiUser size={16} />}
        </span>
      )}

      <div className="flex w-auto min-w-10">
        <div className="flex flex-col">
        <p className="font-medium text-neutral-800">
          {fullName || 'Sin nombre'}
        </p>
        <span className="text-sm text-neutral-500">
          {people.phone || 'Sin teléfono'}
        </span>
      </div>

      </div>
        <span className="inline-flex items-center gap-1 px-1 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-700 mt-1">
          <FiBriefcase size={16} />
          {role.name}
        </span>
      </div>
  );
});

// ============================================================================
// MAIN COMPONENT
// ============================================================================

function MinistryFunctionsSection({
  ministryFunctions,
  isLoading,
  onManage,
}: Readonly<MinistryFunctionsSectionProps>) {
  const totalAssignments = ministryFunctions.length;

  // ✅ Sonar fix: extract nested ternary
  let assignmentText = 'Sin asignaciones';
  if (totalAssignments === 1) {
    assignmentText = '1 persona asignada';
  } else if (totalAssignments > 1) {
    assignmentText = `${totalAssignments} personas asignadas`;
  }

  return (
    <Card>
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 mb-6">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-primary-100 rounded-lg">
            <FiBriefcase className="text-primary-600" size={20} />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-neutral-800">
              Funciones Ministeriales
            </h2>
            <p className="text-sm text-neutral-500">{assignmentText}</p>
          </div>
        </div>

        <Button variant="primary" onClick={onManage}>
          <span className="flex items-center gap-2">
            <FiEdit2 size={16} />
            Gestionar
          </span>
        </Button>
      </div>

      {/* Content */}
      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
        </div>
      ) : totalAssignments === 0 ? (
        <div className="text-center py-12 bg-neutral-50 rounded-lg border-2 border-dashed border-neutral-200">
          <FiBriefcase className="mx-auto h-12 w-12 text-neutral-300 mb-3" />
          <p className="text-neutral-600 font-medium">No hay funciones asignadas</p>
          <p className="text-neutral-400 text-sm mt-1">
            Presiona &quot;Gestionar&quot; para asignar personas a funciones ministeriales
          </p>
        </div>
      ) : (
        <div className="flex gap-3 overflow-x-auto pb-2 snap-x snap-mandatory">
          {ministryFunctions.map((mf) => (
            <div key={mf.id} className="snap-start shrink-0 w-72">
              <FunctionCard ministryFunction={mf} />
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}

export default memo(MinistryFunctionsSection);
