import { Card } from '../shared';

interface MetricItem {
  label: string;
  value: string | number;
  colorClass?: string;
}

interface MetricCardProps {
  title: string;
  metrics: MetricItem[];
  className?: string;
}

export default function MetricCard({
  title,
  metrics,
  className = ''
}: MetricCardProps) {
  return (
    <Card className={`p-5 sm:p-6 ${className}`}>
      <h3 className="text-lg sm:text-xl font-semibold text-primary-900 mb-4">
        {title}
      </h3>
      <div className="space-y-4">
        {metrics.map((metric, index) => (
          <div
            key={index}
            className={`flex justify-between items-center ${
              index < metrics.length - 1 ? 'border-b border-primary-100 pb-3' : ''
            }`}
          >
            <span className="text-sm sm:text-base text-primary-700">
              {metric.label}
            </span>
            <span className={`text-lg sm:text-xl font-bold ${
              metric.colorClass || 'text-primary-800'
            }`}>
              {metric.value}
            </span>
          </div>
        ))}
      </div>
    </Card>
  );
}
