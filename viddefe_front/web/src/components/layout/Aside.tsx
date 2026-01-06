import { Link, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import {
  IconDashboard,
  IconChurch,
  IconPeople,
  IconGroups,
  IconEvents,
  IconMenu,
  IconClose,
  IconLogout,
  IconWorship,
} from "../../components/icons";
import { FiHome, FiUsers } from "react-icons/fi";

// Estructura de menú con secciones para mejor organización
interface MenuItem {
  path: string;
  label: string;
  icon: React.ReactNode;
}

interface MenuSection {
  title: string;
  items: MenuItem[];
}

// Menú organizado por contexto: Iglesia vs Grupos
const menuSections: MenuSection[] = [
  {
    title: "",
    items: [
      { path: "/dashboard", label: "Dashboard", icon: <IconDashboard /> },
    ],
  },
  {
    title: "Mi Espacio",
    items: [
      { path: "/my-church", label: "Mi Iglesia", icon: <FiHome size={20} /> },
      { path: "/my-group", label: "Mi Grupo", icon: <FiUsers size={20} /> },
    ],
  },
  {
    title: "Iglesia",
    items: [
      { path: "/churches", label: "Iglesias Hijas", icon: <IconChurch /> },
      { path: "/worships", label: "Cultos", icon: <IconWorship /> },
      { path: "/events", label: "Eventos", icon: <IconEvents /> },
    ],
  },
  {
    title: "Comunidad",
    items: [
      { path: "/people", label: "Personas", icon: <IconPeople /> },
      { path: "/groups", label: "Grupos", icon: <IconGroups /> },
    ],
  },
];

// Flatten para móvil
const allMenuItems = menuSections.flatMap(section => section.items);

export default function Aside() {
  const location = useLocation();
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  // Detectar si estamos en móvil
  useEffect(() => {
    const checkMobile = () => {
      const mobile = window.innerWidth < 1024;
      setIsMobile(mobile);
      if (mobile) {
        setIsCollapsed(false); // En móvil no colapsar
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

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  const closeMobileMenu = () => {
    setMobileMenuOpen(false);
  };

  return (
    <>
      {/* NAVBAR PARA MÓVIL - SOLO VISIBLE EN MÓVIL */}
      {isMobile && (
        <nav className="lg:hidden fixed top-0 left-0 right-0 bg-white border-b border-primary-100 shadow-sm z-50">
          <div className="px-4 h-16 flex items-center justify-between">
            <div className="flex items-center">
              <button
                onClick={toggleMobileMenu}
                className="p-2 rounded-lg hover:bg-primary-50 text-primary-700"
                aria-label="Toggle menu"
              >
                {mobileMenuOpen ? <IconClose /> : <IconMenu />}
              </button>
              <h1 className="ml-3 text-xl font-bold text-primary-800">VIDDEFE</h1>
            </div>

            {/* Perfil/Usuario (opcional) */}
            <div className="flex items-center">
              <button className="p-2 rounded-full bg-primary-50 hover:bg-primary-100 text-primary-700">
                <span className="sr-only">Perfil</span>
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </button>
            </div>
          </div>

          {mobileMenuOpen && (
            <div className="absolute top-full left-0 right-0 bg-white border-t border-primary-100 shadow-lg">
              <ul className="py-2">
                {allMenuItems.map((item) => {
                  const isActive = location.pathname.startsWith(item.path);
                  return (
                    <li key={item.path}>
                      <Link
                        to={item.path}
                        onClick={closeMobileMenu}
                        className={`flex items-center px-6 py-4 border-l-4 transition-colors ${
                          isActive
                            ? "bg-primary-50 text-primary-800 border-primary-500 font-medium"
                            : "text-primary-700 hover:bg-primary-25 border-transparent"
                        }`}
                      >
                        <span className="mr-3 text-primary-700">{item.icon}</span>
                        <span>{item.label}</span>
                      </Link>
                    </li>
                  );
                })}
                
                <li className="border-t border-primary-100 mt-2 pt-2">
                  <button className="flex items-center w-full px-6 py-4 text-primary-700 hover:bg-primary-25">
                    <span className="mr-3">
                      <IconLogout />
                    </span>
                    <span>Cerrar Sesión</span>
                  </button>
                </li>
              </ul>
            </div>
          )}
        </nav>
      )}

      <aside
        className={`hidden lg:flex bg-white text-primary-900 flex-col h-screen shadow-sm shadow-primary-200
          transition-all duration-300 overflow-hidden border-r border-primary-50
          ${isCollapsed ? "w-20" : "w-72"}`}
      >
        {/* Header Desktop */}
        <div className="px-4 py-6 border-b border-primary-100 flex items-center justify-between min-h-[80px]">
          {!isCollapsed && (
            <h1 className="text-2xl font-bold tracking-wide whitespace-nowrap text-primary-800">
              VIDDEFE
            </h1>
          )}

          {/* Collapse button - Solo en desktop */}
          <button
            className="p-2 rounded-full hover:bg-primary-50 transition-colors border border-transparent"
            onClick={toggleCollapse}
            aria-label={isCollapsed ? "Expandir menú" : "Colapsar menú"}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="text-primary-700">
              {isCollapsed ? (
                <path d="M9 6l6 6-6 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              ) : (
                <path d="M15 6l-6 6 6 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              )}
            </svg>
          </button>
        </div>

        {/* Navegación Desktop */}
        <nav className="flex-1 py-4 overflow-y-auto">
          {menuSections.map((section, sectionIndex) => (
            <div key={sectionIndex} className={section.title ? "mt-4" : ""}>
              {/* Título de sección (solo si no está colapsado) */}
              {section.title && !isCollapsed && (
                <h3 className="px-6 mb-2 text-xs font-semibold text-primary-500 uppercase tracking-wider">
                  {section.title}
                </h3>
              )}
              {/* Separador visual cuando está colapsado */}
              {section.title && isCollapsed && (
                <div className="mx-4 my-2 border-t border-primary-100" />
              )}
              <ul className="space-y-1 px-2">
                {section.items.map((item) => {
                  const isActive = location.pathname.startsWith(item.path);

                  return (
                    <li key={item.path} className="relative">
                      <Link
                        to={item.path}
                        className={`flex items-center px-4 py-3 rounded-lg mx-2 transition-all duration-200 group
                          ${
                            isActive
                              ? "bg-primary-50 text-primary-800 font-medium border-l-4 border-primary-500"
                              : "text-primary-700 hover:bg-primary-25 hover:text-primary-800"
                          }
                        `}
                        title={isCollapsed ? item.label : ""}
                      >
                        <span className={`flex justify-center text-primary-800 ${isCollapsed ? "mx-auto" : ""}`}>
                          {item.icon}
                        </span>
                        {!isCollapsed && (
                          <span className="ml-3 whitespace-nowrap overflow-hidden">
                            {item.label}
                          </span>
                        )}
                        {isCollapsed && isActive && (
                          <div className="absolute left-0 w-1 h-6 bg-primary-500 rounded-r-full" />
                        )}
                      </Link>
                    </li>
                  );
                })}
              </ul>
            </div>
          ))}
        </nav>

        {/* Footer con botón de logout - Desktop */}
        <div className="px-4 py-6 border-t border-primary-100">
          {!isCollapsed ? (
            <button className="w-full py-3 text-primary-800 rounded-lg transition-all duration-300 bg-primary-50 hover:bg-primary-100 border border-primary-100 cursor-pointer flex items-center gap-2 justify-center">
              <IconLogout />
              Cerrar Sesión
            </button>
          ) : (
            <button
              className="w-full py-3 flex justify-center hover:bg-primary-50 rounded-lg transition-colors"
              title="Cerrar Sesión"
            >
              <IconLogout />
            </button>
          )}
        </div>
      </aside>

      {isMobile && <div className="h-16 lg:hidden" />}
    </>
  );
}