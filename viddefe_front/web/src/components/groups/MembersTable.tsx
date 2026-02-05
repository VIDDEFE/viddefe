import { useState, useCallback } from 'react';
import { useQueryClient, useMutation } from '@tanstack/react-query';
import type { Pageable } from '../../services/api';
import { Table, Button, Modal, DropDown } from '../shared';
import { FiPlus, FiChevronLeft, FiChevronRight } from 'react-icons/fi';

type Member = {
  id: string;
  firstName: string;
  lastName: string;
  avatar?: string;
  phone?: string;
};

interface MembersTableProps {
  readonly groupId: string;
  readonly membersData?: Pageable<Member>;
  readonly isLoading: boolean;
  readonly page: number;
  readonly pageSize: number;
  readonly onPageChange: (page: number) => void;
  readonly onPageSizeChange: (size: number) => void;
  readonly editable?: boolean;
}

export default function MembersTable({
  groupId,
  membersData,
  isLoading,
  page,
  pageSize,
  onPageChange,
  onPageSizeChange,
  editable = false,
}: MembersTableProps) {
  const qc = useQueryClient();
  
  // Remove member mutation
  const removeMember = useMutation({
    mutationFn: (personId: string) =>
      typeof groupId === 'string' && groupId
        ? import('../../services/groupService').then(m => m.groupService.removeMember(groupId, personId))
        : Promise.reject(new Error('No groupId')),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['groupMembers', groupId] })
  });

  // Add member modal state (only used when editable)
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [selectedPersonId, setSelectedPersonId] = useState<string | null>(null);
  const [peoplePage, setPeoplePage] = useState(0);
  const [peopleData, setPeopleData] = useState<Pageable<Member> | null>(null);
  const [isLoadingPeople, setIsLoadingPeople] = useState(false);
  const peoplePageSize = 10; // Fixed page size for modal dropdown

  // Add member mutation
  const addMember = useMutation({
    mutationFn: (personId: string) =>
      typeof groupId === 'string' && groupId
        ? import('../../services/groupService').then(m => m.groupService.addMember(groupId, personId))
        : Promise.reject(new Error('No groupId')),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['groupMembers', groupId] });
      setAddModalOpen(false);
      setSelectedPersonId(null);
      setPeoplePage(0);
      setPeopleData(null);
    }
  });

  // Fetch people (paginated) for add modal
  const fetchPeople = useCallback(async (page: number, size: number) => {
    setIsLoadingPeople(true);
    const mod = await import('../../services/personService');
    const res = await mod.personService.getAll({ page, size });
    setPeopleData(res);
    setIsLoadingPeople(false);
  }, []);

  // Open modal and fetch first page
  const handleOpenAddModal = () => {
    setAddModalOpen(true);
    setPeoplePage(0);
    setSelectedPersonId(null);
    fetchPeople(0, peoplePageSize);
  };

  // Pagination handlers for people dropdown
  const handlePeoplePageChange = (newPage: number) => {
    setPeoplePage(newPage);
    fetchPeople(newPage, peoplePageSize);
  };

  // Add member submit
  const handleAddMember = () => {
    if (selectedPersonId) {
      addMember.mutate(selectedPersonId);
    }
  };

  // Table columns
  const columns = [
    {
      key: 'firstName' as const,
      label: 'Nombre',
      render: (_: any, m: Member) => (
        <span className="flex items-center gap-2">
          {m.avatar ? (
            <img src={m.avatar} alt={m.firstName} className="w-6 h-6 rounded-full object-cover" />
          ) : (
            <span className="w-6 h-6 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-semibold text-xs">
              {m.firstName?.[0]}{m.lastName?.[0]}
            </span>
          )}
          <span>{m.firstName} {m.lastName}</span>
        </span>
      ),
    },
    {
      key: 'phone' as const,
      label: 'Teléfono',
      render: (v: any) => v || <span className="text-neutral-400">-</span>,
    },
    ...(editable ? [{
      key: 'id' as const,
      label: 'Acciones',
      render: (_: any, m: Member) => (
        <button
          className="text-red-600 hover:underline text-xs ml-2"
          onClick={() => removeMember.mutate(m.id)}
          disabled={removeMember.isPending}
        >
          Quitar
        </button>
      ),
    }] : []),
  ];

  return (
    <div>
      {editable && (
        <div className="flex items-center justify-between mb-2">
          <span className="font-semibold text-neutral-700">Miembros</span>
          <Button size="sm" variant="primary" onClick={handleOpenAddModal}>
            <FiPlus className="inline mr-1" /> Agregar miembro
          </Button>
        </div>
      )}
      
      <Table
        data={membersData?.content || []}
        columns={columns}
        loading={isLoading}
        pagination={{
          mode: 'manual',
          currentPage: page,
          totalPages: membersData?.totalPages ?? 0,
          totalElements: membersData?.totalElements ?? 0,
          pageSize: pageSize,
          onPageChange,
          onPageSizeChange,
        }}
      />

      {/* Add Member Modal - only shown when editable */}
      <Modal
        isOpen={editable && addModalOpen}
        onClose={() => setAddModalOpen(false)}
        title="Agregar miembro al grupo"
        actions={
          <>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setAddModalOpen(false)}
              disabled={addMember.isPending}
            >
              Cancelar
            </Button>
            <Button
              variant="primary"
              size="sm"
              onClick={handleAddMember}
              disabled={!selectedPersonId || addMember.isPending}
            >
              {addMember.isPending ? 'Agregando...' : 'Agregar'}
            </Button>
          </>
        }
      >
        <div className="space-y-4">
          {/* Persona selector */}
          <DropDown
            label="Selecciona una persona"
            placeholder="Busca o selecciona una persona..."
            value={selectedPersonId || ''}
            onChangeValue={setSelectedPersonId}
            options={
              !isLoadingPeople && peopleData?.content
                ? peopleData.content.map(person => {
                    const phone = person.phone ? ` (${person.phone})` : '';
                    return {
                      value: person.id,
                      label: `${person.firstName} ${person.lastName}${phone}`,
                    };
                  })
                : []
            }
            labelKey="label"
            valueKey="value"
            disabled={isLoadingPeople || !peopleData?.content?.length}
            hasMore={false}
          />

          {isLoadingPeople && (
            <div className="text-center text-neutral-500 py-6 bg-neutral-50 rounded-lg">
              <div className="inline-flex items-center gap-2">
                <div className="w-4 h-4 border-2 border-primary-600 border-t-transparent rounded-full animate-spin" />
                Cargando personas...
              </div>
            </div>
          )}

          {!isLoadingPeople && !peopleData?.content?.length && (
            <div className="text-center text-neutral-500 py-6 bg-amber-50 border border-amber-200 rounded-lg">
              No hay personas disponibles
            </div>
          )}

          {/* Pagination controls */}
          {peopleData && peopleData.totalPages > 1 && (
            <div className="flex items-center justify-between p-3 bg-neutral-50 rounded-lg border border-neutral-200">
              <Button
                size="sm"
                variant="secondary"
                onClick={() => handlePeoplePageChange(Math.max(peoplePage - 1, 0))}
                disabled={peoplePage === 0 || isLoadingPeople}
                className="px-2!"
              >
                <FiChevronLeft size={16} />
              </Button>
              <span className="text-sm text-neutral-600 font-medium">
                Página {peoplePage + 1} de {peopleData.totalPages}
              </span>
              <Button
                size="sm"
                variant="secondary"
                onClick={() => handlePeoplePageChange(Math.min(peoplePage + 1, peopleData.totalPages - 1))}
                disabled={peoplePage >= peopleData.totalPages - 1 || isLoadingPeople}
                className="px-2!"
              >
                <FiChevronRight size={16} />
              </Button>
            </div>
          )}
        </div>
      </Modal>
    </div>
  );
}
