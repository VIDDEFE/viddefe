import { useState, useCallback } from 'react';

interface UseFormState<T> {
  values: T;
  setValues: (values: T) => void;
  setField: (field: keyof T, value: any) => void;
  reset: () => void;
}

export const useForm = <T,>(initialValues: T): UseFormState<T> => {
  const [values, setValues] = useState<T>(initialValues);

  const setField = useCallback((field: keyof T, value: any) => {
    setValues(prev => ({
      ...prev,
      [field]: value,
    }));
  }, []);

  const reset = useCallback(() => {
    setValues(initialValues);
  }, [initialValues]);

  return {
    values,
    setValues,
    setField,
    reset,
  };
};

// Hook para toggle
export const useToggle = (initialState: boolean = false) => {
  const [state, setState] = useState(initialState);
  const toggle = useCallback(() => setState(prev => !prev), []);
  const setTrue = useCallback(() => setState(true), []);
  const setFalse = useCallback(() => setState(false), []);

  return {
    state,
    toggle,
    setTrue,
    setFalse,
  };
};

// Hook para manejo de modal
export const useModal = (initialState: boolean = false) => {
  const [isOpen, setIsOpen] = useState(initialState);
  const open = useCallback(() => setIsOpen(true), []);
  const close = useCallback(() => setIsOpen(false), []);
  const toggle = useCallback(() => setIsOpen(prev => !prev), []);

  return {
    isOpen,
    open,
    close,
    toggle,
  };
};

// Hook para fetching de datos
export const useFetch = <T,>(
  fetchFn: () => Promise<T>,
  initialData?: T
) => {
  const [data, setData] = useState<T | undefined>(initialData);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const execute = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetchFn();
      setData(result);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Error desconocido'));
    } finally {
      setLoading(false);
    }
  }, [fetchFn]);

  return {
    data,
    loading,
    error,
    execute,
  };
};
