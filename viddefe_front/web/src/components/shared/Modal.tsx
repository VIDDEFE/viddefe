interface ModalProps {
  isOpen: boolean;
  title: string;
  children: React.ReactNode;
  onClose: () => void;
  actions?: React.ReactNode;
}

export default function Modal({
  isOpen,
  title,
  children,
  onClose,
  actions,
}: ModalProps) {
  if (!isOpen) return null;

  return (
    <dialog
      open
      className="
        fixed inset-0 z-50 flex items-center justify-center
        bg-black/40 backdrop-blur-sm px-4
        animate-fadeIn w-full h-screen
      "
      onClick={onClose}
    >
      {/* Contenido del modal */}
      <div
        className="
          container mx-auto px-4
          max-w-2xl max-h-[85vh]
          bg-white rounded-2xl shadow-2xl border border-neutral-300
          flex flex-col
          animate-scaleIn
        "
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-6 py-5 border-b border-neutral-200 flex justify-between items-center">
          <h2 className="text-2xl font-bold text-primary-900">{title}</h2>

          <button
            className="
              text-neutral-500 text-3xl w-8 h-8 flex items-center justify-center
              hover:text-primary-600 transition-colors duration-200
            "
            onClick={onClose}
          >
            ×
          </button>
        </div>

        {/* Body con scroll */}
        <div className="px-6 py-6 overflow-y-auto">
          {children}
        </div>

        {/* Actions */}
        {actions && (
          <div className="px-6 py-4 border-t border-neutral-200 bg-neutral-50 flex gap-3 justify-end">
            {actions}
          </div>
        )}
      </div>
    </dialog>
  );
}
