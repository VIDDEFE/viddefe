import React from 'react';

interface FormProps extends React.FormHTMLAttributes<HTMLFormElement> {
  children: React.ReactNode;
}

export function Form({ children, className = '', ...props }: FormProps) {
  return (
    <form className={`flex flex-col gap-6 ${className}`} {...props}>
      {children}
    </form>
  );
}

interface FormGroupProps {
  label?: string;
  error?: string;
  children: React.ReactNode;
  className?: string;
}

export function FormGroup({ label, error, children, className = '' }: FormGroupProps) {
  return (
    <div className={`flex flex-col ${className}`}>
      {label && <label className="font-semibold text-primary-900 mb-2 text-base">{label}</label>}
      {children}
      {error && <span className="text-red-600 text-sm mt-1">{error}</span>}
    </div>
  );
}

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export function Input({ label, error, className = '', ...props }: InputProps) {
  return (
    <FormGroup label={label} error={error}>
      <input 
        className={`px-3 py-3 border-2 border-neutral-200 rounded-lg text-base transition-all duration-300 font-inherit focus:outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-300 focus:ring-opacity-50 placeholder-neutral-400 ${className}`} 
        {...props} 
      />
    </FormGroup>
  );
}

interface TextAreaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
}

export function TextArea({ label, error, className = '', ...props }: TextAreaProps) {
  return (
    <FormGroup label={label} error={error}>
      <textarea 
        className={`px-3 py-3 border-2 border-neutral-200 rounded-lg text-base transition-all duration-300 font-inherit focus:outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-300 focus:ring-opacity-50 placeholder-neutral-400 resize-vertical min-h-32 ${className}`} 
        {...props} 
      />
    </FormGroup>
  );
}
