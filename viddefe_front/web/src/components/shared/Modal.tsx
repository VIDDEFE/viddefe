import { createPortal } from 'react-dom';

interface ModalProps {
  readonly isOpen: boolean;
  readonly title: string;
  readonly children: React.ReactNode;
  readonly onClose: () => void;
  readonly actions?: React.ReactNode;
}

export default function Modal({
  isOpen,
  title,
  children,
  onClose,
  actions,
}: ModalProps) {
  if (!isOpen) return null;

  return createPortal(
    <div
      className="
        fixed inset-0 z-[999999]
        flex items-center justify-center
        bg-black/40 backdrop-blur-sm
        px-4
      "
      onClick={onClose}
      onKeyDown={(e) => {
        if (e.key === 'Escape') onClose();
      }}
      tabIndex={-1}
    >
      <div
        className="
          w-full max-w-2xl min-h-80 max-h-[90vh]
          bg-white rounded-2xl
          shadow-2xl
          flex flex-col overflow-hidden
        "
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-6 py-5 flex justify-between items-center">
          <h2 id="modal-title" className="text-2xl font-bold text-primary-900">
            {title}
          </h2>

          <button
            className="
              text-neutral-500 text-3xl w-8 h-8 flex items-center justify-center
              hover:text-primary-600 transition-colors
            "
            onClick={onClose}
            aria-label="Close modal"
          >
            ×
          </button>
        </div>

        {/* Body */}
        <div className="px-6 py-6 overflow-y-auto flex-1">
          {children}
        </div>

        {/* Actions */}
        {actions && (
          <div className="px-6 py-4 flex justify-end gap-3">
            {actions}
          </div>
        )}
      </div>
    </div>,
    document.body
  );
}
