import { useState, useEffect, useMemo, memo, useCallback, useRef } from 'react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { Modal, Button } from '../shared';
import type { RoleStrategyNode, Person } from '../../models';
import type { Pageable } from '../../services/api';
import { personService } from '../../services/personService';
import { FiSearch, FiUser, FiUserMinus, FiAlertCircle, FiCheck, FiX, FiLoader, FiChevronDown } from 'react-icons/fi';

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
// PAGINATED PERSON DROPDOWN - DropDown con paginación infinita
// ============================================================================
interface PaginatedPersonDropdownProps {
  selectedIds: string[];
  excludeIds: Set<string>;
  onToggle: (personId: string) => void;
  isMultiple?: boolean;
  placeholder?: string;
}

const PAGE_SIZE = 10;

const PaginatedPersonDropdown = memo(function PaginatedPersonDropdown({
  selectedIds,
  excludeIds,
  onToggle,
  placeholder = "Seleccionar personas...",
}: PaginatedPersonDropdownProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const wrapperRef = useRef<HTMLDivElement>(null);
  const listRef = useRef<HTMLDivElement>(null);

  // Debounce search
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedSearch(searchTerm), 300);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  // Cerrar al hacer clic fuera
  useEffect(() => {
    function handler(e: MouseEvent) {
      if (!wrapperRef.current?.contains(e.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  // Fetch paginado - solo cuando está abierto
  const {
    data: peoplePages,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
  } = useInfiniteQuery<Pageable<Person>, Error>({
    queryKey: ['people-dropdown', debouncedSearch],
    queryFn: async ({ pageParam = 0 }) => {
      const params: { page: number; size: number; search?: string } = {
        page: pageParam as number,
        size: PAGE_SIZE,
      };
      if (debouncedSearch.trim()) {
        params.search = debouncedSearch.trim();
      }
      return personService.getAll(params);
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.totalPages === lastPage.number + 1) return undefined;
      return lastPage.number + 1;
    },
    initialPageParam: 0,
    enabled: isOpen,
    staleTime: 30 * 1000,
  });

  // Combinar páginas
  const allPeople = useMemo(() => {
    if (!peoplePages) return [];
    return peoplePages.pages.flatMap(page => page.content);
  }, [peoplePages]);

  // Filtrar localmente como fallback
  const filteredPeople = useMemo(() => {
    if (!debouncedSearch.trim()) return allPeople;
    const term = debouncedSearch.toLowerCase();
    return allPeople.filter(person => 
      person.firstName?.toLowerCase().includes(term) ||
      person.lastName?.toLowerCase().includes(term) ||
      person.phone?.includes(term)
    );
  }, [allPeople, debouncedSearch]);

  // Scroll infinito
  const handleScroll = useCallback((e: React.UIEvent<HTMLDivElement>) => {
    const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;
    if (scrollHeight - scrollTop <= clientHeight * 1.5 && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [fetchNextPage, hasNextPage, isFetchingNextPage]);

  // Texto del botón
  const buttonLabel = selectedIds.length > 0
    ? `${selectedIds.length} persona${selectedIds.length > 1 ? 's' : ''} seleccionada${selectedIds.length > 1 ? 's' : ''}`
    : placeholder;

  return (
    <div ref={wrapperRef} className="relative w-full">
      {/* Trigger */}
      <button
        type="button"
        onClick={() => setIsOpen(o => !o)}
        className={`
          px-3 py-3 w-full text-left border-2 rounded-lg text-base transition-all duration-300 bg-white
          flex items-center justify-between
          ${isOpen ? 'border-primary-500 ring-2 ring-primary-300' : 'border-neutral-200'}
          hover:border-primary-400
        `}
      >
        <span className={`truncate ${selectedIds.length === 0 ? 'text-neutral-400' : 'text-neutral-800'}`}>
          {buttonLabel}
        </span>
        <FiChevronDown className={`ml-2 text-xl transition-transform duration-200 ${isOpen ? 'rotate-180' : ''} text-neutral-500`} />
      </button>

      {/* Dropdown panel */}
      {isOpen && (
        <div className="absolute top-full left-0 right-0 mt-1 bg-white border border-neutral-200 rounded-lg shadow-lg overflow-hidden">
          {/* Search */}
          <div className="relative border-b border-neutral-200">
            <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-neutral-400" size={16} />
            <input
              autoFocus
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Buscar por nombre..."
              className="px-3 py-2.5 pl-9 w-full text-sm focus:outline-none"
            />
            {searchTerm && (
              <button
                onClick={() => setSearchTerm('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-400 hover:text-neutral-600"
              >
                <FiX size={14} />
              </button>
            )}
          </div>

          {/* Options list */}
          <div 
            ref={listRef}
            className="max-h-60 overflow-auto"
            onScroll={handleScroll}
          >
            {isLoading && filteredPeople.length === 0 ? (
              <div className="flex items-center justify-center py-6">
                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-primary-600" />
              </div>
            ) : filteredPeople.length === 0 ? (
              <div className="px-3 py-4 text-neutral-500 text-sm text-center">
                No se encontraron personas
              </div>
            ) : (
              <>
                {filteredPeople.map((person) => {
                  const isExcluded = excludeIds.has(person.id);
                  const isSelected = selectedIds.includes(person.id);
                  const fullName = `${person.firstName} ${person.lastName}`.trim();
                  const initials = `${person.firstName?.[0] || ''}${person.lastName?.[0] || ''}`.toUpperCase();

                  return (
                    <div
                      key={person.id}
                      onClick={() => !isExcluded && onToggle(person.id)}
                      className={`
                        px-3 py-2.5 flex items-center gap-3 cursor-pointer transition
                        ${isExcluded 
                          ? 'bg-neutral-100 cursor-not-allowed opacity-50' 
                          : isSelected 
                            ? 'bg-primary-50 hover:bg-primary-100' 
                            : 'hover:bg-neutral-50'
                        }
                      `}
                    >
                      {/* Checkbox */}
                      <div className={`
                        w-4 h-4 rounded border-2 flex items-center justify-center shrink-0
                        ${isExcluded 
                          ? 'bg-neutral-300 border-neutral-300' 
                          : isSelected 
                            ? 'bg-primary-600 border-primary-600' 
                            : 'border-neutral-300'
                        }
                      `}>
                        {(isSelected || isExcluded) && <FiCheck size={10} className="text-white" />}
                      </div>

                      {/* Avatar */}
                      <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium text-xs shrink-0">
                        {initials || <FiUser size={12} />}
                      </div>

                      {/* Info */}
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-neutral-800 truncate">{fullName}</p>
                        {person.phone && (
                          <p className="text-xs text-neutral-500">{person.phone}</p>
                        )}
                      </div>

                      {isExcluded && (
                        <span className="text-xs text-neutral-400 bg-neutral-200 px-1.5 py-0.5 rounded shrink-0">
                          Asignado
                        </span>
                      )}
                    </div>
                  );
                })}

                {/* Loading more */}
                {isFetchingNextPage && (
                  <div className="flex items-center justify-center py-2 gap-2">
                    <FiLoader className="animate-spin text-primary-600" size={16} />
                    <span className="text-xs text-neutral-500">Cargando más...</span>
                  </div>
                )}

                {/* End indicator */}
                {!hasNextPage && filteredPeople.length >= PAGE_SIZE && (
                  <div className="text-center py-2 text-xs text-neutral-400">
                    — Fin de la lista —
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
});

// ============================================================================
// PERSON ITEM - Item de persona en la lista de asignados
// ============================================================================
interface AssignedPersonItemProps {
  person: Person;
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
  const [selectedPersonIds, setSelectedPersonIds] = useState<string[]>([]);

  // Reset al abrir/cerrar
  useEffect(() => {
    if (isOpen) {
      setSelectedPersonIds([]);
    }
  }, [isOpen]);

  // Verificar si es rol de líder
  const isLeader = role ? isLeaderRole(role.name) : false;
  const assignedPeopleIds = useMemo(
    () => new Set(role?.people?.map(p => p.id) || []),
    [role?.people]
  );

  // Manejar selección de persona
  const handleTogglePerson = useCallback((personId: string) => {
    if (assignedPeopleIds.has(personId)) return;

    setSelectedPersonIds(prev => {
      if (prev.includes(personId)) {
        return prev.filter(id => id !== personId);
      }
      // Si es líder, solo permitir una persona
      if (isLeader) {
        return [personId];
      }
      return [...prev, personId];
    });
  }, [assignedPeopleIds, isLeader]);

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
        <div className="flex gap-2">
          <Button variant="secondary" onClick={onClose} disabled={isSaving}>
            Cerrar
          </Button>
          {selectedPersonIds.length > 0 && (
            <Button variant="primary" onClick={handleAssign} disabled={isSaving}>
              {isSaving ? 'Asignando...' : `Asignar ${selectedPersonIds.length}`}
            </Button>
          )}
        </div>
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

        {/* Dropdown para agregar personas */}
        {canAssignMore && (
          <div>
            <label className="block text-sm font-medium text-neutral-700 mb-1.5">
              Agregar personas
            </label>
            <PaginatedPersonDropdown
              selectedIds={selectedPersonIds}
              excludeIds={assignedPeopleIds}
              onToggle={handleTogglePerson}
              isMultiple={!isLeader}
              placeholder={isLeader ? "Seleccionar líder..." : "Seleccionar personas..."}
            />
          </div>
        )}

        {/* Personas actualmente asignadas */}
        <div>
          <label className="block text-sm font-medium text-neutral-700 mb-2">
            Personas asignadas ({role.people?.length || 0})
          </label>
          <div className="space-y-2 max-h-[250px] overflow-y-auto">
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
              <div className="text-center py-6 text-neutral-500 bg-neutral-50 rounded-lg border border-dashed border-neutral-300">
                <FiUser className="w-10 h-10 mx-auto mb-2 text-neutral-300" />
                <p className="text-sm">No hay personas asignadas</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </Modal>
  );
});
