import React, { useState, useMemo } from 'react';
import { 
  FiEdit2, FiTrash2, FiEye, FiChevronLeft, FiChevronRight, 
  FiChevronsLeft, FiChevronsRight, FiUserPlus, FiFilter,
  FiChevronUp, FiChevronDown
} from 'react-icons/fi';
import { HiOutlineViewGrid, HiOutlineViewList } from 'react-icons/hi';

// Tipos de ordenamiento
export type SortDirection = 'asc' | 'desc';
export type SortConfig = {
  field: string;
  direction: SortDirection;
};

interface TableColumn<T> {
  key: keyof T;
  label: string;
  render?: (value: T[keyof T], item: T) => React.ReactNode;
  hideOnMobile?: boolean;
  priority?: number; // 1-5, donde 1 es más importante
  sortable?: boolean; // Indica si la columna es ordenable
  sortKey?: string; // Campo a usar para el sort en backend (si difiere de key)
}

interface TableAction<T> {
  icon: 'edit' | 'delete' | 'view' | 'user';
  label: string;
  onClick: (item: T) => void;
  variant?: 'primary' | 'danger' | 'secondary';
  hidden?: (item: T) => boolean;
  hideOnMobile?: boolean;
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

// Ordenamiento manual (backend)
interface ManualSorting {
  mode: 'manual';
  sortConfig?: SortConfig;
  onSortChange: (sort: SortConfig | undefined) => void;
}

// Ordenamiento automático (frontend)
interface AutoSorting {
  mode: 'auto';
}

type SortingProps = ManualSorting | AutoSorting;

interface TableProps<T extends { id: string }> {
  data: T[];
  columns: TableColumn<T>[];
  onRowClick?: (item: T) => void;
  actions?: TableAction<T>[];
  loading?: boolean;
  pagination?: PaginationProps;
  sorting?: SortingProps;
  title?: string;
  searchable?: boolean;
  onSearch?: (query: string) => void;
  viewMode?: 'auto' | 'table' | 'cards';
  onViewModeChange?: (mode: 'table' | 'cards') => void;
}

const iconMap = {
  edit: FiEdit2,
  delete: FiTrash2,
  view: FiEye,
  user: FiUserPlus,
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
  sorting,
  title,
  searchable,
  onSearch,
  viewMode = 'auto',
  onViewModeChange,
}: TableProps<T>) {
  // Estado para paginación automática
  const [autoPage, setAutoPage] = useState(0);
  const [autoPageSize, setAutoPageSize] = useState(
    pagination?.mode === 'auto' ? (pagination.pageSize ?? DEFAULT_PAGE_SIZE) : DEFAULT_PAGE_SIZE
  );

  // Estado para ordenamiento automático
  const [autoSort, setAutoSort] = useState<SortConfig | undefined>(undefined);

  // Estado para vista móvil
  const [isMobileView, setIsMobileView] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Determinar vista basada en tamaño de pantalla y prop viewMode
  React.useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth < 768; // Tailwind md breakpoint
      if (viewMode === 'auto') {
        setIsMobileView(mobile);
      } else if (viewMode === 'cards') {
        setIsMobileView(true);
      } else {
        setIsMobileView(false);
      }
    };

    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [viewMode]);

  // Filtrar datos si hay búsqueda
  const filteredData = useMemo(() => {
    if (!searchQuery || !onSearch) return data;
    
    // Si hay onSearch, se delega al padre
    // Para búsqueda local simple:
    return data.filter(item => 
      Object.values(item).some(value => 
        String(value).toLowerCase().includes(searchQuery.toLowerCase())
      )
    );
  }, [data, searchQuery, onSearch]);

  // Obtener configuración de ordenamiento actual
  const currentSort = useMemo(() => {
    if (!sorting) return undefined;
    if (sorting.mode === 'manual') return sorting.sortConfig;
    return autoSort;
  }, [sorting, autoSort]);

  // Ordenar datos (solo para modo automático)
  const sortedData = useMemo(() => {
    if (!sorting || sorting.mode === 'manual' || !autoSort) {
      return filteredData;
    }
    
    // Ordenamiento local para modo automático
    const sorted = [...filteredData].sort((a, b) => {
      const aValue = a[autoSort.field as keyof T];
      const bValue = b[autoSort.field as keyof T];
      
      if (aValue === bValue) return 0;
      if (aValue === null || aValue === undefined) return 1;
      if (bValue === null || bValue === undefined) return -1;
      
      const comparison = String(aValue).localeCompare(String(bValue), undefined, { numeric: true });
      return autoSort.direction === 'asc' ? comparison : -comparison;
    });
    
    return sorted;
  }, [filteredData, sorting, autoSort]);

  // Calcular datos paginados
  const paginatedData = useMemo(() => {
    if (!pagination || pagination.mode === 'manual') {
      return sortedData;
    }
    const start = autoPage * autoPageSize;
    return sortedData.slice(start, start + autoPageSize);
  }, [sortedData, pagination, autoPage, autoPageSize]);

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

    const totalPages = Math.ceil(sortedData.length / autoPageSize);
    return {
      currentPage: autoPage,
      totalPages,
      totalElements: sortedData.length,
      pageSize: autoPageSize,
      pageSizeOptions: pagination.pageSizeOptions ?? DEFAULT_PAGE_SIZE_OPTIONS,
    };
  }, [pagination, sortedData.length, autoPage, autoPageSize]);

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
      setAutoPage(0);
    }
  };

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value;
    setSearchQuery(query);
    if (onSearch) {
      onSearch(query);
    }
  };

  // Manejar cambio de ordenamiento
  const handleSortChange = (field: string) => {
    if (!sorting) return;
    
    let newSort: SortConfig | undefined;
    
    if (currentSort?.field === field) {
      // Si ya está ordenado por este campo, cambiar dirección o quitar
      if (currentSort.direction === 'asc') {
        newSort = { field, direction: 'desc' };
      } else {
        newSort = undefined; // Quitar ordenamiento
      }
    } else {
      // Nuevo campo de ordenamiento
      newSort = { field, direction: 'asc' };
    }
    
    if (sorting.mode === 'manual') {
      sorting.onSortChange(newSort);
    } else {
      setAutoSort(newSort);
    }
  };

  // Renderizar icono de ordenamiento
  const renderSortIcon = (field: string) => {
    if (currentSort?.field !== field) {
      return (
        <span className="ml-1 opacity-30 group-hover:opacity-60">
          <FiChevronUp size={12} className="inline -mb-1" />
          <FiChevronDown size={12} className="inline -mt-1" />
        </span>
      );
    }
    
    return currentSort.direction === 'asc' ? (
      <FiChevronUp size={14} className="ml-1 inline text-primary-600" />
    ) : (
      <FiChevronDown size={14} className="ml-1 inline text-primary-600" />
    );
  };

  const displayData = pagination ? paginatedData : sortedData;

  // Renderizar tabla para desktop
  const renderDesktopTable = () => (
    <div className="overflow-x-auto overflow-y-auto max-h-[60vh] flex-1">
      <table className="w-full border-collapse min-w-max">
        <thead className="from-primary-50 to-primary-100 border-b-2 border-primary-300 sticky top-0 bg-white z-10">
          <tr>
            {columns.map(col => {
              const isSortable = col.sortable !== false && sorting;
              const sortField = col.sortKey ?? String(col.key);
              return (
                <th 
                  key={String(col.key)} 
                  className={`px-4 py-4 text-left font-bold text-primary-900 text-sm uppercase tracking-wider ${
                    isSortable ? 'cursor-pointer hover:bg-primary-50 select-none group transition-colors' : ''
                  }`}
                  onClick={() => isSortable && handleSortChange(sortField)}
                >
                  <span className="flex items-center">
                    {col.label}
                    {isSortable && renderSortIcon(sortField)}
                  </span>
                </th>
              );
            })}
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
              <td colSpan={columns.length + (actions?.length ? 1 : 0)} 
                className="px-4 border-b border-neutral-200 text-neutral-700 text-center py-12"
              >
                <div className="flex flex-col items-center justify-center gap-3">
                  <div className="w-10 h-10 border-3 border-primary-500 border-t-transparent rounded-full animate-spin"></div>
                  <span className="text-neutral-600">Cargando datos...</span>
                </div>
              </td>
            </tr>
          ) : displayData.length === 0 ? (
            <tr>
              <td colSpan={columns.length + (actions?.length ? 1 : 0)} 
                className="px-4 border-b border-neutral-200 text-neutral-700 text-center py-12"
              >
                <div className="flex flex-col items-center justify-center gap-2">
                  <div className="text-4xl mb-2">📊</div>
                  <p className="text-lg font-medium text-neutral-800">No hay datos disponibles</p>
                  <p className="text-neutral-600">Intenta ajustar los filtros o crear un nuevo registro</p>
                </div>
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
                        if (action.hidden && action.hidden(item)) return null;
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

  // Renderizar tarjetas para móvil
  const renderMobileCards = () => (
    <div className="overflow-y-auto max-h-[60vh] flex-1 p-2">
      {loading ? (
        <div className="flex flex-col items-center justify-center py-12">
          <div className="w-10 h-10 border-3 border-primary-500 border-t-transparent rounded-full animate-spin mb-3"></div>
          <span className="text-neutral-600">Cargando datos...</span>
        </div>
      ) : displayData.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-12 text-center">
          <div className="text-5xl mb-4">📱</div>
          <p className="text-lg font-medium text-neutral-800 mb-2">No hay datos</p>
          <p className="text-neutral-600">Intenta con otros filtros o crea un nuevo registro</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-3">
          {displayData.map(item => (
            <div 
              key={item.id}
              onClick={() => onRowClick?.(item)}
              className={`bg-white rounded-lg shadow-sm border border-neutral-200 p-4 transition-all hover:shadow-md ${
                onRowClick ? 'cursor-pointer active:scale-[0.99]' : ''
              }`}
            >
              {/* Información principal (2-3 columnas más importantes) */}
              <div className="mb-3">
                {columns
                  .filter(col => col.priority && col.priority <= 2)
                  .slice(0, 3)
                  .map(col => (
                    <div key={String(col.key)} className="mb-2">
                      <span className="text-xs font-semibold text-neutral-500 uppercase tracking-wider block">
                        {col.label}
                      </span>
                      <span className="text-sm font-medium text-neutral-800 block truncate">
                        {col.render ? col.render(item[col.key], item) : String(item[col.key] ?? '')}
                      </span>
                    </div>
                  ))}
              </div>

              {/* Información secundaria con botón de expansión */}
              {columns.length > 3 && (
                <details className="group">
                  <summary className="flex items-center justify-between text-primary-600 text-sm font-medium cursor-pointer list-none">
                    <span>Más detalles</span>
                    <svg className="w-4 h-4 transform group-open:rotate-180 transition-transform" 
                         fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                  </summary>
                  <div className="mt-3 pt-3 border-t border-neutral-100 space-y-2">
                    {columns
                      .filter(col => !col.priority || col.priority > 2)
                      .map(col => (
                        <div key={String(col.key)} className="flex justify-between items-start">
                          <span className="text-xs text-neutral-500 pr-2">{col.label}:</span>
                          <span className="text-sm text-neutral-700 text-right flex-1">
                            {col.render ? col.render(item[col.key], item) : String(item[col.key] ?? '')}
                          </span>
                        </div>
                      ))}
                  </div>
                </details>
              )}

              {/* Acciones en móvil */}
              {actions && actions.length > 0 && (
                <div className="mt-4 pt-4 border-t border-neutral-200 flex justify-between" onClick={(e) => e.stopPropagation()}>
                  <div className="flex gap-2">
                    {actions
                      .filter(action => !action.hidden?.(item))
                      .filter(action => !action.hideOnMobile)
                      .map((action, idx) => {
                        const Icon = iconMap[action.icon];
                        const variant = action.variant || (action.icon === 'delete' ? 'danger' : 'primary');
                        return (
                          <button
                            key={idx}
                            onClick={() => action.onClick(item)}
                            className={`p-2 rounded-lg ${variantClasses[variant]}`}
                            title={action.label}
                          >
                            <Icon size={18} />
                          </button>
                        );
                      })}
                  </div>
                  {actions.filter(action => action.hideOnMobile).length > 0 && (
                    <div className="relative group">
                      <button className="p-2 text-neutral-600 hover:text-neutral-800 hover:bg-neutral-100 rounded-lg">
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                                d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                        </svg>
                      </button>
                      <div className="absolute right-0 top-full mt-1 bg-white rounded-lg shadow-lg border border-neutral-200 py-2 z-20 hidden group-hover:block">
                        {actions
                          .filter(action => action.hideOnMobile && !action.hidden?.(item))
                          .map((action, idx) => {
                            const Icon = iconMap[action.icon];
                            return (
                              <button
                                key={idx}
                                onClick={() => action.onClick(item)}
                                className="flex items-center gap-2 px-4 py-2 text-sm text-neutral-700 hover:bg-neutral-100 w-full text-left"
                              >
                                <Icon size={16} />
                                {action.label}
                              </button>
                            );
                          })}
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );

  // Renderizar paginación responsive
  const renderPagination = () => {
    if (!pagination || !paginationInfo) return null;

    return (
      <div className="flex flex-col sm:flex-row items-center justify-between px-4 py-3 border-t border-neutral-200 bg-neutral-50 rounded-b-xl gap-4">
        {/* Información general - visible en todos los dispositivos */}
        <div className="flex items-center gap-2">

        <div className="text-sm text-neutral-600 mr-4">
          <span className="hidden sm:inline">
            Mostrando {displayData.length} de {paginationInfo.totalElements} registros
          </span>
          <span className="sm:hidden">
            {paginationInfo.currentPage + 1}/{paginationInfo.totalPages}
          </span>
        </div>

        {/* Selector de tamaño de página - oculto en móvil si hay muchas opciones */}
          <label className="hidden sm:block text-sm text-neutral-600">Por página:</label>
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

        {/* Navegación de páginas */}
        <div className="flex items-center gap-1">
          {/* Botones de navegación para móvil */}
          <div className="flex sm:hidden items-center gap-2">
            <button
              onClick={() => handlePageChange(paginationInfo.currentPage - 1)}
              disabled={paginationInfo.currentPage === 0}
              className="p-2 rounded-md border border-neutral-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-neutral-100 transition-colors"
              title="Anterior"
            >
              <FiChevronLeft size={16} />
            </button>
            
            <span className="text-sm font-medium min-w-[60px] text-center">
              Página {paginationInfo.currentPage + 1}
            </span>
            
            <button
              onClick={() => handlePageChange(paginationInfo.currentPage + 1)}
              disabled={paginationInfo.currentPage >= paginationInfo.totalPages - 1}
              className="p-2 rounded-md border border-neutral-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-neutral-100 transition-colors"
              title="Siguiente"
            >
              <FiChevronRight size={16} />
            </button>
          </div>

          {/* Navegación completa para desktop */}
          <div className="hidden sm:flex items-center gap-2">
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

            {/* Números de página - responsive */}
            <div className="flex gap-1">
              {Array.from({ length: Math.min(isMobileView ? 3 : 5, paginationInfo.totalPages) }, (_, i) => {
                let pageNum: number;
                if (paginationInfo.totalPages <= (isMobileView ? 3 : 5)) {
                  pageNum = i;
                } else if (paginationInfo.currentPage < 2) {
                  pageNum = i;
                } else if (paginationInfo.currentPage > paginationInfo.totalPages - 3) {
                  pageNum = paginationInfo.totalPages - (isMobileView ? 3 : 5) + i;
                } else {
                  pageNum = paginationInfo.currentPage - 1 + i;
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
              
              {paginationInfo.totalPages > (isMobileView ? 3 : 5) && paginationInfo.currentPage < paginationInfo.totalPages - 3 && (
                <span className="px-2 py-1 text-neutral-500">...</span>
              )}
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
      </div>
    );
  };

  return (
    <div className="bg-white rounded-xl shadow-md mb-8 border border-neutral-200 flex flex-col">
      {/* Header con título, búsqueda y controles */}
      {(title || searchable || onViewModeChange) && (
        <div className="px-4 py-3 border-b border-neutral-200 bg-neutral-50 flex flex-col sm:flex-row gap-3 sm:items-center justify-between">
          {title && (
            <div className="flex items-center gap-2">
              <h2 className="text-lg font-bold text-neutral-800">{title}</h2>
              <span className="bg-primary-100 text-primary-800 text-xs font-medium px-2 py-1 rounded-full">
                {paginationInfo?.totalElements || data.length}
              </span>
            </div>
          )}
          
          <div className="flex items-center gap-2 flex-1 sm:justify-end">
            {searchable && (
              <div className="relative flex-1 sm:flex-none sm:w-64">
                <FiFilter className="absolute left-3 top-1/2 transform -translate-y-1/2 text-neutral-400" size={16} />
                <input
                  type="text"
                  placeholder="Buscar..."
                  value={searchQuery}
                  onChange={handleSearch}
                  className="w-full pl-10 pr-4 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 text-sm"
                />
              </div>
            )}
            
            {onViewModeChange && (
              <div className="hidden sm:flex border border-neutral-300 rounded-lg overflow-hidden">
                <button
                  onClick={() => onViewModeChange('table')}
                  className={`p-2 ${!isMobileView ? 'bg-primary-100 text-primary-700' : 'bg-white text-neutral-600'}`}
                  title="Vista de tabla"
                >
                  <HiOutlineViewList size={18} />
                </button>
                <button
                  onClick={() => onViewModeChange('cards')}
                  className={`p-2 ${isMobileView ? 'bg-primary-100 text-primary-700' : 'bg-white text-neutral-600'}`}
                  title="Vista de tarjetas"
                >
                  <HiOutlineViewGrid size={18} />
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Vista principal (tabla o tarjetas) */}
      {isMobileView ? renderMobileCards() : renderDesktopTable()}

      {/* Paginación */}
      {renderPagination()}

      {/* Indicador de vista móvil */}
      {isMobileView && displayData.length > 0 && (
        <div className="sm:hidden px-4 py-2 border-t border-neutral-200 text-center">
          <p className="text-xs text-neutral-500">
            Desliza horizontalmente para ver más información en las tarjetas
          </p>
        </div>
      )}
    </div>
  );
}