import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'success';
  size?: 'sm' | 'md' | 'lg';
  children: React.ReactNode;
}

export default function Button({
  variant = 'primary',
  size = 'md',
  children,
  className = '',
  ...props
}: ButtonProps) {
  const baseClasses = 'px-5 py-2 border-0 rounded-lg text-base font-semibold cursor-pointer transition-all duration-300 whitespace-nowrap focus:outline-none focus:ring-2';

  const variantClasses = {
    primary: 'bg-primary-500  text-white hover:shadow-lg hover:translate-y-[-2px] hover:from-primary-600 hover:to-primary-700 focus:ring-primary-300',
    secondary: 'bg-secondary-200 text-secondary-900 hover:bg-secondary-300 font-semibold focus:ring-secondary-300',
    danger: 'bg-red-500 text-white hover:bg-red-600 focus:ring-red-300',
    success: 'bg-emerald-500 text-white hover:bg-emerald-600 focus:ring-emerald-300',
  };

  const sizeClasses = {
    sm: 'px-3 py-2 text-sm',
    md: '',
    lg: 'px-7 py-4 text-lg',
  };

  return (
    <button
      className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
