import { useState, useEffect, useMemo, memo } from 'react';
import { Modal, Button } from '../shared';
import type { RoleStrategyNode, PersonInRole } from '../../models';
import type { Person } from '../../models';
import { usePeople } from '../../hooks';
import { FiSearch, FiUser, FiUserPlus, FiUserMinus, FiAlertCircle, FiCheck, FiX } from 'react-icons/fi';

// ============================================================================
// TYPES
// ============================================================================

interface RolePeopleAssignmentModalProps {
  isOpen: boolean;
  role: RoleStrategyNode | null;
  onAssign: (personIds: string[]) => void;
  onRemove: (personId: string) => void;
  onClose: () => void;
  isSaving?: boolean;
}

// Helper para verificar si un rol es "Leader" o similar
function isLeaderRole(roleName: string): boolean {
  const leaderKeywords = ['leader', 'líder', 'lider', 'pastor', 'coordinador'];
  return leaderKeywords.some(keyword => 
    roleName.toLowerCase().includes(keyword)
  );
}

// ============================================================================
// PERSON ITEM - Item de persona en la lista de asignados
// ============================================================================
interface AssignedPersonItemProps {
  person: PersonInRole;
  onRemove: () => void;
  isRemoving?: boolean;
}

const AssignedPersonItem = memo(function AssignedPersonItem({
  person,
  onRemove,
  isRemoving = false,
}: AssignedPersonItemProps) {
  const initials = `${person.firstName?.[0] || ''}${person.lastName?.[0] || ''}`.toUpperCase();
  const fullName = `${person.firstName} ${person.lastName}`.trim();

  return (
    <div className="flex items-center justify-between p-3 bg-white border border-neutral-200 rounded-lg hover:border-neutral-300 transition-colors">
      <div className="flex items-center gap-3">
        {person.avatar ? (
          <img
            src={person.avatar}
            alt={fullName}
            className="w-10 h-10 rounded-full object-cover"
          />
        ) : (
          <div className="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium">
            {initials || <FiUser size={16} />}
          </div>
        )}
        <div>
          <p className="font-medium text-neutral-800">{fullName}</p>
          {person.phone && (
            <p className="text-sm text-neutral-500">{person.phone}</p>
          )}
        </div>
      </div>
      <button
        onClick={onRemove}
        disabled={isRemoving}
        className="p-2 text-red-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
        title="Remover del rol"
      >
        <FiUserMinus size={18} />
      </button>
    </div>
  );
});

// ============================================================================
// SELECTABLE PERSON - Persona seleccionable para asignar
// ============================================================================
interface SelectablePersonItemProps {
  person: Person;
  isSelected: boolean;
  isAlreadyAssigned: boolean;
  onToggle: () => void;
}

const SelectablePersonItem = memo(function SelectablePersonItem({
  person,
  isSelected,
  isAlreadyAssigned,
  onToggle,
}: SelectablePersonItemProps) {
  const initials = `${person.firstName?.[0] || ''}${person.lastName?.[0] || ''}`.toUpperCase();
  const fullName = `${person.firstName} ${person.lastName}`.trim();

  return (
    <button
      onClick={onToggle}
      disabled={isAlreadyAssigned}
      className={`
        w-full flex items-center justify-between p-3 rounded-lg border transition-all
        ${isAlreadyAssigned 
          ? 'bg-neutral-100 border-neutral-200 cursor-not-allowed opacity-60' 
          : isSelected
            ? 'bg-primary-50 border-primary-300 ring-2 ring-primary-200'
            : 'bg-white border-neutral-200 hover:border-primary-300 hover:bg-primary-50/50'
        }
      `}
    >
      <div className="flex items-center gap-3">
        {/* Checkbox visual */}
        <div className={`
          w-5 h-5 rounded border-2 flex items-center justify-center transition-colors
          ${isAlreadyAssigned 
            ? 'bg-neutral-300 border-neutral-300' 
            : isSelected 
              ? 'bg-primary-600 border-primary-600' 
              : 'border-neutral-300'
          }
        `}>
          {(isSelected || isAlreadyAssigned) && (
            <FiCheck size={12} className="text-white" />
          )}
        </div>

        {/* Avatar */}
        {person.email ? ( // Usando un campo como proxy para avatar
          <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium text-sm">
            {initials || <FiUser size={14} />}
          </div>
        ) : (
          <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium text-sm">
            {initials || <FiUser size={14} />}
          </div>
        )}

        <div className="text-left">
          <p className="font-medium text-neutral-800 text-sm">{fullName}</p>
          {person.phone && (
            <p className="text-xs text-neutral-500">{person.phone}</p>
          )}
        </div>
      </div>

      {isAlreadyAssigned && (
        <span className="text-xs text-neutral-500 bg-neutral-200 px-2 py-0.5 rounded">
          Ya asignado
        </span>
      )}
    </button>
  );
});

// ============================================================================
// MAIN MODAL
// ============================================================================

export default memo(function RolePeopleAssignmentModal({
  isOpen,
  role,
  onAssign,
  onRemove,
  onClose,
  isSaving = false,
}: RolePeopleAssignmentModalProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedPersonIds, setSelectedPersonIds] = useState<string[]>([]);
  const [activeTab, setActiveTab] = useState<'assigned' | 'add'>('assigned');

  // Obtener lista de personas disponibles
  const { data: peopleData, isLoading: isLoadingPeople } = usePeople({ page: 0, size: 100 });
  const availablePeople = peopleData?.content || [];

  // Reset al abrir/cerrar
  useEffect(() => {
    if (isOpen) {
      setSearchTerm('');
      setSelectedPersonIds([]);
      setActiveTab(role?.people?.length ? 'assigned' : 'add');
    }
  }, [isOpen, role?.people?.length]);

  // Verificar si es rol de líder
  const isLeader = role ? isLeaderRole(role.name) : false;
  const assignedPeopleIds = useMemo(
    () => new Set(role?.people?.map(p => p.id) || []),
    [role?.people]
  );

  // Filtrar personas disponibles
  const filteredPeople = useMemo(() => {
    if (!searchTerm.trim()) return availablePeople;
    const term = searchTerm.toLowerCase();
    return availablePeople.filter(person => 
      person.firstName?.toLowerCase().includes(term) ||
      person.lastName?.toLowerCase().includes(term) ||
      person.phone?.includes(term)
    );
  }, [availablePeople, searchTerm]);

  // Manejar selección de persona
  const handleTogglePerson = (personId: string) => {
    if (assignedPeopleIds.has(personId)) return;

    setSelectedPersonIds(prev => {
      if (prev.includes(personId)) {
        return prev.filter(id => id !== personId);
      }
      // Si es líder, solo permitir una persona
      if (isLeader && role?.people?.length === 0) {
        return [personId];
      }
      if (isLeader && prev.length >= 1) {
        return [personId]; // Reemplazar selección
      }
      return [...prev, personId];
    });
  };

  // Asignar personas seleccionadas
  const handleAssign = () => {
    if (selectedPersonIds.length > 0) {
      onAssign(selectedPersonIds);
      setSelectedPersonIds([]);
    }
  };

  if (!role) return null;

  const hasAssignedPeople = role.people && role.people.length > 0;
  const canAssignMore = !isLeader || !hasAssignedPeople;

  return (
    <Modal
      isOpen={isOpen}
      title={`Gestionar Personas - ${role.name}`}
      onClose={onClose}
      actions={
        activeTab === 'add' && selectedPersonIds.length > 0 ? (
          <div className="flex gap-2">
            <Button variant="secondary" onClick={onClose} disabled={isSaving}>
              Cancelar
            </Button>
            <Button variant="primary" onClick={handleAssign} disabled={isSaving}>
              {isSaving ? 'Asignando...' : `Asignar ${selectedPersonIds.length} persona${selectedPersonIds.length > 1 ? 's' : ''}`}
            </Button>
          </div>
        ) : (
          <Button variant="secondary" onClick={onClose}>
            Cerrar
          </Button>
        )
      }
    >
      <div className="space-y-4">
        {/* Advertencia para rol líder */}
        {isLeader && (
          <div className="flex items-start gap-2 p-3 bg-amber-50 border border-amber-200 rounded-lg">
            <FiAlertCircle className="text-amber-600 mt-0.5 shrink-0" size={18} />
            <div className="text-sm">
              <p className="font-medium text-amber-800">Rol de Liderazgo</p>
              <p className="text-amber-700">Este rol solo puede tener una persona asignada.</p>
            </div>
          </div>
        )}

        {/* Tabs */}
        <div className="flex border-b border-neutral-200">
          <button
            onClick={() => setActiveTab('assigned')}
            className={`
              px-4 py-2 text-sm font-medium border-b-2 transition-colors
              ${activeTab === 'assigned'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-neutral-500 hover:text-neutral-700'
              }
            `}
          >
            Asignados ({role.people?.length || 0})
          </button>
          <button
            onClick={() => setActiveTab('add')}
            disabled={!canAssignMore}
            className={`
              px-4 py-2 text-sm font-medium border-b-2 transition-colors
              ${activeTab === 'add'
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-neutral-500 hover:text-neutral-700'
              }
              ${!canAssignMore ? 'opacity-50 cursor-not-allowed' : ''}
            `}
          >
            <span className="flex items-center gap-1.5">
              <FiUserPlus size={14} />
              Agregar
            </span>
          </button>
        </div>

        {/* Tab: Personas Asignadas */}
        {activeTab === 'assigned' && (
          <div className="space-y-2">
            {hasAssignedPeople ? (
              role.people.map(person => (
                <AssignedPersonItem
                  key={person.id}
                  person={person}
                  onRemove={() => onRemove(person.id)}
                  isRemoving={isSaving}
                />
              ))
            ) : (
              <div className="text-center py-8 text-neutral-500">
                <FiUser className="w-12 h-12 mx-auto mb-3 text-neutral-300" />
                <p>No hay personas asignadas a este rol</p>
                <button
                  onClick={() => setActiveTab('add')}
                  className="mt-3 text-primary-600 hover:text-primary-700 font-medium text-sm"
                >
                  + Agregar personas
                </button>
              </div>
            )}
          </div>
        )}

        {/* Tab: Agregar Personas */}
        {activeTab === 'add' && (
          <div className="space-y-3">
            {/* Buscador */}
            <div className="relative">
              <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400" size={18} />
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Buscar por nombre o teléfono..."
                className="w-full pl-10 pr-4 py-2.5 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
              {searchTerm && (
                <button
                  onClick={() => setSearchTerm('')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-400 hover:text-neutral-600"
                >
                  <FiX size={16} />
                </button>
              )}
            </div>

            {/* Selección múltiple info */}
            {selectedPersonIds.length > 0 && (
              <div className="flex items-center justify-between p-2 bg-primary-50 border border-primary-200 rounded-lg">
                <span className="text-sm text-primary-700">
                  {selectedPersonIds.length} persona{selectedPersonIds.length > 1 ? 's' : ''} seleccionada{selectedPersonIds.length > 1 ? 's' : ''}
                </span>
                <button
                  onClick={() => setSelectedPersonIds([])}
                  className="text-sm text-primary-600 hover:text-primary-700 font-medium"
                >
                  Limpiar selección
                </button>
              </div>
            )}

            {/* Lista de personas */}
            <div className="max-h-[300px] overflow-y-auto space-y-2">
              {isLoadingPeople ? (
                <div className="flex items-center justify-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
                </div>
              ) : filteredPeople.length === 0 ? (
                <div className="text-center py-8 text-neutral-500">
                  <p>No se encontraron personas</p>
                </div>
              ) : (
                filteredPeople.map(person => (
                  <SelectablePersonItem
                    key={person.id}
                    person={person}
                    isSelected={selectedPersonIds.includes(person.id)}
                    isAlreadyAssigned={assignedPeopleIds.has(person.id)}
                    onToggle={() => handleTogglePerson(person.id)}
                  />
                ))
              )}
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
});
