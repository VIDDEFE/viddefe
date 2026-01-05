import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { HiChevronDown } from "react-icons/hi";
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
  disabled?: boolean;
  // Props para paginación infinita
  hasMore?: boolean;
  isLoadingMore?: boolean;
  onLoadMore?: () => void;
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
  disabled = false,
  // Paginación infinita
  hasMore = false,
  isLoadingMore = false,
  onLoadMore,
}: Readonly<DropDownProps>) {
  const searchField = searchKey ?? labelKey;

  const [open, setOpen] = useState(false);
  const [q, setQ] = useState("");

  const wrapperRef = useRef<HTMLDivElement>(null);
  const listRef = useRef<HTMLDivElement>(null);
  const loadMoreTriggerRef = useRef<HTMLDivElement>(null);

  // Filter options by search
  const filtered = useMemo(() => {
    if (!q.trim()) return options;
    const term = q.toLowerCase();
    return options.filter((opt) =>
      String(opt[searchField] ?? "").toLowerCase().includes(term)
    );
  }, [options, q, searchField]);

  // Close on outside click
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

  // Intersection Observer para cargar más cuando se acerca al final
  useEffect(() => {
    if (!onLoadMore || !hasMore || isLoadingMore) return;

    const observer = new IntersectionObserver(
      (entries) => {
        const [entry] = entries;
        if (entry.isIntersecting && hasMore && !isLoadingMore) {
          onLoadMore();
        }
      },
      {
        root: listRef.current,
        rootMargin: "100px",
        threshold: 0.1,
      }
    );

    const trigger = loadMoreTriggerRef.current;
    if (trigger) {
      observer.observe(trigger);
    }

    return () => {
      if (trigger) {
        observer.unobserve(trigger);
      }
    };
  }, [hasMore, isLoadingMore, onLoadMore, open]);

  const currentLabel =
    options.find((o) => String(o[valueKey]) === String(value))?.[labelKey] ??
    placeholder;

  const handleScroll = useCallback(() => {
    if (!onLoadMore || !hasMore || isLoadingMore || !listRef.current) return;
    
    const { scrollTop, scrollHeight, clientHeight } = listRef.current;
    // Cargar más cuando está a 100px del final
    if (scrollHeight - scrollTop - clientHeight < 100) {
      onLoadMore();
    }
  }, [hasMore, isLoadingMore, onLoadMore]);

  return (
    <FormGroup label={label} error={error}>
      <div ref={wrapperRef} className="relative w-full">
        {/* Trigger */}
        <button
          type="button"
          disabled={disabled}
          onClick={() => {
            if (!disabled) setOpen((o) => !o);
          }}
          className={`px-3 py-3 w-full text-left border-2 rounded-lg 
            text-base transition-all duration-300 font-inherit bg-white
            flex items-center justify-between
            ${disabled
              ? "bg-neutral-100 border-neutral-200 text-neutral-400 cursor-not-allowed"
              : "border-neutral-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-300 cursor-pointer"
            }
            ${className}`}
        >
          <span className="truncate">{currentLabel}</span>

          <HiChevronDown
            className={`ml-2 text-xl transition-transform duration-200
              ${open ? "rotate-180" : "rotate-0"}
              ${disabled ? "text-neutral-400" : "text-neutral-500"}
            `}
          />
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
            <div 
              ref={listRef}
              className="max-h-60 overflow-auto custom-scrollbar"
              onScroll={handleScroll}
            >
              {filtered.length === 0 && !isLoadingMore && (
                <div className="px-3 py-3 text-neutral-500 text-sm">
                  No results
                </div>
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

              {/* Loading indicator para paginación */}
              {isLoadingMore && (
                <div className="px-3 py-3 flex items-center justify-center gap-2 text-neutral-500 text-sm">
                  <div className="w-4 h-4 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
                  Cargando más...
                </div>
              )}

              {/* Trigger invisible para intersection observer */}
              {hasMore && !isLoadingMore && (
                <div ref={loadMoreTriggerRef} className="h-1" />
              )}
            </div>
          </div>
        )}
      </div>
    </FormGroup>
  );
}
