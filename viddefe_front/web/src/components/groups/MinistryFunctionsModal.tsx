import { useState, useMemo, useCallback } from 'react';
import { Modal, Button, DropDown } from '../shared';
import { 
  useMinistryFunctions, 
  useMinistryRoles,
  useCreateMinistryFunction, 
  useDeleteMinistryFunction,
  useInfinitePeople
} from '../../hooks';
import type { 
  MinistryFunction, 
  EventType, 
  Person, 
  RoleStrategyNode 
} from '../../models';
import { FiBriefcase, FiPlus, FiTrash2, FiUser } from 'react-icons/fi';

// ============================================================================
// HELPERS
// ============================================================================

/**
 * Extrae todas las personas de una jerarquía de roles de forma recursiva
 */
function extractPeopleFromHierarchy(hierarchy: RoleStrategyNode[]): Person[] {
  const people: Person[] = [];
  
  function traverse(nodes: RoleStrategyNode[]) {
    for (const node of nodes) {
      if (node.people && node.people.length > 0) {
        people.push(...node.people);
      }
      if (node.children && node.children.length > 0) {
        traverse(node.children);
      }
    }
  }
  
  traverse(hierarchy);
  
  // Eliminar duplicados por ID
  const uniquePeople = Array.from(
    new Map(people.map(p => [p.id, p])).values()
  );
  
  return uniquePeople;
}

// ============================================================================
// TYPES
// ============================================================================

interface MinistryFunctionsModalProps {
  isOpen: boolean;
  meetingId: string;
  eventType: EventType;
  /** Jerarquía del grupo - solo requerida para GROUP_MEETING */
  hierarchy?: RoleStrategyNode[];
  onClose: () => void;
}

interface MinistryFunctionFormData {
  peopleId: string;
  roleId: string;
}

// ============================================================================
// FUNCTION ITEM - Muestra una función ministerial asignada
// ============================================================================

interface FunctionItemProps {
  ministryFunction: MinistryFunction;
  onDelete: (id: string) => void;
  isDeleting: boolean;
}

function FunctionItem({ ministryFunction, onDelete, isDeleting }: Readonly<FunctionItemProps>) {
  const fullName = `${ministryFunction.people.firstName} ${ministryFunction.people.lastName}`.trim();
  const initials = `${ministryFunction.people.firstName?.[0] || ''}${ministryFunction.people.lastName?.[0] || ''}`.toUpperCase();

  return (
    <div className="flex items-center justify-between p-3 bg-neutral-50 rounded-lg border border-neutral-200 hover:border-neutral-300 transition-colors group">
      <div className="flex items-center gap-3">
        {ministryFunction.people.avatar ? (
          <img
            src={ministryFunction.people.avatar}
            alt={fullName}
            className="w-10 h-10 rounded-full object-cover"
          />
        ) : (
          <span className="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium">
            {initials || <FiUser size={16} />}
          </span>
        )}
        <div>
          <p className="font-medium text-neutral-800">{fullName || 'Sin nombre'}</p>
          <p className="text-sm text-neutral-500">{ministryFunction.people.phone || 'Sin teléfono'}</p>
        </div>
      </div>
      
      <div className="flex items-center gap-3">
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-medium bg-primary-100 text-primary-800">
          <FiBriefcase size={14} />
          {ministryFunction.role.name}
        </span>
        
        <button
          onClick={() => onDelete(ministryFunction.id)}
          disabled={isDeleting}
          className="p-2 text-neutral-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors opacity-0 group-hover:opacity-100"
          title="Eliminar asignación"
        >
          <FiTrash2 size={16} />
        </button>
      </div>
    </div>
  );
}

// ============================================================================
// MAIN COMPONENT
// ============================================================================

export default function MinistryFunctionsModal({
  isOpen,
  meetingId,
  eventType,
  hierarchy = [],
  onClose,
}: Readonly<MinistryFunctionsModalProps>) {
  // Estado del formulario
  const [formData, setFormData] = useState<MinistryFunctionFormData>({
    peopleId: '',
    roleId: '',
  });
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  // Queries
  const { data: ministryFunctions = [], isLoading } = useMinistryFunctions(meetingId, eventType);
  const { data: roles = [] } = useMinistryRoles();
  
  // Para TEMPLE_WORSHIP: cargar personas del endpoint general
  // El hook se ejecuta siempre, pero solo se usa para cultos
  const { 
    data: peoplePages, 
    fetchNextPage, 
    hasNextPage, 
    isFetchingNextPage 
  } = useInfinitePeople({ size: 20 });

  // Mutations
  const createMinistryFunction = useCreateMinistryFunction(meetingId, eventType);
  const deleteMinistryFunction = useDeleteMinistryFunction(meetingId, eventType);

  // Obtener lista de personas según el tipo de evento
  const availablePeople = useMemo(() => {
    if (eventType === 'GROUP_MEETING') {
      // Para reuniones de grupo: extraer de la jerarquía
      return extractPeopleFromHierarchy(hierarchy);
    } else {
      // Para cultos: usar el endpoint de personas
      if (!peoplePages?.pages) return [];
      return peoplePages.pages.flatMap(page => page.content ?? []);
    }
  }, [eventType, hierarchy, peoplePages]);

  // Filtrar personas ya asignadas y por búsqueda
  const filteredPeople = useMemo(() => {
    const assignedIds = new Set(ministryFunctions.map(mf => mf.people.id));
    const filtered = availablePeople.filter(p => !assignedIds.has(p.id));
    
    if (!searchTerm.trim()) return filtered;
    
    const search = searchTerm.toLowerCase();
    return filtered.filter(p => 
      `${p.firstName} ${p.lastName}`.toLowerCase().includes(search) ||
      p.phone?.toLowerCase().includes(search)
    );
  }, [availablePeople, ministryFunctions, searchTerm]);

  // Opciones para el selector de personas
  const personOptions = useMemo(() => 
    filteredPeople.map(p => ({
      value: p.id,
      label: `${p.firstName} ${p.lastName}`,
      avatar: p.avatar,
    })),
  [filteredPeople]);

  // Opciones para el selector de roles
  const roleOptions = useMemo(() => 
    roles.map(r => ({
      value: String(r.id),
      label: r.name,
    })),
  [roles]);

  const handleLoadMorePeople = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const validateForm = (): boolean => {
    if (!formData.peopleId) {
      setError('Selecciona una persona');
      return false;
    }
    if (!formData.roleId) {
      setError('Selecciona una función');
      return false;
    }
    setError('');
    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    try {
      await createMinistryFunction.mutateAsync({
        peopleId: formData.peopleId,
        roleId: Number(formData.roleId),
      });
      
      // Limpiar formulario
      setFormData({ peopleId: '', roleId: '' });
      setError('');
    } catch (err: any) {
      setError(err.message || 'Error al asignar la función');
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteMinistryFunction.mutateAsync(id);
    } catch (err: any) {
      setError(err.message || 'Error al eliminar la asignación');
    }
  };

  const handleClose = () => {
    setFormData({ peopleId: '', roleId: '' });
    setError('');
    setSearchTerm('');
    onClose();
  };

  const eventTypeLabel = eventType === 'TEMPLE_WORHSIP' ? 'Culto' : 'Reunión de Grupo';

  return (
    <Modal
      isOpen={isOpen}
      title="Funciones Ministeriales"
      onClose={handleClose}
    >
      <div className="space-y-6">
        {/* Header info */}
        <div className="bg-primary-50 rounded-lg p-4">
          <p className="text-sm text-primary-700">
            <strong>Tipo de evento:</strong> {eventTypeLabel}
          </p>
          <p className="text-xs text-primary-600 mt-1">
            {eventType === 'GROUP_MEETING' 
              ? 'Las personas disponibles son las que pertenecen a la jerarquía del grupo.'
              : 'Puedes seleccionar cualquier persona registrada en el sistema.'
            }
          </p>
        </div>

        {/* Formulario de nueva asignación */}
        <div className="border border-neutral-200 rounded-lg p-4">
          <h4 className="font-medium text-neutral-800 mb-4 flex items-center gap-2">
            <FiPlus className="text-primary-600" />
            Nueva Asignación
          </h4>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            <div>
              <DropDown
                label="Persona"
                options={personOptions}
                value={formData.peopleId}
                onChangeValue={(value) => setFormData(prev => ({ ...prev, peopleId: value }))}
                placeholder="Seleccionar persona..."
                searchKey="label"
                hasMore={eventType === 'TEMPLE_WORHSIP' && hasNextPage}
                isLoadingMore={isFetchingNextPage}
                onLoadMore={handleLoadMorePeople}
              />
            </div>

            <div>
              <DropDown
                label="Función"
                options={roleOptions}
                value={formData.roleId}
                onChangeValue={(value) => setFormData(prev => ({ ...prev, roleId: value }))}
                placeholder="Seleccionar función..."
              />
            </div>
          </div>

          {error && (
            <p className="text-red-500 text-sm mb-4">{error}</p>
          )}

          <div className="flex justify-end">
            <Button
              variant="primary"
              onClick={handleSubmit}
              disabled={createMinistryFunction.isPending}
            >
              {createMinistryFunction.isPending ? 'Asignando...' : 'Asignar Función'}
            </Button>
          </div>
        </div>

        {/* Lista de funciones asignadas */}
        <div>
          <h4 className="font-medium text-neutral-800 mb-4 flex items-center gap-2">
            <FiBriefcase className="text-primary-600" />
            Funciones Asignadas
            {ministryFunctions.length > 0 && (
              <span className="ml-auto text-sm text-neutral-500">
                {ministryFunctions.length} {ministryFunctions.length === 1 ? 'persona' : 'personas'}
              </span>
            )}
          </h4>

          {isLoading ? (
            <div className="flex justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
            </div>
          ) : ministryFunctions.length === 0 ? (
            <div className="text-center py-8 bg-neutral-50 rounded-lg border-2 border-dashed border-neutral-200">
              <FiBriefcase className="mx-auto h-12 w-12 text-neutral-300 mb-3" />
              <p className="text-neutral-500">No hay funciones asignadas</p>
              <p className="text-sm text-neutral-400 mt-1">
                Selecciona una persona y una función para comenzar
              </p>
            </div>
          ) : (
            <div className="space-y-2 max-h-75 overflow-y-auto">
              {ministryFunctions.map((mf) => (
                <FunctionItem
                  key={mf.id}
                  ministryFunction={mf}
                  onDelete={handleDelete}
                  isDeleting={deleteMinistryFunction.isPending}
                />
              ))}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex justify-end pt-4 border-t border-neutral-200">
          <Button variant="secondary" onClick={handleClose}>
            Cerrar
          </Button>
        </div>
      </div>
    </Modal>
  );
}
