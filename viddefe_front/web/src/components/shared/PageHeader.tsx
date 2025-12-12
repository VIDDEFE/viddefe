import React from 'react';

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  actions?: React.ReactNode;
}

export default function PageHeader({ title, subtitle, actions }: PageHeaderProps) {
  return (
    <div className="flex justify-between items-start mb-8 bg-white p-8 rounded-xl shadow-md border-b-4 border-primary-500">
      <div>
        <h1 className="m-0 text-4xl font-bold text-primary-900">{title}</h1>
        {subtitle && <p className="mt-2 text-neutral-500 text-base">{subtitle}</p>}
      </div>
      {actions && <div className="flex gap-3">{actions}</div>}
    </div>
  );
}
