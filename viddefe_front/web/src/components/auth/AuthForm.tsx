import React from 'react';

export interface AuthFormProps {
    title: string;
    onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
    children: React.ReactNode;
    submitLabel: string;
    isSubmitting?: boolean;
    footer?: React.ReactNode;
}

const AuthForm: React.FC<AuthFormProps> = ({
    title,
    onSubmit,
    children,
    submitLabel,
    isSubmitting = false,
    footer,
}) => {
    return (
        <form onSubmit={onSubmit} className="form">
            <h2 className="text-2xl font-bold text-dark-900 mb-4">{title}</h2>
            <div className="space-y-4">{children}</div>
            <button
                type="submit"
                className="button button--primary w-full mt-2"
                disabled={isSubmitting}
            >
                {isSubmitting ? 'Loading...' : submitLabel}
            </button>
            {footer && <div className="mt-6 pt-6 border-t border-gray-200">{footer}</div>}
        </form>
    );
};

export default AuthForm;