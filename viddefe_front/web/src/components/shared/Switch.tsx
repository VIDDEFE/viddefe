interface SwitchProps {
  checked: boolean;
  onChange: (checked: boolean) => void;
  disabled?: boolean;
  loading?: boolean;
  size?: 'sm' | 'md' | 'lg';
  label?: string;
  labelPosition?: 'left' | 'right';
  className?: string;
}

export default function Switch({
  checked,
  onChange,
  disabled = false,
  loading = false,
  size = 'md',
  label,
  labelPosition = 'right',
  className = '',
}: SwitchProps) {
  const sizeClasses = {
    sm: {
      track: 'w-8 h-4',
      thumb: 'w-3 h-3',
      translate: 'translate-x-4',
    },
    md: {
      track: 'w-11 h-6',
      thumb: 'w-5 h-5',
      translate: 'translate-x-5',
    },
    lg: {
      track: 'w-14 h-7',
      thumb: 'w-6 h-6',
      translate: 'translate-x-7',
    },
  };

  const sizes = sizeClasses[size];

  const handleClick = () => {
    if (!disabled && !loading) {
      onChange(!checked);
    }
  };

  const switchElement = (
    <button
      type="button"
      role="switch"
      aria-checked={checked}
      disabled={disabled || loading}
      onClick={handleClick}
      className={`
        relative inline-flex items-center shrink-0 cursor-pointer rounded-full
        transition-colors duration-200 ease-in-out
        focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2
        ${sizes.track}
        ${checked ? 'bg-primary-600' : 'bg-neutral-300'}
        ${disabled ? 'opacity-50 cursor-not-allowed' : ''}
        ${loading ? 'cursor-wait' : ''}
      `}
    >
      <span
        className={`
          pointer-events-none inline-block rounded-full bg-white shadow-lg
          transform transition-transform duration-200 ease-in-out
          ${sizes.thumb}
          ${checked ? sizes.translate : 'translate-x-0.5'}
          ${loading ? 'animate-pulse' : ''}
        `}
      >
        {loading && (
          <span className="absolute inset-0 flex items-center justify-center">
            <svg
              className="animate-spin h-3 w-3 text-primary-600"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
              />
              <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
          </span>
        )}
      </span>
    </button>
  );

  if (label) {
    return (
      <label
        className={`
          inline-flex items-center gap-2 cursor-pointer
          ${disabled ? 'cursor-not-allowed opacity-50' : ''}
          ${className}
        `}
      >
        {labelPosition === 'left' && (
          <span className="text-sm text-neutral-700">{label}</span>
        )}
        {switchElement}
        {labelPosition === 'right' && (
          <span className="text-sm text-neutral-700">{label}</span>
        )}
      </label>
    );
  }

  return <span className={className}>{switchElement}</span>;
}
