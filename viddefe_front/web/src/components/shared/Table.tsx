import React from 'react';
import { FiEdit2, FiTrash2, FiEye } from 'react-icons/fi';

interface TableColumn<T> {
  key: keyof T;
  label: string;
  render?: (value: T[keyof T], item: T) => React.ReactNode;
}

interface TableAction<T> {
  icon: 'edit' | 'delete' | 'view';
  label: string;
  onClick: (item: T) => void;
  variant?: 'primary' | 'danger' | 'secondary';
}

interface TableProps<T extends { id: string }> {
  data: T[];
  columns: TableColumn<T>[];
  onRowClick?: (item: T) => void;
  actions?: TableAction<T>[];
  loading?: boolean;
}

const iconMap = {
  edit: FiEdit2,
  delete: FiTrash2,
  view: FiEye,
};

const variantClasses = {
  primary: 'text-primary-600 hover:text-primary-800 hover:bg-primary-50',
  secondary: 'text-neutral-600 hover:text-neutral-800 hover:bg-neutral-50',
  danger: 'text-red-600 hover:text-red-800 hover:bg-red-50',
};

export default function Table<T extends { id: string }>({
  data,
  columns,
  onRowClick,
  actions,
  loading = false,
}: TableProps<T>) {
  return (
    <div className="bg-white rounded-xl shadow-md mb-8 border border-neutral-200 overflow-x-auto overflow-y-auto max-h-[70vh]">
      <table className="w-full border-collapse min-w-max">
        <thead className="from-primary-50 to-primary-100 border-b-2 border-primary-300 sticky top-0 bg-white z-10">
          <tr>
            {columns.map(col => (
              <th key={String(col.key)} className="px-4 py-4 text-left font-bold text-primary-900 text-sm uppercase tracking-wider">
                {col.label}
              </th>
            ))}
            {actions && actions.length > 0 && (
              <th className="px-4 py-4 text-center font-bold text-primary-900 text-sm uppercase tracking-wider w-32">
                Acciones
              </th>
            )}
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td colSpan={columns.length + (actions?.length ? 1 : 0)} className="px-4 border-b border-neutral-200 text-neutral-700 text-center py-8">
                <div className="flex items-center justify-center gap-2">
                  <div className="w-5 h-5 border-2 border-primary-500 border-t-transparent rounded-full animate-spin"></div>
                  Cargando...
                </div>
              </td>
            </tr>
          ) : data.length === 0 ? (
            <tr>
              <td colSpan={columns.length + (actions?.length ? 1 : 0)} className="px-4 border-b border-neutral-200 text-neutral-700 text-center py-8">
                No hay datos
              </td>
            </tr>
          ) : (
            data?.map(item => (
              <tr 
                key={item.id} 
                onClick={() => onRowClick?.(item)} 
                className={`hover:bg-primary-50 transition-colors ${onRowClick ? 'cursor-pointer' : ''}`}
              >
                {columns.map(col => (
                  <td key={String(col.key)} className="px-4 py-4 border-b border-neutral-200 text-neutral-700">
                    {col.render ? col.render(item[col.key], item) : String(item[col.key] ?? '')}
                  </td>
                ))}
                {actions && actions.length > 0 && (
                  <td className="px-4 py-4 border-b border-neutral-200" onClick={(e) => e.stopPropagation()}>
                    <div className="flex items-center justify-center gap-1">
                      {actions.map((action, idx) => {
                        const Icon = iconMap[action.icon];
                        const variant = action.variant || (action.icon === 'delete' ? 'danger' : 'primary');
                        return (
                          <button
                            key={idx}
                            onClick={() => action.onClick(item)}
                            className={`p-2 rounded-lg transition-colors ${variantClasses[variant]}`}
                            title={action.label}
                          >
                            <Icon size={18} />
                          </button>
                        );
                      })}
                    </div>
                  </td>
                )}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
