import { Link, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";

const menuItems = [
  { path: "/dashboard", label: "Dashboard", icon: "📊" },
  { path: "/churches", label: "Iglesias", icon: "⛪" },
  { path: "/people", label: "Personas", icon: "👥" },
  { path: "/services", label: "Servicios", icon: "🙏" },
  { path: "/groups", label: "Grupos", icon: "👫" },
  { path: "/events", label: "Eventos", icon: "📅" },
];

export default function Aside() {
  const location = useLocation();
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(false);

  // Detectar si estamos en móvil
  useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 1024);
      // En móvil, forzar que no esté colapsado
      if (window.innerWidth < 1024) {
        setIsCollapsed(false);
      }
    };

    checkMobile();
    window.addEventListener("resize", checkMobile);
    return () => window.removeEventListener("resize", checkMobile);
  }, []);

  const toggleCollapse = () => {
    if (!isMobile) {
      setIsCollapsed(!isCollapsed);
    }
  };

  const toggleMobile = () => {
    setMobileOpen(!mobileOpen);
  };

  const closeMobileMenu = () => {
    if (isMobile) {
      setMobileOpen(false);
    }
  };

  // Determinar las clases del aside
  const getAsideClasses = () => {
    if (isMobile) {
      return mobileOpen
        ? "translate-x-0"
        : "-translate-x-full lg:translate-x-0";
    } else {
      return isCollapsed ? "w-20" : "w-72";
    }
  };

  return (
    <>
      {/* Mobile toggle button - Solo visible en móvil */}
      <button
        className={`lg:hidden fixed top-4 left-4 z-50 bg-primary-700 text-white p-3 rounded-md shadow-lg transition-all duration-300 ${
          mobileOpen ? "left-64" : "left-4"
        }`}
        onClick={toggleMobile}
        aria-label="Toggle menu"
      >
        {mobileOpen ? "✖" : "☰"}
      </button>

      {/* Overlay para móvil */}
      {mobileOpen && isMobile && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-30 lg:hidden"
          onClick={closeMobileMenu}
        />
      )}

      <aside
        className={`fixed lg:relative bg-primary-700 text-white flex flex-col h-screen shadow-xl z-40
          transition-all duration-300 overflow-hidden
          ${getAsideClasses()}
          ${isMobile ? "w-72" : ""}`}
      >
        {/* Header */}
        <div className="px-4 py-6 border-b border-white/10 flex items-center justify-between min-h-[80px]">
          {(!isCollapsed || isMobile) && (
            <h1 className="text-2xl font-bold tracking-wide whitespace-nowrap">
              VIDDEFE
            </h1>
          )}

          {/* Collapse button - Solo visible en desktop */}
          {!isMobile && (
            <button
              className="p-2 rounded-full hover:bg-white/10 transition-colors"
              onClick={toggleCollapse}
              aria-label={isCollapsed ? "Expandir menú" : "Colapsar menú"}
            >
              {isCollapsed ? "➡" : "⬅"}
            </button>
          )}
        </div>

        <nav className="flex-1 py-4 overflow-y-auto">
          <ul className="space-y-1 px-2">
            {menuItems.map((item) => {
              const isActive = location.pathname.startsWith(item.path);

              return (
                <li key={item.path}>
                  <Link
                    to={item.path}
                    onClick={closeMobileMenu}
                    className={`flex items-center px-4 py-3 rounded-lg mx-2 transition-all duration-200 group
                      ${
                        isActive
                          ? "bg-white/20 text-white font-medium"
                          : "text-white/80 hover:bg-white/10 hover:text-white"
                      }
                    `}
                    title={isCollapsed && !isMobile ? item.label : ""}
                  >
                    <span className="text-xl min-w-[40px] flex justify-center">
                      {item.icon}
                    </span>
                    {(!isCollapsed || isMobile) && (
                      <span className="whitespace-nowrap overflow-hidden">
                        {item.label}
                      </span>
                    )}
                    {isCollapsed && !isMobile && isActive && (
                      <div className="absolute left-0 w-1 h-6 bg-white rounded-r-full" />
                    )}
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>

        {/* Footer con botón de logout */}
        <div className="px-4 py-6 border-t border-white/10">
          {(!isCollapsed || isMobile) && (
            <button className="w-full py-3 text-white rounded-lg transition-all duration-300 bg-primary-800 hover:bg-primary-900 cursor-pointer">
              Cerrar Sesión
            </button>
          )}
          {isCollapsed && !isMobile && (
            <button
              className="w-full py-3 flex justify-center"
              title="Cerrar Sesión"
            >
              <span className="text-xl">🚪</span>
            </button>
          )}
        </div>
      </aside>
    </>
  );
}