import React, { useState, useMemo } from 'react';
import { FiEdit2, FiTrash2, FiEye, FiChevronLeft, FiChevronRight, FiChevronsLeft, FiChevronsRight } from 'react-icons/fi';

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

// Paginación manual (backend)
interface ManualPagination {
  mode: 'manual';
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
}

// Paginación automática (frontend)
interface AutoPagination {
  mode: 'auto';
  pageSize?: number;
  pageSizeOptions?: number[];
}

type PaginationProps = ManualPagination | AutoPagination;

interface TableProps<T extends { id: string }> {
  data: T[];
  columns: TableColumn<T>[];
  onRowClick?: (item: T) => void;
  actions?: TableAction<T>[];
  loading?: boolean;
  pagination?: PaginationProps;
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

const DEFAULT_PAGE_SIZE = 10;
const DEFAULT_PAGE_SIZE_OPTIONS = [5, 10, 20, 50];

export default function Table<T extends { id: string }>({
  data,
  columns,
  onRowClick,
  actions,
  loading = false,
  pagination,
}: TableProps<T>) {
  // Estado para paginación automática
  const [autoPage, setAutoPage] = useState(0);
  const [autoPageSize, setAutoPageSize] = useState(
    pagination?.mode === 'auto' ? (pagination.pageSize ?? DEFAULT_PAGE_SIZE) : DEFAULT_PAGE_SIZE
  );

  // Calcular datos paginados para modo automático
  const paginatedData = useMemo(() => {
    if (!pagination || pagination.mode === 'manual') {
      return data;
    }
    const start = autoPage * autoPageSize;
    return data.slice(start, start + autoPageSize);
  }, [data, pagination, autoPage, autoPageSize]);

  // Calcular info de paginación
  const paginationInfo = useMemo(() => {
    if (!pagination) return null;

    if (pagination.mode === 'manual') {
      return {
        currentPage: pagination.currentPage,
        totalPages: pagination.totalPages,
        totalElements: pagination.totalElements,
        pageSize: pagination.pageSize,
        pageSizeOptions: DEFAULT_PAGE_SIZE_OPTIONS,
      };
    }

    const totalPages = Math.ceil(data.length / autoPageSize);
    return {
      currentPage: autoPage,
      totalPages,
      totalElements: data.length,
      pageSize: autoPageSize,
      pageSizeOptions: pagination.pageSizeOptions ?? DEFAULT_PAGE_SIZE_OPTIONS,
    };
  }, [pagination, data.length, autoPage, autoPageSize]);

  const handlePageChange = (page: number) => {
    if (!pagination) return;
    if (pagination.mode === 'manual') {
      pagination.onPageChange(page);
    } else {
      setAutoPage(page);
    }
  };

  const handlePageSizeChange = (size: number) => {
    if (!pagination) return;
    if (pagination.mode === 'manual' && pagination.onPageSizeChange) {
      pagination.onPageSizeChange(size);
    } else if (pagination.mode === 'auto') {
      setAutoPageSize(size);
      setAutoPage(0); // Reset a primera página
    }
  };

  const displayData = pagination ? paginatedData : data;

  return (
    <div className="bg-white rounded-xl shadow-md mb-8 border border-neutral-200 flex flex-col">
      <div className="overflow-x-auto overflow-y-auto max-h-[60vh] flex-1">
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
            ) : displayData.length === 0 ? (
              <tr>
                <td colSpan={columns.length + (actions?.length ? 1 : 0)} className="px-4 border-b border-neutral-200 text-neutral-700 text-center py-8">
                  No hay datos
                </td>
              </tr>
            ) : (
              displayData.map(item => (
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

      {/* Paginación */}
      {pagination && paginationInfo && (
        <div className="flex items-center justify-between px-4 py-3 border-t border-neutral-200 bg-neutral-50 rounded-b-xl">
          <div className="flex items-center gap-4">
            <span className="text-sm text-neutral-600">
              Mostrando {displayData.length} de {paginationInfo.totalElements} registros
            </span>
            <div className="flex items-center gap-2">
              <label className="text-sm text-neutral-600">Por página:</label>
              <select
                value={paginationInfo.pageSize}
                onChange={(e) => handlePageSizeChange(Number(e.target.value))}
                className="border border-neutral-300 rounded-md px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                {paginationInfo.pageSizeOptions.map(size => (
                  <option key={size} value={size}>{size}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-sm text-neutral-600 mr-2">
              Página {paginationInfo.currentPage + 1} de {paginationInfo.totalPages || 1}
            </span>
            
            <button
              onClick={() => handlePageChange(0)}
              disabled={paginationInfo.currentPage === 0}
              className="p-1.5 rounded-md border border-neutral-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-neutral-100 transition-colors"
              title="Primera página"
            >
              <FiChevronsLeft size={16} />
            </button>
            
            <button
              onClick={() => handlePageChange(paginationInfo.currentPage - 1)}
              disabled={paginationInfo.currentPage === 0}
              className="p-1.5 rounded-md border border-neutral-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-neutral-100 transition-colors"
              title="Página anterior"
            >
              <FiChevronLeft size={16} />
            </button>

            {/* Números de página */}
            <div className="flex gap-1">
              {Array.from({ length: Math.min(5, paginationInfo.totalPages) }, (_, i) => {
                let pageNum: number;
                if (paginationInfo.totalPages <= 5) {
                  pageNum = i;
                } else if (paginationInfo.currentPage < 3) {
                  pageNum = i;
                } else if (paginationInfo.currentPage > paginationInfo.totalPages - 4) {
                  pageNum = paginationInfo.totalPages - 5 + i;
                } else {
                  pageNum = paginationInfo.currentPage - 2 + i;
                }
                
                if (pageNum < 0 || pageNum >= paginationInfo.totalPages) return null;
                
                return (
                  <button
                    key={pageNum}
                    onClick={() => handlePageChange(pageNum)}
                    className={`w-8 h-8 rounded-md text-sm font-medium transition-colors ${
                      pageNum === paginationInfo.currentPage
                        ? 'bg-primary-600 text-white'
                        : 'border border-neutral-300 hover:bg-neutral-100'
                    }`}
                  >
                    {pageNum + 1}
                  </button>
                );
              })}
            </div>

            <button
              onClick={() => handlePageChange(paginationInfo.currentPage + 1)}
              disabled={paginationInfo.currentPage >= paginationInfo.totalPages - 1}
              className="p-1.5 rounded-md border border-neutral-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-neutral-100 transition-colors"
              title="Página siguiente"
            >
              <FiChevronRight size={16} />
            </button>
            
            <button
              onClick={() => handlePageChange(paginationInfo.totalPages - 1)}
              disabled={paginationInfo.currentPage >= paginationInfo.totalPages - 1}
              className="p-1.5 rounded-md border border-neutral-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-neutral-100 transition-colors"
              title="Última página"
            >
              <FiChevronsRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
