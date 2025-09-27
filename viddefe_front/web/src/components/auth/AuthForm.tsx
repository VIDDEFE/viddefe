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
        <form onSubmit={onSubmit} className="auth-form">
            <h2 className="auth-form__title">{title}</h2>
            <div className="auth-form__fields">{children}</div>
            <button
                type="submit"
                className="auth-form__submit"
                disabled={isSubmitting}
            >
                {isSubmitting ? 'Loading...' : submitLabel}
            </button>
            {footer && <div className="auth-form__footer">{footer}</div>}
        </form>
    );
};

export default AuthForm;