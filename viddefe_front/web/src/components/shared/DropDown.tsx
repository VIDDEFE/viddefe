import { useEffect, useMemo, useRef, useState } from "react";
import { FormGroup } from "./Form";

type AnyObject = Record<string, any>;

interface DropDownProps {
  label?: string;
  error?: string;
  options?: AnyObject[];
  value?: string;
  onChangeValue?: (value: string) => void;
  placeholder?: string;
  labelKey?: string;
  valueKey?: string;
  searchKey?: string;
  className?: string;
}

export default function DropDown({
  label,
  error,
  options = [],
  value,
  onChangeValue,
  placeholder = "Seleccionar...",
  labelKey = "label",
  valueKey = "value",
  searchKey,
  className = "",
}: DropDownProps) {
  const searchField = searchKey ?? labelKey;

  const [open, setOpen] = useState(false);
  const [q, setQ] = useState("");

  const wrapperRef = useRef<HTMLDivElement>(null);

  // Filtrar opciones por búsqueda
  const filtered = useMemo(() => {
    if (!q.trim()) return options;
    const term = q.toLowerCase();
    return options.filter((opt) =>
      String(opt[searchField] ?? "").toLowerCase().includes(term)
    );
  }, [options, q, searchField]);

  // Cerrar al hacer click afuera
  useEffect(() => {
    function handler(e: MouseEvent) {
      if (!wrapperRef.current?.contains(e.target as Node)) {
        setOpen(false);
        setQ("");
      }
    }
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const currentLabel =
    options.find((o) => String(o[valueKey]) === String(value))?.[labelKey] ??
    placeholder;

  return (
    <FormGroup label={label} error={error}>
      <div ref={wrapperRef} className="relative w-full">
        {/* Trigger */}
        <button
          type="button"
          onClick={() => setOpen((o) => !o)}
          className={`px-3 py-3 w-full text-left border-2 border-neutral-200 rounded-lg 
            text-base transition-all duration-300 font-inherit focus:outline-none
            focus:border-primary-500 focus:ring-2 focus:ring-primary-300 cursor-pointer bg-white
            ${className}`}
        >
          {currentLabel}
        </button>

        {/* Dropdown panel */}
        {open && (
          <div
            className="absolute top-full left-0 right-0 mt-1 bg-white border border-neutral-200 
            rounded-lg shadow-lg z-[9999] overflow-hidden"
          >
            {/* Search box */}
            <input
              autoFocus
              value={q}
              onChange={(e) => setQ(e.target.value)}
              placeholder="Buscar..."
              className="px-3 py-2 w-full border-b border-neutral-200 text-base focus:outline-none"
            />

            {/* Options list */}
            <div className="max-h-60 overflow-auto custom-scrollbar">
              {filtered.length === 0 && (
                <div className="px-3 py-3 text-neutral-500 text-sm">No results</div>
              )}

              {filtered.map((opt) => {
                const val = String(opt[valueKey]);
                const selected = val === value;

                return (
                  <div
                    key={val}
                    className={`px-3 py-2 cursor-pointer text-base transition
                      hover:bg-primary-100 
                      ${selected ? "bg-primary-50 font-semibold" : ""}`}
                    onClick={() => {
                      onChangeValue?.(val);
                      setOpen(false);
                      setQ("");
                    }}
                  >
                    {String(opt[labelKey])}
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </FormGroup>
  );
}
