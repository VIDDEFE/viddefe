import React from 'react';

interface CardProps {
  children: React.ReactNode;
  className?: string;
  onClick?: () => void;
}

export default function Card({ children, className = '', onClick }: CardProps) {
  return (
    <div 
      className={`bg-white rounded-xl p-6 shadow-md transition-all duration-300 hover:shadow-lg hover:translate-y-[-2px] border border-neutral-200 ${className}`}
      onClick={onClick}
    >
      {children}
    </div>
  );
}
