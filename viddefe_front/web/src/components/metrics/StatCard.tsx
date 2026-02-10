import { ReactNode } from 'react';
import { Card } from '../shared';

interface StatCardProps {
  icon: ReactNode;
  title: string;
  value: number | string;
  bgColor?: string;
  className?: string;
}

export default function StatCard({
  icon,
  title,
  value,
  bgColor = 'bg-blue-500',
  className = ''
}: StatCardProps) {
  return (
    <Card className={`flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-neutral-100 ${className}`}>
      <div className={`text-2xl sm:text-3xl p-3 rounded-lg text-white ${bgColor}`}>
        {icon}
      </div>
      <h3 className="text-base sm:text-lg font-semibold text-neutral-900">
        {title}
      </h3>
      <p className="text-2xl sm:text-3xl font-bold text-neutral-800">
        {value}
      </p>
    </Card>
  );
}
