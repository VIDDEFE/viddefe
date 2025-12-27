import { useState } from 'react';
import { Button, Modal, Form, Input } from '../shared';
import { useStrategies, useCreateStrategy, useUpdateStrategy, useDeleteStrategy } from '../../hooks';
import type { Strategy } from '../../models';
import { FiEdit2, FiTrash2 } from 'react-icons/fi';

interface StrategyManagerProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function StrategyManager({ isOpen, onClose }: StrategyManagerProps) {
  const { data: strategies = [], isLoading } = useStrategies();
  const createStrategy = useCreateStrategy();
  const updateStrategy = useUpdateStrategy();
  const deleteStrategy = useDeleteStrategy();

  const [mode, setMode] = useState<'list' | 'create' | 'edit' | 'delete'>('list');
  const [selectedStrategy, setSelectedStrategy] = useState<Strategy | null>(null);
  const [strategyName, setStrategyName] = useState('');
  const [error, setError] = useState('');

  const resetState = () => {
    setMode('list');
    setSelectedStrategy(null);
    setStrategyName('');
    setError('');
  };

  const handleCreate = () => {
    if (!strategyName.trim()) {
      setError('El nombre es requerido');
      return;
    }
    createStrategy.mutate(
      { name: strategyName.trim() },
      {
        onSuccess: () => resetState(),
        onError: () => setError('Error al crear la estrategia'),
      }
    );
  };

  const handleUpdate = () => {
    if (!selectedStrategy || !strategyName.trim()) {
      setError('El nombre es requerido');
      return;
    }
    updateStrategy.mutate(
      { id: selectedStrategy.id, data: { name: strategyName.trim() } },
      {
        onSuccess: () => resetState(),
        onError: () => setError('Error al actualizar la estrategia'),
      }
    );
  };

  const handleDelete = () => {
    if (!selectedStrategy) return;
    deleteStrategy.mutate(selectedStrategy.id, {
      onSuccess: () => resetState(),
      onError: () => setError('Error al eliminar la estrategia'),
    });
  };

  const openCreate = () => {
    setMode('create');
    setStrategyName('');
    setError('');
  };

  const openEdit = (strategy: Strategy) => {
    setSelectedStrategy(strategy);
    setStrategyName(strategy.name);
    setMode('edit');
    setError('');
  };

  const openDelete = (strategy: Strategy) => {
    setSelectedStrategy(strategy);
    setMode('delete');
    setError('');
  };

  const isMutating = createStrategy.isPending || updateStrategy.isPending || deleteStrategy.isPending;

  return (
    <Modal
      isOpen={isOpen}
      title="Gestionar Estrategias"
      onClose={() => {
        resetState();
        onClose();
      }}
      actions={
        mode === 'list' ? (
          <div className="flex gap-2">
            <Button variant="primary" onClick={openCreate}>
              + Nueva Estrategia
            </Button>
            <Button variant="secondary" onClick={onClose}>
              Cerrar
            </Button>
          </div>
        ) : mode === 'create' ? (
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleCreate} disabled={isMutating}>
              {createStrategy.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
            <Button variant="secondary" onClick={resetState}>
              Cancelar
            </Button>
          </div>
        ) : mode === 'edit' ? (
          <div className="flex gap-2">
            <Button variant="primary" onClick={handleUpdate} disabled={isMutating}>
              {updateStrategy.isPending ? 'Guardando...' : 'Guardar'}
            </Button>
            <Button variant="secondary" onClick={resetState}>
              Cancelar
            </Button>
          </div>
        ) : (
          <div className="flex gap-2">
            <Button variant="danger" onClick={handleDelete} disabled={isMutating}>
              {deleteStrategy.isPending ? 'Eliminando...' : 'Eliminar'}
            </Button>
            <Button variant="secondary" onClick={resetState}>
              Cancelar
            </Button>
          </div>
        )
      }
    >
      {mode === 'list' && (
        <div>
          {isLoading ? (
            <div className="flex justify-center items-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600" />
              <span className="ml-2 text-neutral-600">Cargando estrategias...</span>
            </div>
          ) : strategies.length === 0 ? (
            <div className="text-center py-8 text-neutral-500">
              No hay estrategias creadas. ¡Crea la primera!
            </div>
          ) : (
            <ul className="divide-y divide-neutral-200">
              {strategies.map((strategy) => (
                <li
                  key={strategy.id}
                  className="flex items-center justify-between py-3 px-2 hover:bg-neutral-50 rounded-lg transition-colors"
                >
                  <span className="font-medium text-neutral-800">{strategy.name}</span>
                  <div className="flex gap-2">
                    <button
                      onClick={() => openEdit(strategy)}
                      className="p-2 text-primary-600 hover:text-primary-800 hover:bg-primary-50 rounded-lg transition-colors"
                      title="Editar"
                    >
                      <FiEdit2 size={16} />
                    </button>
                    <button
                      onClick={() => openDelete(strategy)}
                      className="p-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-colors"
                      title="Eliminar"
                    >
                      <FiTrash2 size={16} />
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}

      {(mode === 'create' || mode === 'edit') && (
        <Form>
          <Input
            label="Nombre de la Estrategia"
            placeholder="Ej: Discipulado"
            value={strategyName}
            onChange={(e) => {
              setStrategyName(e.target.value);
              if (error) setError('');
            }}
            error={error}
            autoFocus
          />
        </Form>
      )}

      {mode === 'delete' && selectedStrategy && (
        <div className="text-center py-4">
          <div className="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
            <svg
              className="h-6 w-6 text-red-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
          </div>
          <p className="text-neutral-700">
            ¿Estás seguro de que deseas eliminar la estrategia{' '}
            <strong className="text-primary-800">{selectedStrategy.name}</strong>?
          </p>
          <p className="text-neutral-500 text-sm mt-2">
            Los grupos asociados a esta estrategia quedarán sin estrategia.
          </p>
          {error && <p className="text-red-600 text-sm mt-2">{error}</p>}
        </div>
      )}
    </Modal>
  );
}
