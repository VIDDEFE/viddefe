import React from 'react';

interface TableColumn<T> {
  key: keyof T;
  label: string;
  render?: (value: T[keyof T], item: T) => React.ReactNode;
}

interface TableProps<T extends { id: string }> {
  data: T[];
  columns: TableColumn<T>[];
  onRowClick?: (item: T) => void;
  actions?: React.ReactNode;
}

export default function Table<T extends { id: string }>({
  data,
  columns,
  onRowClick,
  actions,
}: TableProps<T>) {
  return (
    <div className="bg-white rounded-xl shadow-md mb-8 border border-neutral-200 overflow-x-auto overflow-y-auto max-h-[70vh]">
      <table className="w-full border-collapse min-w-max">
        <thead className="from-primary-50 to-primary-100 border-b-2 border-primary-300">
          <tr>
            {columns.map(col => (
              <th key={String(col.key)} className="px-4 py-4 text-left font-bold text-primary-900 text-sm uppercase tracking-wider">
                {col.label}
              </th>
            ))}
            {actions && <th className="px-4 py-4 text-left font-bold text-primary-900 text-sm uppercase tracking-wider">Acciones</th>}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan={columns.length + (actions ? 1 : 0)} className="px-4 border-b border-neutral-200 text-neutral-700 text-center py-8">
                No hay datos
              </td>
            </tr>
          ) : (
            data?.map(item => (
              <tr key={item.id} onClick={() => onRowClick?.(item)} className="hover:bg-primary-50 cursor-pointer transition-colors">
                {columns.map(col => (
                  <td key={String(col.key)} className="px-4 py-4 border-b border-neutral-200 text-neutral-700">
                    {col.render ? col.render(item[col.key], item) : String(item[col.key])}
                  </td>
                ))}
                {actions && <td className="px-4 py-4 border-b border-neutral-200 text-neutral-700">{actions}</td>}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
