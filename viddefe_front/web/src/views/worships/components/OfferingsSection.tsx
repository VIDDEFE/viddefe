import { memo, useMemo, useState } from 'react';
import { Card, Button, Table, Avatar } from '../../../components/shared';
import { FiDollarSign, FiPlus, FiEdit2, FiTrash2, FiPieChart, FiBarChart2 } from 'react-icons/fi';
import { 
  PieChart, 
  Pie, 
  Cell, 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  Tooltip, 
  ResponsiveContainer 
} from 'recharts';
import { formatCurrency } from './helpers';
import type { OfferingsSectionProps, OfferingTableItem } from './types';

// Colores para los gráficos
const CHART_COLORS = [
  '#10b981', // emerald-500
  '#3b82f6', // blue-500
  '#8b5cf6', // violet-500
  '#f59e0b', // amber-500
  '#ef4444', // red-500
  '#06b6d4', // cyan-500
  '#ec4899', // pink-500
  '#84cc16', // lime-500
];

function OfferingsSection({
  offeringTableData,
  offeringTotalElements,
  offeringTotalPages,
  totalOfferingsAmount,
  analytics,
  isLoading,
  viewMode,
  onViewModeChange,
  currentPage,
  pageSize,
  onPageChange,
  onPageSizeChange,
  onAddOffering,
  onEditOffering,
  onDeleteOffering,
}: Readonly<OfferingsSectionProps>) {
  
  const [chartType, setChartType] = useState<'pie' | 'bar'>('pie');

  // Preparar datos para los gráficos
  const chartData = useMemo(() => {
    if (!analytics || analytics.length === 0) return [];
    return analytics.map((item, index) => ({
      name: item.name,
      value: item.amount,
      count: item.count,
      color: CHART_COLORS[index % CHART_COLORS.length],
    }));
  }, [analytics]);

  const columns = useMemo(() => [
    {
      key: 'personName' as const,
      label: 'Persona',
      priority: 1,
      render: (_value: OfferingTableItem[keyof OfferingTableItem], item: OfferingTableItem) => (
        <div className="flex items-center gap-3">
          <Avatar
            src={item.avatar}
            name={item.personName}
            size="sm"
          />
          <p className="font-medium text-neutral-800">{item.personName}</p>
        </div>
      ),
    },
    {
      key: 'typeName' as const,
      label: 'Tipo',
      priority: 3,
      render: (value: OfferingTableItem[keyof OfferingTableItem]) => {
        const strValue = String(value ?? '-');
        if (strValue === '-') {
          return <span className="text-neutral-400">-</span>;
        }
        return (
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-700">
            {strValue}
          </span>
        );
      },
    },
    {
      key: 'amount' as const,
      label: 'Monto',
      priority: 2,
      render: (value: OfferingTableItem[keyof OfferingTableItem]) => (
        <span className="font-semibold text-green-600">
          ${typeof value === 'number' ? formatCurrency(value) : '0.00'}
        </span>
      ),
    },
    {
      key: 'id' as const,
      label: 'Acciones',
      priority: 4,
      render: (_value: OfferingTableItem[keyof OfferingTableItem], item: OfferingTableItem) => (
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => onEditOffering(item)}
            className="p-1.5 text-neutral-500 hover:text-primary-600 hover:bg-primary-50 rounded transition-colors"
            title="Editar"
          >
            <FiEdit2 size={14} />
          </button>
          <button
            type="button"
            onClick={() => onDeleteOffering(item.id)}
            className="p-1.5 text-neutral-500 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
            title="Eliminar"
          >
            <FiTrash2 size={14} />
          </button>
        </div>
      ),
    },
  ], [onEditOffering, onDeleteOffering]);

  return (
    <Card>
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-4">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-green-100 rounded-lg">
            <FiDollarSign className="text-green-600" size={24} />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-neutral-800">
              Ofrendas
            </h3>
            <p className="text-sm text-neutral-500">
              {offeringTotalElements} registros · Total:{' '}
              <span className="font-semibold text-green-600">
                ${formatCurrency(totalOfferingsAmount)}
              </span>
            </p>
          </div>
        </div>
        <Button onClick={onAddOffering}>
          <span className="flex items-center gap-2">
            <FiPlus size={16} />
            Nueva Ofrenda
          </span>
        </Button>
      </div>

      {/* Analytics Section with Charts */}
      {analytics && analytics.length > 0 && (
        <div className="mb-6 p-4 bg-linear-to-r from-green-50 to-emerald-50 rounded-xl border border-green-100">
          {/* Header con toggle de tipo de gráfico */}
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <FiPieChart className="text-green-600" size={18} />
              <h4 className="font-medium text-neutral-700">Resumen por Tipo</h4>
            </div>
            <div className="flex items-center gap-1 bg-white rounded-lg p-1 shadow-sm border border-green-100">
              <button
                type="button"
                onClick={() => setChartType('pie')}
                className={`p-1.5 rounded transition-colors ${
                  chartType === 'pie' 
                    ? 'bg-green-100 text-green-700' 
                    : 'text-neutral-500 hover:text-green-600'
                }`}
                title="Gráfico circular"
              >
                <FiPieChart size={16} />
              </button>
              <button
                type="button"
                onClick={() => setChartType('bar')}
                className={`p-1.5 rounded transition-colors ${
                  chartType === 'bar' 
                    ? 'bg-green-100 text-green-700' 
                    : 'text-neutral-500 hover:text-green-600'
                }`}
                title="Gráfico de barras"
              >
                <FiBarChart2 size={16} />
              </button>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Gráfico */}
            <div className="bg-white rounded-lg p-4 shadow-sm border border-green-100">
              <ResponsiveContainer width="100%" height={250}>
                {chartType === 'pie' ? (
                  <PieChart>
                    <Pie
                      data={chartData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={90}
                      paddingAngle={2}
                      dataKey="value"
                      label={({ name, percent }) => `${name} (${((percent ?? 0) * 100).toFixed(0)}%)`}
                      labelLine={false}
                    >
                      {chartData.map((entry) => (
                        <Cell key={`pie-${entry.name}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip 
                      formatter={(value) => [`$${formatCurrency(Number(value) || 0)}`, 'Monto']}
                      contentStyle={{ 
                        borderRadius: '8px', 
                        border: '1px solid #e5e7eb',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
                      }}
                    />
                  </PieChart>
                ) : (
                  <BarChart data={chartData} layout="vertical">
                    <XAxis type="number" tickFormatter={(value) => `$${formatCurrency(value)}`} />
                    <YAxis 
                      type="category" 
                      dataKey="name" 
                      width={100}
                      tick={{ fontSize: 12 }}
                    />
                    <Tooltip 
                      formatter={(value) => [`$${formatCurrency(Number(value) || 0)}`, 'Monto']}
                      contentStyle={{ 
                        borderRadius: '8px', 
                        border: '1px solid #e5e7eb',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
                      }}
                    />
                    <Bar dataKey="value" radius={[0, 4, 4, 0]}>
                      {chartData.map((entry) => (
                        <Cell key={`bar-${entry.name}`} fill={entry.color} />
                      ))}
                    </Bar>
                  </BarChart>
                )}
              </ResponsiveContainer>
            </div>

            {/* Cards de resumen */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 content-start">
              {analytics.map((item, index) => (
                <div
                  key={item.code}
                  className="bg-white rounded-lg p-3 shadow-sm border border-green-100 hover:shadow-md transition-shadow"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-2">
                      <div 
                        className="w-3 h-3 rounded-full shrink-0" 
                        style={{ backgroundColor: CHART_COLORS[index % CHART_COLORS.length] }}
                      />
                      <div className="flex-1 min-w-0">
                        <p className="text-xs font-medium text-neutral-500 uppercase tracking-wide truncate">
                          {item.name}
                        </p>
                        <p className="text-lg font-bold text-green-600 mt-0.5">
                          ${formatCurrency(item.amount)}
                        </p>
                      </div>
                    </div>
                    <span className="shrink-0 ml-2 inline-flex items-center justify-center w-6 h-6 rounded-full bg-green-100 text-green-700 text-xs font-semibold">
                      {item.count}
                    </span>
                  </div>
                </div>
              ))}
              
              {/* Total card */}
              <div className="bg-green-600 rounded-lg p-3 shadow-sm sm:col-span-2">
                <div className="flex items-center justify-between text-white">
                  <div>
                    <p className="text-xs font-medium text-green-100 uppercase tracking-wide">
                      Total General
                    </p>
                    <p className="text-2xl font-bold mt-0.5">
                      ${formatCurrency(totalOfferingsAmount)}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-green-100">Registros</p>
                    <p className="text-xl font-semibold">{offeringTotalElements}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
      
      <Table<OfferingTableItem>
        data={offeringTableData}
        columns={columns}
        loading={isLoading}
        viewMode={viewMode}
        onViewModeChange={onViewModeChange}
        pagination={{
          mode: 'manual',
          currentPage,
          totalPages: offeringTotalPages,
          totalElements: offeringTotalElements,
          pageSize,
          onPageChange,
          onPageSizeChange,
        }}
      />
    </Card>
  );
}

export default memo(OfferingsSection);
